package utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JpaUtil {
    
    private static final String PERSISTENCE_UNIT_NAME = "BE2PU";
    private static EntityManagerFactory emf;
    
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            System.out.println("\n=== [JpaUtil] Création de l'EntityManagerFactory ===");
            System.out.println("Unité de persistence: " + PERSISTENCE_UNIT_NAME);
            
            try {
                Map<String, String> props = new HashMap<>();
                props.put("eclipselink.logging.level", "FINE");
                props.put("eclipselink.logging.level.sql", "FINE");
                props.put("eclipselink.logging.parameters", "true");
                
                System.out.println("Tentative de connexion...");
                System.out.println("  - Driver: com.mysql.cj.jdbc.Driver");
                System.out.println("  - URL: jdbc:mysql://localhost:3306/bd_be2?useSSL=false&serverTimezone=UTC");
                System.out.println("  - User: admin_m2i");
                
                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
                
                System.out.println("✅ EntityManagerFactory créée !");
                
                EntityManager em = emf.createEntityManager();
                System.out.println("✅ EntityManager créé !");
                em.close();
                
            } catch (Exception e) {
                System.err.println("❌ ERREUR lors de la création de l'EntityManagerFactory !");
                System.err.println("   Message: " + e.getMessage());
                e.printStackTrace();
                
                throw new RuntimeException("Erreur de création de l'EntityManagerFactory", e);
            } finally {
                System.out.println("=== FIN JpaUtil ===\n");
            }
        }
        return emf;
    }
    
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("✅ EntityManagerFactory fermée");
        }
    }
}