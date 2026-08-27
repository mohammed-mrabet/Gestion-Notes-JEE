package controllers;

import beans.Administrateur;
import beans.Etudiant;
import beans.Professeur;
import beans.Utilisateur;
import services.UtilisateurService;
import exceptions.ServiceException;
import exceptions.ValidationException;
import utils.PasswordUtils;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named
@SessionScoped
public class UtilisateurController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private AuthentificationController authController;
    
    private final UtilisateurService utilisateurService;
    
    private List<Utilisateur> utilisateurs;
    private Utilisateur utilisateurSelectionne;
    
    // champs pour le formulaire
    private String nom;
    private String prenom;
    private String email;
    private String login;
    private String motDePasse;
    private String role;
    
    // champs spécifiques étudiant
    private String cne;
    private LocalDate dateNaissance;
    private String adresse;
    private String classe;
    
    // champs spécifiques professeur
    private String specialite;
    private String grade;
    
    // champs spécifiques admin
    private String fonction;
    
    public UtilisateurController() {
        this.utilisateurService = new UtilisateurService();
        System.out.println("✅ UtilisateurController initialisé");
    }
    
    public void chargerUtilisateurs() {
        try {
            this.utilisateurs = utilisateurService.getAllUtilisateurs();
            System.out.println("✅ " + utilisateurs.size() + " utilisateurs chargés");
        } catch (ServiceException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur: " + e.getMessage(), null));
        }
    }
    
    public String ajouterUtilisateur() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {
            System.out.println("\n=== AJOUT D'UN NOUVEL UTILISATEUR ===");
            System.out.println("Rôle: " + role);
            System.out.println("Login: " + login);
            System.out.println("Nom: " + nom + " " + prenom);
            System.out.println("Email: " + email);
            
            // validation des champs obligatoires
            if (role == null || role.trim().isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Veuillez sélectionner un rôle", null));
                return null;
            }
            if (nom == null || nom.trim().isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Le nom est obligatoire", null));
                return null;
            }
            if (prenom == null || prenom.trim().isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Le prénom est obligatoire", null));
                return null;
            }
            if (email == null || email.trim().isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "L'email est obligatoire", null));
                return null;
            }
            if (login == null || login.trim().isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Le login est obligatoire", null));
                return null;
            }
            if (motDePasse == null || motDePasse.trim().isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Le mot de passe est obligatoire", null));
                return null;
            }
            
            // validation selon le rôle
            if (role.equals("ETUDIANT")) {
                if (cne == null || cne.trim().isEmpty()) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Le CNE est obligatoire pour un étudiant", null));
                    return null;
                }
            }
            
            Utilisateur utilisateur = null;
            String hashedPassword = PasswordUtils.hashPassword(motDePasse);
            
            switch (role) {
                case "ADMIN":
                    Administrateur admin = new Administrateur();
                    admin.setNom(nom);
                    admin.setPrenom(prenom);
                    admin.setEmail(email);
                    admin.setLogin(login);
                    admin.setMotDePasse(hashedPassword);
                    admin.setRole(Utilisateur.Role.ADMIN);
                    admin.setFonction(fonction != null ? fonction : "Administrateur");
                    utilisateur = admin;
                    System.out.println("✅ Administrateur créé");
                    break;
                    
                case "PROFESSEUR":
                    Professeur prof = new Professeur();
                    prof.setNom(nom);
                    prof.setPrenom(prenom);
                    prof.setEmail(email);
                    prof.setLogin(login);
                    prof.setMotDePasse(hashedPassword);
                    prof.setRole(Utilisateur.Role.PROFESSEUR);
                    prof.setSpecialite(specialite != null ? specialite : "Informatique");
                    prof.setGrade(grade != null ? grade : "Professeur");
                    utilisateur = prof;
                    System.out.println("✅ Professeur créé");
                    break;
                    
                case "ETUDIANT":
                    Etudiant etu = new Etudiant();
                    etu.setNom(nom);
                    etu.setPrenom(prenom);
                    etu.setEmail(email);
                    etu.setLogin(login);
                    etu.setMotDePasse(hashedPassword);
                    etu.setRole(Utilisateur.Role.ETUDIANT);
                    etu.setCne(cne);
                    etu.setDateNaissance(dateNaissance);
                    etu.setAdresse(adresse != null ? adresse : "");
                    etu.setClasse(classe != null ? classe : "");
                    utilisateur = etu;
                    System.out.println("✅ Étudiant créé");
                    break;
                    
                default:
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Rôle invalide: " + role, null));
                    return null;
            }
            
            // appel du service
            utilisateurService.ajouterUtilisateur(utilisateur);
            
            // réinitialiser le formulaire
            resetForm();
            
            // recharger la liste
            chargerUtilisateurs();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "✅ Utilisateur ajouté avec succès", null));
            
            System.out.println("✅ Utilisateur ajouté avec succès: " + login);
            
            return null; // reste sur la même page
            
        } catch (ServiceException | ValidationException e) {
            System.err.println("❌ Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "❌ Erreur: " + e.getMessage(), null));
            return null;
        }
    }
    
    // conversion de date
    public void setDateNaissanceAsString(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                this.dateNaissance = LocalDate.parse(dateStr);
            } catch (Exception e) {
                System.err.println("❌ Erreur conversion date: " + e.getMessage());
            }
        }
    }

    public String getDateNaissanceAsString() {
        return dateNaissance != null ? dateNaissance.toString() : "";
    }
    
    public void preparerModification(Utilisateur utilisateur) {
        this.utilisateurSelectionne = utilisateur;
        
        // remplir les champs communs
        this.nom = utilisateur.getNom();
        this.prenom = utilisateur.getPrenom();
        this.email = utilisateur.getEmail();
        this.login = utilisateur.getLogin();
        this.motDePasse = ""; // on ne montre pas le mot de passe
        this.role = utilisateur.getRole().toString();
        
        // réinitialiser les champs spécifiques
        this.cne = null;
        this.dateNaissance = null;
        this.adresse = null;
        this.classe = null;
        this.specialite = null;
        this.grade = null;
        this.fonction = null;
        
        // remplir selon le type
        if (utilisateur instanceof Etudiant) {
            Etudiant etu = (Etudiant) utilisateur;
            this.cne = etu.getCne();
            this.dateNaissance = etu.getDateNaissance();
            this.adresse = etu.getAdresse();
            this.classe = etu.getClasse();
            System.out.println("✅ Étudiant sélectionné: " + etu.getCne());
            
        } else if (utilisateur instanceof Professeur) {
            Professeur prof = (Professeur) utilisateur;
            this.specialite = prof.getSpecialite();
            this.grade = prof.getGrade();
            System.out.println("✅ Professeur sélectionné: " + prof.getSpecialite());
            
        } else if (utilisateur instanceof Administrateur) {
            Administrateur admin = (Administrateur) utilisateur;
            this.fonction = admin.getFonction();
            System.out.println("✅ Administrateur sélectionné: " + admin.getFonction());
        }
        
        System.out.println("✅ Préparation modification: " + utilisateur.getLogin());
    }
    
    public String modifierUtilisateur() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {
            System.out.println("\n=== MODIFICATION D'UN UTILISATEUR ===");
            
            if (utilisateurSelectionne == null) {
                throw new ValidationException("Aucun utilisateur sélectionné");
            }
            
            // mettre à jour les champs communs
            utilisateurSelectionne.setNom(nom);
            utilisateurSelectionne.setPrenom(prenom);
            utilisateurSelectionne.setEmail(email);
            
            // mettre à jour le mot de passe seulement s'il a été changé
            if (motDePasse != null && !motDePasse.trim().isEmpty()) {
                String hashedPassword = PasswordUtils.hashPassword(motDePasse);
                utilisateurSelectionne.setMotDePasse(hashedPassword);
            }
            
            // mettre à jour selon le type
            if (utilisateurSelectionne instanceof Etudiant) {
                Etudiant etu = (Etudiant) utilisateurSelectionne;
                etu.setCne(cne);
                etu.setDateNaissance(dateNaissance);
                etu.setAdresse(adresse);
                etu.setClasse(classe);
                System.out.println("✅ Mise à jour étudiant: " + cne);
                
            } else if (utilisateurSelectionne instanceof Professeur) {
                Professeur prof = (Professeur) utilisateurSelectionne;
                prof.setSpecialite(specialite);
                prof.setGrade(grade);
                System.out.println("✅ Mise à jour professeur: " + specialite);
                
            } else if (utilisateurSelectionne instanceof Administrateur) {
                Administrateur admin = (Administrateur) utilisateurSelectionne;
                admin.setFonction(fonction);
                System.out.println("✅ Mise à jour admin: " + fonction);
            }
            
            utilisateurService.modifierUtilisateur(utilisateurSelectionne);
            chargerUtilisateurs();
            resetForm();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "✅ Utilisateur modifié avec succès", null));
            
            System.out.println("✅ Utilisateur modifié: " + login);
            
            return "/views/admin/gestionUtilisateurs?faces-redirect=true";
            
        } catch (ServiceException | ValidationException e) {
            System.err.println("❌ Erreur modification: " + e.getMessage());
            e.printStackTrace();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "❌ Erreur: " + e.getMessage(), null));
            return null;
        }
    }
    
    public void annulerModification() {
        this.utilisateurSelectionne = null;
        resetForm();
        System.out.println("✅ Modification annulée");
    }
    
    public void supprimerUtilisateur(int id) {
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {
            System.out.println("\n=== SUPPRESSION D'UN UTILISATEUR ===");
            System.out.println("ID à supprimer: " + id);
            
            // empêcher la suppression de son propre compte
            if (authController.getUtilisateurCourant() != null && 
                authController.getUtilisateurCourant().getId() == id) {
                
                System.out.println("⚠️ Tentative de suppression du compte courant");
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, 
                        "⚠️ Vous ne pouvez pas supprimer votre propre compte", null));
                return;
            }
            
            utilisateurService.supprimerUtilisateur(id);
            chargerUtilisateurs();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "✅ Utilisateur supprimé avec succès", null));
            
            System.out.println("✅ Utilisateur supprimé: ID " + id);
                
        } catch (ServiceException e) {
            System.err.println("❌ Erreur suppression: " + e.getMessage());
            e.printStackTrace();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "❌ Erreur: " + e.getMessage(), null));
        }
    }
    
    private void resetForm() {
        this.nom = null;
        this.prenom = null;
        this.email = null;
        this.login = null;
        this.motDePasse = null;
        this.role = null;
        this.cne = null;
        this.dateNaissance = null;
        this.adresse = null;
        this.classe = null;
        this.specialite = null;
        this.grade = null;
        this.fonction = null;
        this.utilisateurSelectionne = null;
    }
    
    // méthodes pour les statistiques
    public int getNombreUtilisateurs() {
        try {
            if (utilisateurs == null) {
                chargerUtilisateurs();
            }
            return utilisateurs != null ? utilisateurs.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    public int getNombreAdmins() {
        try {
            if (utilisateurs == null) {
                chargerUtilisateurs();
            }
            return (int) utilisateurs.stream()
                .filter(u -> u.getRole() == Utilisateur.Role.ADMIN)
                .count();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public int getNombreProfesseurs() {
        try {
            if (utilisateurs == null) {
                chargerUtilisateurs();
            }
            return (int) utilisateurs.stream()
                .filter(u -> u.getRole() == Utilisateur.Role.PROFESSEUR)
                .count();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public int getNombreEtudiants() {
        try {
            if (utilisateurs == null) {
                chargerUtilisateurs();
            }
            return (int) utilisateurs.stream()
                .filter(u -> u.getRole() == Utilisateur.Role.ETUDIANT)
                .count();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public List<Utilisateur> getDerniersUtilisateurs() {
        try {
            if (utilisateurs == null) {
                chargerUtilisateurs();
            }
            
            if (utilisateurs != null && !utilisateurs.isEmpty()) {
                return utilisateurs.stream()
                    .filter(u -> u.getDateInscription() != null)
                    .sorted((u1, u2) -> u2.getDateInscription().compareTo(u1.getDateInscription()))
                    .limit(5)
                    .collect(Collectors.toList());
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur dans getDerniersUtilisateurs: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // tous les getters et setters
    public List<Utilisateur> getUtilisateurs() {
        if (utilisateurs == null) {
            chargerUtilisateurs();
        }
        return utilisateurs;
    }
    
    public void setUtilisateurs(List<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }
    
    public Utilisateur getUtilisateurSelectionne() {
        return utilisateurSelectionne;
    }
    
    public void setUtilisateurSelectionne(Utilisateur utilisateurSelectionne) {
        this.utilisateurSelectionne = utilisateurSelectionne;
    }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    
    public String getRole() { return role; }
    public void setRole(String role) { 
        this.role = role;
        System.out.println("🔹 Rôle changé: " + role);
    }
    
    public String getCne() { return cne; }
    public void setCne(String cne) { this.cne = cne; }
    
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    
    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }
    
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public String getFonction() { return fonction; }
    public void setFonction(String fonction) { this.fonction = fonction; }
}