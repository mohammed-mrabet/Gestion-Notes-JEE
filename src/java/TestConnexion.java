import utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

public class TestConnexion {
    public static void main(String[] args) {
        System.out.println("=== TEST DE CONNEXION À LA BASE DE DONNÉES ===");
        
        try {
            // Tester la création de l'EntityManagerFactory
            System.out.println("1. Tentative de création EntityManagerFactory...");
            var emf = JpaUtil.getEntityManagerFactory();
            System.out.println("   ✅ EntityManagerFactory créée");
            
            // Tester la création d'un EntityManager
            System.out.println("2. Tentative de création EntityManager...");
            EntityManager em = emf.createEntityManager();
            System.out.println("   ✅ EntityManager créé");
            
            // Tester une requête simple
            System.out.println("3. Exécution d'une requête simple...");
            Query query = em.createNativeQuery("SELECT COUNT(*) FROM utilisateurs");
            Long count = (Long) query.getSingleResult();
            System.out.println("   ✅ Requête exécutée. Nombre d'utilisateurs: " + count);
            
            // Lister les utilisateurs
            System.out.println("4. Récupération des utilisateurs...");
            List<Object[]> utilisateurs = em.createNativeQuery(
                "SELECT id, login, role, LEFT(motDePasse, 30) as hash_debut FROM utilisateurs")
                .getResultList();
            
            System.out.println("   📋 Liste des utilisateurs:");
            for (Object[] u : utilisateurs) {
                System.out.println("   - ID: " + u[0] + 
                                 ", Login: " + u[1] + 
                                 ", Rôle: " + u[2] + 
                                 ", Hash: " + u[3] + "...");
            }
            
            em.close();
            System.out.println("\n✅ TEST RÉUSSI - Connexion OK !");
            
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR DE CONNEXION:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "inconnue"));
            e.printStackTrace();
        } finally {
            JpaUtil.close();
        }
    }
}