package controllers;

import beans.Module;
import services.ModuleService;
import exceptions.ServiceException;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class ModuleController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private ModuleService moduleService;
    private Module nouveauModule;
    private Module moduleSelectionne;
    private List<Module> modules;
    
    public ModuleController() {
        this.moduleService = new ModuleService();
        this.nouveauModule = new Module();
    }
    
    public void chargerModules() {
        try {
            this.modules = moduleService.getAllModules();
            System.out.println("✅ Modules chargés: " + modules.size());
        } catch (ServiceException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur: " + e.getMessage(), null));
        }
    }
    
    public void ajouterModule() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            // petite validation avant d'ajouter
            if (nouveauModule.getCode() == null || nouveauModule.getCode().trim().isEmpty()) {
                throw new ServiceException("Le code du module est obligatoire");
            }
            if (nouveauModule.getNom() == null || nouveauModule.getNom().trim().isEmpty()) {
                throw new ServiceException("Le nom du module est obligatoire");
            }
            if (nouveauModule.getCoefficient() <= 0) {
                throw new ServiceException("Le coefficient doit être supérieur à 0");
            }
            
            moduleService.ajouterModule(nouveauModule);
            this.nouveauModule = new Module();
            chargerModules();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "✅ Module ajouté avec succès", null));
        } catch (ServiceException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "❌ Erreur: " + e.getMessage(), null));
        }
    }
    
    public void preparerModification(Module module) {
        this.moduleSelectionne = module;
        // je copie les valeurs pour l'édition
        this.nouveauModule.setId(module.getId());
        this.nouveauModule.setCode(module.getCode());
        this.nouveauModule.setNom(module.getNom());
        this.nouveauModule.setCoefficient(module.getCoefficient());
        System.out.println("✅ Préparation modification module: " + module.getCode());
    }
    
    public void modifierModule() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (moduleSelectionne == null) {
                throw new ServiceException("Aucun module sélectionné");
            }
            
            // mise à jour
            moduleSelectionne.setCode(nouveauModule.getCode());
            moduleSelectionne.setNom(nouveauModule.getNom());
            moduleSelectionne.setCoefficient(nouveauModule.getCoefficient());
            
            moduleService.modifierModule(moduleSelectionne);
            
            // réinitialisation
            this.moduleSelectionne = null;
            this.nouveauModule = new Module();
            chargerModules();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "✅ Module modifié avec succès", null));
        } catch (ServiceException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "❌ Erreur: " + e.getMessage(), null));
        }
    }
    
    public void annulerModification() {
        this.moduleSelectionne = null;
        this.nouveauModule = new Module();
        System.out.println("✅ Modification annulée");
    }
    
    public void supprimerModule(int id) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            moduleService.supprimerModule(id);
            chargerModules();
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "✅ Module supprimé avec succès", null));
        } catch (ServiceException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "❌ Erreur: " + e.getMessage(), null));
        }
    }
    
    // pour les statistiques du dashboard
    public int getNombreModules() {
        try {
            if (modules == null) {
                chargerModules();
            }
            return modules != null ? modules.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    // getters et setters
    public Module getNouveauModule() { return nouveauModule; }
    public void setNouveauModule(Module nouveauModule) { this.nouveauModule = nouveauModule; }
    
    public Module getModuleSelectionne() { return moduleSelectionne; }
    public void setModuleSelectionne(Module moduleSelectionne) { this.moduleSelectionne = moduleSelectionne; }
    
    public List<Module> getModules() { 
        if (modules == null) {
            chargerModules();
        }
        return modules; 
    }
    public void setModules(List<Module> modules) { this.modules = modules; }
}