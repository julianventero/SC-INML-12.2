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
import modelo.Usuario;

/**
 *
 * @author julia
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "SC-INML-12.2PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }
    
    public Usuario login( Usuario datos_login){
        return (Usuario) em.createNativeQuery("select cedula, contrasena from Usuario u where u.cedula = ?1 and u.contrasena = ?2", Usuario.class).setParameter(1, datos_login.getCedula()).setParameter(2, datos_login.getContrasena()).getSingleResult();
    }
    
}
