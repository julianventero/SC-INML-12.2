/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fachada;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import modelo.EncuestaPreguntas;

/**
 *
 * @author julia
 */
@Stateless
public class EncuestaPreguntasFacade extends AbstractFacade<EncuestaPreguntas> {

    @PersistenceContext(unitName = "SC-INML-12.2PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EncuestaPreguntasFacade() {
        super(EncuestaPreguntas.class);
    }
    
    public List<EncuestaPreguntas> verPreguntas(int id_encuesta){
    return em.createNativeQuery("select * from ENCUESTA_PREGUNTAS where ENCUESTA_idENCUESTA = ?1", EncuestaPreguntas.class).setParameter(1, id_encuesta).getResultList();
    }
    
    public EncuestaPreguntas traerIdEncuestaPreguntas(int id_encuesta,String id_pregunta){
    return (EncuestaPreguntas) em.createNativeQuery("select * from ENCUESTA_PREGUNTAS where ENCUESTA_idENCUESTA = ?1 and PREGUNTAS_idPREGUNTAS = ?2", EncuestaPreguntas.class).setParameter(1, id_encuesta).setParameter(2, id_pregunta).getSingleResult();
    }
}
