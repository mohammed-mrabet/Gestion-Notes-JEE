package controllers;

import beans.Etudiant;
import beans.Inscription;
import beans.Module;
import beans.Professeur;
import beans.Utilisateur;
import services.AffectationService;
import services.UtilisateurService;
import services.ModuleService;
import exceptions.ServiceException;
import exceptions.ValidationException;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Named
@SessionScoped
public class AffectationController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // services
    private final AffectationService affectationService;
    private final UtilisateurService utilisateurService;
    private final ModuleService moduleService;
    
    // listes pour les menus déroulants
    private List<Professeur> professeurs;
    private List<Etudiant> etudiants;
    private List<Module> modules;
    private List<Map<String, Object>> affectationsProfesseurs;
    private List<Inscription> inscriptions;
    
    // variables pour les sélections
    private int idProfesseur;
    private int idEtudiant;
    private int idModule;
    private String annee = "2025-2026"; // année par défaut
    
    public AffectationController() {
        this.affectationService = new AffectationService();
        this.utilisateurService = new UtilisateurService();
        this.moduleService = new ModuleService();
        System.out.println("✅ AffectationController initialisé");
        
        // je charge tout au début
        chargerProfesseurs();
        chargerEtudiants();
        chargerModules();
        chargerAffectationsProfesseurs();
        chargerInscriptions();
    }
    
    @PostConstruct
    public void init() {
        // au cas où, je recharge
        chargerProfesseurs();
        chargerEtudiants();
        chargerModules();
        chargerAffectationsProfesseurs();
        chargerInscriptions();
        System.out.println("✅ Données chargées au démarrage");
    }
    
    public void chargerProfesseurs() {
        try {
            List<Utilisateur> users = utilisateurService.getUtilisateursByRole("PROFESSEUR");
            this.professeurs = new ArrayList<>();
            for (Utilisateur u : users) {
                if (u instanceof Professeur) {
                    professeurs.add((Professeur) u);
                }
            }
            System.out.println("✅ Professeurs chargés: " + professeurs.size());
        } catch (ServiceException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur chargement professeurs: " + e.getMessage(), null));
        }
    }
    
    public void chargerEtudiants() {
        try {
            List<Utilisateur> users = utilisateurService.getUtilisateursByRole("ETUDIANT");
            this.etudiants = new ArrayList<>();
            for (Utilisateur u : users) {
                if (u instanceof Etudiant) {
                    etudiants.add((Etudiant) u);
                }
            }
            System.out.println("✅ Étudiants chargés: " + etudiants.size());
            for (Etudiant e : etudiants) {
                System.out.println("   - ID: " + e.getId() + ", " + e.getPrenom() + " " + e.getNom() + " (" + e.getCne() + ")");
            }
        } catch (ServiceException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur chargement étudiants: " + e.getMessage(), null));
        }
    }
    
    public void chargerModules() {
        try {
            this.modules = moduleService.getAllModules();
            System.out.println("✅ Modules chargés: " + modules.size());
        } catch (ServiceException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur chargement modules: " + e.getMessage(), null));
        }
    }
    
    public void chargerAffectationsProfesseurs() {
        try {
            this.affectationsProfesseurs = new ArrayList<>();
            if (modules != null) {
                for (Module m : modules) {
                    if (m.getProfesseur() != null) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("module", m);
                        item.put("professeur", m.getProfesseur());
                        affectationsProfesseurs.add(item);
                    }
                }
            }
            System.out.println("✅ Affectations chargées: " + affectationsProfesseurs.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void chargerInscriptions() {
        try {
            this.inscriptions = affectationService.getAllInscriptions();
            System.out.println("✅ Inscriptions chargées: " + inscriptions.size());
        } catch (ServiceException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur chargement inscriptions: " + e.getMessage(), null));
        }
    }
    
    // juste pour tester, je garde cette méthode
    public void testerIds() {
        FacesContext context = FacesContext.getCurrentInstance();
        System.out.println("\n=== TEST DES IDs ===");
        System.out.println("idProfesseur: " + idProfesseur);
        System.out.println("idEtudiant: " + idEtudiant);
        System.out.println("idModule: " + idModule);
        System.out.println("annee: " + annee);
        
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
            "IDs - Prof: " + idProfesseur + ", Etud: " + idEtudiant + ", Module: " + idModule, null));
    }
    
    public String affecterProfesseurModule() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {
            System.out.println("Affectation - Professeur ID: " + idProfesseur + ", Module ID: " + idModule);
            
            if (idProfesseur <= 0) {
                throw new ValidationException("Veuillez sélectionner un professeur");
            }
            if (idModule <= 0) {
                throw new ValidationException("Veuillez sélectionner un module");
            }
            
            affectationService.affecterProfesseurModule(idProfesseur, idModule);
            
            // je réinitialise et recharge
            this.idProfesseur = 0;
            this.idModule = 0;
            chargerModules();
            chargerAffectationsProfesseurs();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "✅ Professeur affecté avec succès", null));
            
        } catch (ServiceException | ValidationException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "❌ Erreur: " + e.getMessage(), null));
        }
        
        return null;
    }
    
    public String retirerProfesseurModule(int idModule) {
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {
            affectationService.retirerProfesseurModule(idModule);
            chargerModules();
            chargerAffectationsProfesseurs();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "✅ Professeur retiré du module", null));
                
        } catch (ServiceException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "❌ Erreur: " + e.getMessage(), null));
        }
        
        return null;
    }
    
    public String inscrireEtudiantModule() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {
            System.out.println("Inscription - Étudiant ID: " + idEtudiant + ", Module ID: " + idModule + ", Année: " + annee);
            
            if (idEtudiant <= 0) {
                throw new ValidationException("Veuillez sélectionner un étudiant");
            }
            if (idModule <= 0) {
                throw new ValidationException("Veuillez sélectionner un module");
            }
            if (annee == null || annee.trim().isEmpty()) {
                throw new ValidationException("Veuillez saisir l'année");
            }
            
            affectationService.inscrireEtudiantModule(idEtudiant, idModule, annee);
            
            // réinitialisation et rechargement
            this.idEtudiant = 0;
            this.idModule = 0;
            chargerInscriptions();
            chargerEtudiants(); // important pour mettre à jour la liste
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "✅ Étudiant inscrit avec succès", null));
            
        } catch (ServiceException | ValidationException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "❌ Erreur: " + e.getMessage(), null));
        }
        
        return null;
    }
    
    public String desinscrireEtudiant(int idInscription) {
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {
            affectationService.desinscrireEtudiant(idInscription);
            chargerInscriptions();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "✅ Étudiant désinscrit", null));
                
        } catch (ServiceException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "❌ Erreur: " + e.getMessage(), null));
        }
        
        return null;
    }
    
    // méthodes pour générer les items des menus déroulants
    public List<SelectItem> getProfesseursItems() {
        List<SelectItem> items = new ArrayList<>();
        if (professeurs != null) {
            for (Professeur p : professeurs) {
                items.add(new SelectItem(p.getId(), 
                    p.getPrenom() + " " + p.getNom() + " - " + p.getSpecialite()));
            }
        }
        return items;
    }

    public List<SelectItem> getEtudiantsItems() {
        List<SelectItem> items = new ArrayList<>();
        if (etudiants != null) {
            for (Etudiant e : etudiants) {
                items.add(new SelectItem(e.getId(), 
                    e.getPrenom() + " " + e.getNom() + " - " + e.getCne() + " (" + e.getClasse() + ")"));
            }
        }
        return items;
    }

    public List<SelectItem> getModulesItems() {
        List<SelectItem> items = new ArrayList<>();
        if (modules != null) {
            for (Module m : modules) {
                items.add(new SelectItem(m.getId(), 
                    m.getCode() + " - " + m.getNom() + " (Coeff: " + m.getCoefficient() + ")"));
            }
        }
        return items;
    }
    
    // tous les getters et setters
    public List<Professeur> getProfesseurs() {
        if (professeurs == null || professeurs.isEmpty()) {
            chargerProfesseurs();
        }
        return professeurs;
    }
    
    public List<Etudiant> getEtudiants() {
        if (etudiants == null || etudiants.isEmpty()) {
            chargerEtudiants();
        }
        return etudiants;
    }
    
    public List<Module> getModules() {
        if (modules == null || modules.isEmpty()) {
            chargerModules();
        }
        return modules;
    }
    
    public List<Map<String, Object>> getAffectationsProfesseurs() {
        if (affectationsProfesseurs == null) {
            chargerAffectationsProfesseurs();
        }
        return affectationsProfesseurs;
    }
    
    public List<Inscription> getInscriptions() {
        if (inscriptions == null) {
            chargerInscriptions();
        }
        return inscriptions;
    }
    
    public int getIdProfesseur() { return idProfesseur; }
    public void setIdProfesseur(int idProfesseur) { this.idProfesseur = idProfesseur; }
    
    public int getIdEtudiant() { return idEtudiant; }
    public void setIdEtudiant(int idEtudiant) { this.idEtudiant = idEtudiant; }
    
    public int getIdModule() { return idModule; }
    public void setIdModule(int idModule) { this.idModule = idModule; }
    
    public String getAnnee() { return annee; }
    public void setAnnee(String annee) { this.annee = annee; }
}