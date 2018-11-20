package controlador;


import com.lowagie.text.pdf.PdfWriter;
import controlador.util.JsfUtil;
import controlador.util.JsfUtil.PersistAction;
import fachada.EncuestaFacade;
import fachada.MuestraFacade;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import modelo.Cliente;
import modelo.Encuesta;
import modelo.EncuestaPreguntas;
import modelo.Muestra;
import modelo.Municipio;
import modelo.ParametrosMedicion;
import modelo.Respuesta;
import modelo.Seccional;
import modelo.ServicioForense;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.util.JRFontNotFoundException;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.annotation.PostConstruct;
import static jxl.biff.BaseCellFeatures.logger;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;









@Named("muestraController")
@SessionScoped
public class MuestraController implements Serializable {

    /**
     * @return the fecha_inicio
     */
    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    /**
     * @param fecha_inicio the fecha_inicio to set
     */
    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    /**
     * @return the fecha_fin
     */
    public Date getFecha_fin() {
        return fecha_fin;
    }

    /**
     * @param fecha_fin the fecha_fin to set
     */
    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    /**
     * @return the municipio
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * @param municipio the municipio to set
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    /**
     * @return the servicio_forense
     */
    public String getServicio_forense() {
        return servicio_forense;
    }

    /**
     * @param servicio_forense the servicio_forense to set
     */
    public void setServicio_forense(String servicio_forense) {
        this.servicio_forense = servicio_forense;
    }

    @EJB
    private fachada.MuestraFacade ejbFacade;
    @EJB //EJB para mandar los datos al facade de Respuesta y ser guardados
    private fachada.RespuestaFacade ejbRespuestaFacade;
    @EJB//EJB para tarer el nombre de la encuesta de acuerdo al servico forense seleccionado
    private fachada.EncuestaFacade ejbFacadeEncuesta;
    private List<Muestra> items = null;
    //este atributo es para cuando registran una muestra estadistica
    private Muestra selected;
    //este atributo es para cuando van a registrar las respuestas de la encuesta, almacenar el id de la encuesta y poder traer las preguntas
    private Muestra selected2;
    private Encuesta Encuesta;
    private ServicioForense servicio;
    private List<Encuesta> encuestas;
    private String idPregunta;
    //Atributo para traer el id Encuesta_Preguntas de cada pregunta que seleccionen y guardarlo en Respuestas
    private EncuestaPreguntas ItemsidEncuestaPreguntas;
    
    
    //Atributos para almacenar los datos del modelo respuesta(Fecha realizacion encuesta,Cliente,Seccional) y se guarden
    private Cliente idcliente;
    private Date fecharealizacion;
    private Seccional idseccional;
    private Municipio idmunicipio;
    private ParametrosMedicion parametromedicion;//este atributo guarda el parametro de medición que seleccionen
    //Atributo de tipo respuesta, para poder almacenar los datos del modelo respuesta (Fecha realizacion encuesta,Cliente,Seccional)
    private Respuesta selectedr;

    //EJB PARA TRAERME LAS PREGUNTAS RESPECTO A LA ENCUESTA QUE SELECCIONEN
    @EJB
    private fachada.EncuestaPreguntasFacade ejbFacadeEncuestaPreguntas;
    private List<EncuestaPreguntas> ItemsEncuestaPreguntas;
    private EncuestaPreguntas selectedEncuestaPreguntas;
    //EJB PARA TRAER LOS PARAMETROS DE MEDICION RESPECTO A LAS PREGUNTAS
    @EJB
    private fachada.ParametrosMedicionFacade ejbFacadeParametrosMedicionFacade;
    private List<ParametrosMedicion> ItemsParametrosMedicion;
    private ParametrosMedicion selectedParametrosMedicion;

    //Atributos para guardar los datos del informe estadistico
    private Date fecha_inicio;
    private Date fecha_fin;
    private String municipio;
    private String servicio_forense;
    
    
    //Metodo para traer el ID de la tabla EncuestaPreguntas,de cada pregunta que seleccionen en la tabla
    public EncuestaPreguntas getItemsidEncuestaPreguntas() {
        try{
        ItemsidEncuestaPreguntas=ejbFacadeEncuestaPreguntas.traerIdEncuestaPreguntas(Encuesta.getIdENCUESTA(), idPregunta);
        //System.out.println(""+ItemsidEncuestaPreguntas);
        }
        catch(Exception e){
        }
        return ItemsidEncuestaPreguntas;
    }

    //METODOS DEL EJB PARA TRAERME LAS PREGUNTAS RESPECTO A LA ENCUESTA QUE SELECCIONEN
    public fachada.EncuestaPreguntasFacade getEjbFacadeEncuestaPreguntas() {
        return ejbFacadeEncuestaPreguntas;
    }
    
    public List<EncuestaPreguntas> getItemsEncuestaPreguntas() {
        if (ItemsEncuestaPreguntas == null) {
            try{
            ItemsEncuestaPreguntas = ejbFacadeEncuestaPreguntas.verPreguntas(Encuesta.getIdENCUESTA());
            }
            catch (Exception e) {

        }
        }
        return ItemsEncuestaPreguntas;
    }
    
    public EncuestaPreguntas getSelectedEncuestaPreguntas() {
        return selectedEncuestaPreguntas;
    }
    
    //METODOS DEL EJB PARA TRAER LOS PARAMETROS DE MEDICION RESPECTO A LA PREGUNTA QUE SELECCIONEN
    public fachada.ParametrosMedicionFacade getEjbFacadeParametrosMedicionFacade() {
        return ejbFacadeParametrosMedicionFacade;
    }

    public Cliente getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(Cliente idcliente) {
        this.idcliente = idcliente;
    }

    public Date getFecharealizacion() {
        return fecharealizacion;
    }

    public void setFecharealizacion(Date fecharealizacion) {
        this.fecharealizacion = fecharealizacion;
    }

    public Seccional getIdseccional() {
        return idseccional;
    }

    public void setIdseccional(Seccional idseccional) {
        this.idseccional = idseccional;
    }
    
    public Municipio getIdmunicipio() {
        return idmunicipio;
    }

    public void setIdmunicipio(Municipio idmunicipio) {
        this.idmunicipio = idmunicipio;
    }
    
    
    public String getIdPregunta() {
        return idPregunta;
    }
    
    public ParametrosMedicion getParametromedicion() {
        return parametromedicion;
    }

    public void setParametromedicion(ParametrosMedicion parametromedicion) {
        this.parametromedicion = parametromedicion;
    }
    
    public Respuesta getSelectedr() {
        return selectedr;
    }
    public void setSelectedr(Respuesta selectedr) {
        try{
            this.selectedr = selectedr;
        }
        catch(Exception e){
        }
    }
    
    public void setIdPregunta(String idPregunta) {
        this.idPregunta = idPregunta;
        getItemsParametrosMedicion();
    }

    public List<ParametrosMedicion> getItemsParametrosMedicion() {

        try {
            ItemsParametrosMedicion = ejbFacadeParametrosMedicionFacade.parametrosXpregunta(idPregunta);
        } catch (Exception e) {

        }

        return ItemsParametrosMedicion;
    }

    public ParametrosMedicion getSelectedParametrosMedicion() {
        return selectedParametrosMedicion;
    }

    public MuestraController() {
    }

    public Muestra getSelected() {
        return selected;
    }

    public void setSelected(Muestra selected) {
        this.selected = selected;
    }
    
    //metodos set y get de selected2, para cuando registran 
    public Muestra getSelected2() {
        return selected2;
    }

    public void setSelected2(Muestra selected2) {
        this.selected2 = selected2;
    }
    
    
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MuestraFacade getFacade() {
        return ejbFacade;
    }

    public EncuestaFacade getEjbFacadeEncuesta() {
        return ejbFacadeEncuesta;
    }

    public Muestra prepareCreate() {
        selected = new Muestra();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("Created"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("Updated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("Deleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Muestra> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
    //Mando al facade el servicio forense seleccionado, para traer el nombre de la encuesta
    public List<Encuesta> getEncuestas() {
        if (servicio != null) {
            encuestas = ejbFacadeEncuesta.buscarXServicio(servicio.getIdSERVICIOFORENSE().toString());
        }
        return encuestas;
    }

    public void setEncuestas(List<Encuesta> encuestas) {
        this.encuestas = encuestas;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Muestra getMuestra(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Muestra> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Muestra> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public ServicioForense getServicio() {
        return servicio;
    }

    public void setServicio(ServicioForense servicio) {
        this.servicio = servicio;
    }

    @FacesConverter(forClass = Muestra.class)
    public static class MuestraControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MuestraController controller = (MuestraController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "muestraController");
            return controller.getMuestra(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Muestra) {
                Muestra o = (Muestra) object;
                return getStringKey(o.getIdMUESTRA());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Muestra.class.getName()});
                return null;
            }
        }

    }

    public void datosRespuesta(){
        addMessage("Creado correctamente");
       // System.out.println(""+fecharealizacion);
       // System.out.println(""+idseccional);
        //System.out.println(""+idcliente);
        //System.out.println(""+idmunicipio);
    }
    
    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
       
    public void guardarRespuesta() {
        
       Respuesta a=new Respuesta();
       a.setCLIENTEidCLIENTE(idcliente);
       a.setSECCIONALidSECCIONAL(idseccional);
       a.setPARAMETROSMEDICIONidPARAMETROSMEDICION(parametromedicion);
       a.setMUNICIPIOidMUNICIPIO(idmunicipio);
       a.setFechaRealizacion(fecharealizacion);
       a.setENCUESTAPREGUNTASidENCUESTAPREGUNTAS(getItemsidEncuestaPreguntas());
       ejbRespuestaFacade.create(a);
       
       
    }

    public Encuesta getEncuesta() {
        return Encuesta;
    }

    public void setEncuesta(Encuesta Encuesta) {
        this.Encuesta = Encuesta;
    }

    
    
    public void creareporte (){
    
    FacesContext fcontext = FacesContext.getCurrentInstance();
    Connection conexion = null;
    String nombreReporte ="reporte4";
    try{
        HttpServletResponse response = null;
        String tipoSalida = "pdf";
        String nrd = "";
        //se establece la conexión
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("java:app/ee");
        conexion = ds.getConnection();
        HashMap parametros = new HashMap();
        parametros = agregarParametrosCD(parametros);
          if("reporte4".equals(nombreReporte)){
            tipoSalida = "pdf";
              if(fecha_inicio != null){
                  parametros.put("fecha_inicio",fecha_inicio);
              }
              if(fecha_fin !=null){
                   parametros.put("fecha_fin",fecha_fin);
              }
              if(municipio !=null){
                  parametros.put("municipio",municipio);
              }
              if(servicio_forense !=null){
                  parametros.put("servicio_forense",servicio_forense);
              }
          }
          
          ServletContext theApplicationsServeletContext =(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
          String rutaArchivo = theApplicationsServeletContext.getRealPath("/reportes") + File.separator;
          String reporte =rutaArchivo + nombreReporte + nrd + ".jasper";
          parametros.put("SUBREPORT_DIR",rutaArchivo);
          JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, conexion);
          ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
          response = (HttpServletResponse) ectx.getResponse();
        
          response.setHeader("content-disposition", "attachment;filename=" + nombreReporte + "." + tipoSalida);
          if(tipoSalida.equals("pdf")){
             JRPdfExporter exporter = new JRPdfExporter();
             response.setContentType("application/pdf");
             exporter.setParameter(JRPdfExporterParameter.OUTPUT_FILE_NAME, nombreReporte + "." + tipoSalida);
             exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT, jasperPrint);
             exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, response.getOutputStream());
             exporter.setParameter(JRPdfExporterParameter.METADATA_AUTHOR, "ESTUDIANTES IST");
             exporter.setParameter(JRPdfExporterParameter.PERMISSIONS, new Integer(PdfWriter.ALLOW_PRINTING | PdfWriter.HideToolbar | PdfWriter.HideToolbar));
             exporter.setParameter(JRPdfExporterParameter.OWNER_PASSWORD, "");
             exporter.setParameter(JRPdfExporterParameter.IS_128_BIT_KEY, Boolean.TRUE);
             exporter.exportReport();
             
          }
          fcontext.responseComplete();

    }
    catch (FileNotFoundException ex){
        JsfUtil.addErrorMessage(ex, "Error al momento de imprimir, reporte no encontrado");
    }
    catch (NamingException | SQLException | JRException |IOException ex){
        JsfUtil.addErrorMessage(ex, "Error al momento de imprimir ");
    }
    catch (JRFontNotFoundException ex){
        JsfUtil.addErrorMessage(ex,"Fuente no encontrada. " );
    }
    catch (Exception ex){
        System.out.println(ex);
        JsfUtil.addErrorMessage(ex, "Error al momento de imprimir. ");
    }
    finally{
        try {
            if(conexion != null){
                conexion.close();
            }
        }catch(SQLException ex){
        }catch(Exception ex){
        }
    }

} 

    private HashMap agregarParametrosCD(HashMap parametros) {
        SimpleDateFormat sdfy = new SimpleDateFormat ("dd/MM/yyyy");
        SimpleDateFormat sdfx = new SimpleDateFormat ("dd 'de' MMMM 'de' yyyy", new Locale("es"));
        parametros.put("fechaHoraElabora", sdfy.format(new Date()));
        parametros.put("TituloReporte", "XXXXXXXXXXX");
        return parametros;
    }

    
    }
