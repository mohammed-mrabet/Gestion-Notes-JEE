package controllers;

import beans.Etudiant;
import beans.Module;
import beans.Professeur;
import beans.Utilisateur;
import services.ProfesseurService;
import services.NoteService;
import exceptions.ServiceException;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named
@SessionScoped
public class ProfesseurController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private AuthentificationController authController;
    
    private final ProfesseurService professeurService;
    private final NoteService noteService;
    
    private List<Module> modules;
    private List<Etudiant> etudiants;
    private String filtreModule;
    private String filtreClasse;
    
    // liste pour les étudiants avec leurs moyennes
    private List<EtudiantAvecMoyenne> etudiantsAvecMoyennes;
    
    // classe interne pour stocker étudiant + moyenne
    public static class EtudiantAvecMoyenne {
        private Etudiant etudiant;
        private double moyenne;
        
        public EtudiantAvecMoyenne(Etudiant etudiant, double moyenne) {
            this.etudiant = etudiant;
            this.moyenne = moyenne;
        }
        
        public Etudiant getEtudiant() { return etudiant; }
        public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }
        
        public double getMoyenne() { return moyenne; }
        public void setMoyenne(double moyenne) { this.moyenne = moyenne; }
    }
    
    public ProfesseurController() {
        this.professeurService = new ProfesseurService();
        this.noteService = new NoteService();
        System.out.println("✅ ProfesseurController initialisé");
    }
    
    @PostConstruct
    public void init() {
        System.out.println("🔍 @PostConstruct: Initialisation du contrôleur professeur");
        chargerModules();
        chargerTousEtudiants();
    }
    
    public void chargerModules() {
        if (authController != null && authController.isProfesseur()) {
            try {
                int idProfesseur = authController.getUtilisateurCourant().getId();
                System.out.println("🔍 Chargement des modules pour professeur ID: " + idProfesseur);
                
                this.modules = professeurService.getModulesByProfesseur(idProfesseur);
                
                System.out.println("✅ Modules chargés pour professeur: " + modules.size());
                for (Module m : modules) {
                    System.out.println("   - " + m.getCode() + ": " + m.getNom());
                }
            } catch (ServiceException e) {
                System.err.println("❌ Erreur: " + e.getMessage());
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Erreur: " + e.getMessage(), null));
            }
        } else {
            System.out.println("⚠️ AuthController null ou utilisateur non professeur");
        }
    }
    
    public void chargerTousEtudiants() {
        try {
            this.etudiants = professeurService.getAllEtudiants();
            System.out.println("✅ Étudiants chargés: " + etudiants.size());
            for (Etudiant e : etudiants) {
                System.out.println("   - " + e.getPrenom() + " " + e.getNom() + " (" + e.getCne() + ")");
            }
        } catch (ServiceException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur: " + e.getMessage(), null));
        }
    }
    
    // récupérer les étudiants d'un module spécifique
    public List<Etudiant> getEtudiantsByModule(int idModule) {
        try {
            System.out.println("🔍 Recherche des étudiants pour le module ID: " + idModule);
            List<Etudiant> etudiantsModule = professeurService.getEtudiantsByModule(idModule);
            System.out.println("📊 Étudiants trouvés: " + etudiantsModule.size());
            return etudiantsModule;
        } catch (ServiceException e) {
            System.err.println("❌ Erreur getEtudiantsByModule: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // charger les étudiants avec leurs moyennes
    public void chargerEtudiantsAvecMoyennes() {
        try {
            if (etudiants == null) {
                chargerTousEtudiants();
            }
            
            this.etudiantsAvecMoyennes = new ArrayList<>();
            
            for (Etudiant e : etudiants) {
                double moyenne;
                
                if (filtreModule != null && !filtreModule.isEmpty()) {
                    // moyenne pour un module spécifique
                    moyenne = professeurService.calculerMoyenneEtudiantParModule(
                        e.getId(), Integer.parseInt(filtreModule));
                } else {
                    // moyenne générale
                    moyenne = professeurService.calculerMoyenneEtudiant(e.getId());
                }
                
                // appliquer le filtre de classe
                if (filtreClasse == null || filtreClasse.isEmpty() || filtreClasse.equals(e.getClasse())) {
                    etudiantsAvecMoyennes.add(new EtudiantAvecMoyenne(e, moyenne));
                }
            }
            
            System.out.println("✅ Étudiants avec moyennes chargés: " + etudiantsAvecMoyennes.size());
            
        } catch (ServiceException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur: " + e.getMessage(), null));
        }
    }
    
    // méthode de filtrage
    public void filtrerEtudiants() {
        chargerEtudiantsAvecMoyennes();
    }
    
    // réinitialiser les filtres
    public void reinitialiserFiltres() {
        this.filtreModule = null;
        this.filtreClasse = null;
        this.etudiantsAvecMoyennes = null;
        chargerEtudiantsAvecMoyennes();
    }
    
    public int getNombreModules() {
        if (modules == null || modules.isEmpty()) {
            chargerModules();
        }
        return modules != null ? modules.size() : 0;
    }
    
    public int getNombreEtudiants() {
        try {
            return professeurService.getNombreEtudiants();
        } catch (ServiceException e) {
            return 0;
        }
    }
    
    public int getNombreEtudiantsParModule(int moduleId) {
        try {
            return professeurService.getNombreEtudiantsParModule(moduleId);
        } catch (ServiceException e) {
            System.err.println("❌ Erreur getNombreEtudiantsParModule: " + e.getMessage());
            return 0;
        }
    }
    
    public List<Etudiant> getEtudiants() {
        if (etudiants == null || etudiants.isEmpty()) {
            chargerTousEtudiants();
        }
        return etudiants;
    }
    
    // getters et setters
    public List<Module> getModules() {
        if (modules == null) {
            if (authController != null && authController.isProfesseur()) {
                chargerModules();
            } else {
                System.out.println("⚠️ getModules: authController null ou utilisateur non professeur");
                modules = new ArrayList<>();
            }
        }
        return modules;
    }
    
    public void setModules(List<Module> modules) {
        this.modules = modules;
    }
    
    public List<EtudiantAvecMoyenne> getEtudiantsAvecMoyennes() {
        if (etudiantsAvecMoyennes == null) {
            chargerEtudiantsAvecMoyennes();
        }
        return etudiantsAvecMoyennes;
    }
    
    public void setEtudiantsAvecMoyennes(List<EtudiantAvecMoyenne> etudiantsAvecMoyennes) {
        this.etudiantsAvecMoyennes = etudiantsAvecMoyennes;
    }
    
    public String getFiltreModule() {
        return filtreModule;
    }
    
    public void setFiltreModule(String filtreModule) {
        this.filtreModule = filtreModule;
    }
    
    public String getFiltreClasse() {
        return filtreClasse;
    }
    
    public void setFiltreClasse(String filtreClasse) {
        this.filtreClasse = filtreClasse;
    }
}