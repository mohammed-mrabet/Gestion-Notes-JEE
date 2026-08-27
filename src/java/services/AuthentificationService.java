package services;

import beans.Utilisateur;
import dao.implementations.UtilisateurDAO;
import dao.interfaces.IUtilisateurDAO;
import exceptions.DAOException;
import exceptions.ServiceException;

public class AuthentificationService {
    
    private final IUtilisateurDAO utilisateurDAO;
    
    public AuthentificationService() {
        System.out.println("🔧 [AuthentificationService] Constructeur appelé");
        this.utilisateurDAO = new UtilisateurDAO();
        System.out.println("✅ UtilisateurDAO initialisé");
    }
    
    public Utilisateur authentifier(String login, String motDePasse) throws ServiceException {
        System.out.println("\n=== [AuthentificationService.authentifier] ===");
        System.out.println("Login reçu: '" + login + "'");
        System.out.println("Mot de passe reçu: '" + motDePasse + "'");
        
        try {
            System.out.println("Appel de utilisateurDAO.authentifier...");
            Utilisateur utilisateur = utilisateurDAO.authentifier(login, motDePasse);
            
            if (utilisateur != null) {
                System.out.println("✅ Authentification réussie pour: " + login);
                System.out.println("   Rôle: " + utilisateur.getRole());
            } else {
                System.out.println("❌ Échec authentification pour: " + login);
            }
            
            return utilisateur;
        } catch (DAOException e) {
            System.err.println("❌ DAOException dans authentifier:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "inconnue"));
            throw new ServiceException("Erreur authentification: " + e.getMessage(), e);
        } finally {
            System.out.println("=== FIN AuthentificationService.authentifier ===\n");
        }
    }
    
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.trim();
    }
}