package dao.implementations;

import beans.Utilisateur;
import dao.interfaces.IUtilisateurDAO;
import exceptions.DAOException;
import utils.JpaUtil;
import utils.PasswordUtils;
import jakarta.persistence.*;
import java.util.List;

public class UtilisateurDAO implements IUtilisateurDAO {
    
    // méthode de test que j'ai gardée
    public static void testConnexionDirecte() {
        System.out.println("\n=== TEST DE CONNEXION DIRECTE ===\n");
        
        String[][] tests = {
            {"admin", "admin123"},
            {"k.alami", "prof123"},
            {"y.cherkaoui", "etudiant123"}
        };
        
        UtilisateurDAO dao = new UtilisateurDAO();
        
        for (String[] test : tests) {
            String login = test[0];
            String password = test[1];
            
            System.out.println("Test: " + login + " / " + password);
            
            try {
                Utilisateur user = dao.authentifier(login, password);
                if (user != null) {
                    System.out.println("  ✅ CONNEXION RÉUSSIE !");
                    System.out.println("     → " + user.getPrenom() + " " + user.getNom() + 
                                     " (" + user.getRole() + ")");
                } else {
                    System.out.println("  ❌ ÉCHEC - Identifiants incorrects");
                }
            } catch (Exception e) {
                System.out.println("  ❌ ERREUR: " + e.getMessage());
            }
            System.out.println("---");
        }
    }

    public static void main(String[] args) {
        testConnexionDirecte();
    }
    
    @Override
    public Utilisateur authentifier(String login, String motDePasse) throws DAOException {
        System.out.println("\n=== [UtilisateurDAO.authentifier] ===");
        System.out.println("Login reçu: '" + login + "'");
        System.out.println("Mot de passe reçu: '" + motDePasse + "'");
        
        EntityManager em = null;
        try {
            em = JpaUtil.getEntityManager();
            System.out.println("✅ EntityManager créé");
            
            String jpql = "SELECT u FROM Utilisateur u WHERE u.login = :login";
            TypedQuery<Utilisateur> query = em.createQuery(jpql, Utilisateur.class);
            query.setParameter("login", login);
            
            System.out.println("Requête JPQL: " + jpql);
            System.out.println("Paramètre login: " + login);
            
            Utilisateur utilisateur = null;
            try {
                utilisateur = query.getSingleResult();
                System.out.println("✅ Utilisateur trouvé dans la BD:");
                System.out.println("   ID: " + utilisateur.getId());
                System.out.println("   Login: " + utilisateur.getLogin());
                System.out.println("   Rôle: " + utilisateur.getRole());
                System.out.println("   Hash en BD: " + utilisateur.getMotDePasse());
            } catch (NoResultException e) {
                System.out.println("❌ Aucun utilisateur trouvé avec le login: " + login);
                return null;
            }
            
            // vérification du mot de passe
            boolean passwordMatch = PasswordUtils.verifyPassword(motDePasse, utilisateur.getMotDePasse());
            System.out.println("Résultat vérification mot de passe: " + passwordMatch);
            
            if (passwordMatch) {
                System.out.println("✅ Authentification réussie pour: " + login);
                return utilisateur;
            } else {
                System.out.println("❌ Mot de passe incorrect pour: " + login);
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Exception dans authentifier:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "aucune"));
            e.printStackTrace();
            throw new DAOException("Erreur authentification: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                em.close();
                System.out.println("EntityManager fermé");
            }
            System.out.println("=== FIN UtilisateurDAO.authentifier ===\n");
        }
    }
    
    @Override
    public Utilisateur findById(int id) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Utilisateur.class, id);
        } catch (Exception e) {
            throw new DAOException("Erreur findById", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Utilisateur findByLogin(String login) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.login = :login", Utilisateur.class);
            query.setParameter("login", login);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new DAOException("Erreur findByLogin", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Utilisateur> findAll() throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Utilisateur u ORDER BY u.nom", Utilisateur.class)
                    .getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur findAll", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Utilisateur> findByRole(String role) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Utilisateur u WHERE u.role = :role", Utilisateur.class)
                    .setParameter("role", Utilisateur.Role.valueOf(role))
                    .getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur findByRole", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void create(Utilisateur utilisateur) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(utilisateur);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Erreur create", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void update(Utilisateur utilisateur) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(utilisateur);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Erreur update", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void delete(int id) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Utilisateur u = em.find(Utilisateur.class, id);
            if (u != null) em.remove(u);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Erreur delete", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean loginExists(String login) throws DAOException {
        return findByLogin(login) != null;
    }
    
    @Override
    public boolean emailExists(String email) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(u) FROM Utilisateur u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            throw new DAOException("Erreur emailExists", e);
        } finally {
            em.close();
        }
    }
}