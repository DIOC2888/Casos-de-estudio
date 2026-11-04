package org.example.proyectohibernate.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.proyectohibernate.database.JPAUtil;
import org.example.proyectohibernate.model.Libro;

import java.util.List;

import static org.example.proyectohibernate.database.JPAUtil.getEntityManagerFactory;

public class LibroDAO {
    public void guardar(Libro libro){
        EntityManager em = getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            //Iniciar la transaccion en PostgreSQL
            tx.begin();
            //Decirle a Hibernate que prepare la persistencia del objeto libro
            em.persist(libro);
            //Confirmar los cambios en la base de datos
            tx.commit();
        }catch(Exception e){
           if(tx.isActive()){
               tx.rollback();
           }
           throw e;
        }finally{
            em.close();
        }
    }


    public List<Libro> listar(){
        EntityManager em = getEntityManagerFactory().createEntityManager();
        try{
            return em.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();
        }finally {
            em.close();
        }
}
}
