package controllers;

import beans.Utilisateur;
import services.AuthentificationService;
import exceptions.ServiceException;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class AuthentificationController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final AuthentificationService authService;
    
    private String login;
    private String motDePasse;
    private Utilisateur utilisateurCourant;
    private boolean authentifie;
    
    public AuthentificationController() {
        this.authService = new AuthentificationService();
        this.authentifie = false;
    }
    
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {
            System.out.println("\n=== [AuthentificationController.login] ===");
            System.out.println("Login saisi: '" + login + "'");
            System.out.println("Mot de passe saisi: '" + motDePasse + "'");
            
            String loginNettoye = AuthentificationService.sanitizeInput(login);
            Utilisateur utilisateur = authService.authentifier(loginNettoye, motDePasse);
            
            if (utilisateur != null) {
                this.utilisateurCourant = utilisateur;
                this.authentifie = true;
                
                System.out.println("✅ Connexion réussie pour: " + utilisateur.getLogin());
                System.out.println("   Rôle: " + utilisateur.getRole());
                System.out.println("   Type réel: " + utilisateur.getClass().getSimpleName());
                
                // j'efface les champs pour la sécurité
                this.login = null;
                this.motDePasse = null;
                
                // redirection selon le rôle
                String redirection = "";
                switch (utilisateur.getRole()) {
                    case ADMIN:
                        redirection = "/views/admin/dashboard?faces-redirect=true";
                        System.out.println("➡️ Redirection vers dashboard ADMIN");
                        break;
                    case PROFESSEUR:
                        redirection = "/views/professeur/dashboard?faces-redirect=true";
                        System.out.println("➡️ Redirection vers dashboard PROFESSEUR");
                        break;
                    case ETUDIANT:
                        redirection = "/views/etudiant/dashboard?faces-redirect=true";
                        System.out.println("➡️ Redirection vers dashboard ETUDIANT");
                        break;
                }
                
                return redirection;
                
            } else {
                System.out.println("❌ Échec connexion - identifiants incorrects");
                context.addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Login ou mot de passe incorrect", null));
                return null;
            }
            
        } catch (ServiceException e) {
            System.err.println("❌ ServiceException: " + e.getMessage());
            e.printStackTrace();
            context.addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur d'authentification: " + e.getMessage(), null));
            return null;
        }
    }
    
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        this.authentifie = false;
        this.utilisateurCourant = null;
        return "/views/login?faces-redirect=true";
    }
    
    public boolean isAdmin() {
        return utilisateurCourant != null && 
               (utilisateurCourant.getRole() == Utilisateur.Role.ADMIN || 
                utilisateurCourant instanceof beans.Administrateur);
    }
    
    public boolean isProfesseur() {
        return utilisateurCourant != null && 
               (utilisateurCourant.getRole() == Utilisateur.Role.PROFESSEUR || 
                utilisateurCourant instanceof beans.Professeur);
    }
    
    public boolean isEtudiant() {
        return utilisateurCourant != null && 
               (utilisateurCourant.getRole() == Utilisateur.Role.ETUDIANT || 
                utilisateurCourant instanceof beans.Etudiant);
    }
    
    // getters et setters
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    
    public Utilisateur getUtilisateurCourant() { 
        return utilisateurCourant; 
    }
    
    public void setUtilisateurCourant(Utilisateur utilisateurCourant) { 
        this.utilisateurCourant = utilisateurCourant; 
    }
    
    public boolean isAuthentifie() { return authentifie; }
    public void setAuthentifie(boolean authentifie) { this.authentifie = authentifie; }
}