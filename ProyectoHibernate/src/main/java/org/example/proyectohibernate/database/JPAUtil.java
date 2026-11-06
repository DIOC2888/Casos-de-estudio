
package org.example.proyectohibernate.database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static EntityManagerFactory entityManagerFactory;

    public static synchronized void iniciar() {
        if (entityManagerFactory == null) {
            String password = System.getenv("DB_PASSWORD");

            if (password == null || password.isEmpty()) {
                throw new RuntimeException(
                        "DB_PASSWORD environment variable is not set or empty."
                );
            }

            Map<String, String> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.password", password);

            entityManagerFactory =
                    Persistence.createEntityManagerFactory(
                            "bibliotecaPU", properties
                    );
        }
    }

    public static EntityManagerFactory getEntityManager() {
        iniciar();
        return entityManagerFactory;
    }

    public static synchronized void cerrar() {
        if (entityManagerFactory != null &&
                entityManagerFactory.isOpen()) {

            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }
}
