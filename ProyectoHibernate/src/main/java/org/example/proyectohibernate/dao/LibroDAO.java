package org.example.proyectohibernate.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.proyectohibernate.database.JPAUtil;
import org.example.proyectohibernate.model.Libro;

import java.util.List;

import static org.example.proyectohibernate.database.JPAUtil.getEntityManager;

public class LibroDAO {
    public void guardar(Libro libro){
        EntityManager em = getEntityManager().createEntityManager();
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
        EntityManager em = JPAUtil.getEntityManager().createEntityManager();
        try{
            return em.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();
        }finally {
            em.close();
        }
    }

    public boolean modificar(Libro libro){
        EntityManager em = getEntityManager().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();
            Libro libroBD = em.find(Libro.class, libro.getId());
            if(libroBD == null){
                tx.rollback();
                return false;
            }
            libroBD.setTitulo(libro.getTitulo());
            libroBD.setAutor(libro.getAutor());
            libroBD.setAnio(libro.getAnio());
            libroBD.setPrecio(libro.getPrecio());
            tx.commit();
            return true;
        } catch (RuntimeException e){
            if(tx.isActive()){
                tx.rollback();
            }
            throw e;
        } finally{
            em.close();
        }
    }

    public boolean eliminar(Long id){
        EntityManager em = JPAUtil.getEntityManager().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();
            Libro libroBD = em.find(Libro.class, id);
            if(libroBD == null){
               tx.rollback();
               return false;
            }
            em.remove(libroBD);
            tx.commit();
            return true;
        } catch (RuntimeException e){
            if(tx.isActive()){
                tx.rollback();
            }
            throw e;
        } finally{
            em.close();
        }
    }
}
