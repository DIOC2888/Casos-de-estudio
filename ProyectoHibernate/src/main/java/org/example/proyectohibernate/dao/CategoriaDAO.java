package org.example.proyectohibernate.dao;

import jakarta.persistence.EntityManager;
import org.example.proyectohibernate.database.JPAUtil;
import org.example.proyectohibernate.model.Categoria;

import java.util.List;

public class CategoriaDAO {
    public List<Categoria> ListarCategorias() {
        EntityManager em = JPAUtil.getEntityManager().createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Categoria c", Categoria.class).getResultList();
        } finally {
            em.close();
        }

    }
}
