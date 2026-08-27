package services;

import beans.Inscription;
import beans.Module;
import beans.Professeur;
import dao.implementations.InscriptionDAO;
import dao.implementations.ModuleDAO;
import dao.implementations.ProfesseurDAO;
import dao.interfaces.IInscriptionDAO;
import dao.interfaces.IModuleDAO;
import dao.interfaces.IProfesseurDAO;
import exceptions.DAOException;
import exceptions.ServiceException;
import exceptions.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AffectationService {
    
    private final IModuleDAO moduleDAO;
    private final IProfesseurDAO professeurDAO;
    private final IInscriptionDAO inscriptionDAO;
    
    public AffectationService() {
        this.moduleDAO = new ModuleDAO();
        this.professeurDAO = new ProfesseurDAO();
        this.inscriptionDAO = new InscriptionDAO();
    }
    
    /**
     * affecter un professeur à un module
     */
    public void affecterProfesseurModule(int idProfesseur, int idModule) 
            throws ServiceException, ValidationException {
        
        try {
            Module module = moduleDAO.findById(idModule);
            if (module == null) {
                throw new ValidationException("Module introuvable");
            }
            
            Professeur professeur = professeurDAO.findById(idProfesseur);
            if (professeur == null) {
                throw new ValidationException("Professeur introuvable");
            }
            
            // si le module a déjà un prof, on remplace (c'est comme ça)
            if (module.getProfesseur() != null) {
                System.out.println("⚠️ Le module avait déjà un professeur, remplacement...");
            }
            
            module.setProfesseur(professeur);
            moduleDAO.update(module);
            
            System.out.println("✅ Professeur " + idProfesseur + " affecté au module " + idModule);
            
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de l'affectation", e);
        }
    }
    
    /**
     * récupérer tous les professeurs avec leurs modules
     */
    public Map<Professeur, List<Module>> getProfesseursAvecModules() throws ServiceException {
        try {
            List<Professeur> tousProfesseurs = professeurDAO.findAll();
            List<Module> tousModules = moduleDAO.findAll();
            
            Map<Professeur, List<Module>> resultat = new HashMap<>();
            
            for (Professeur p : tousProfesseurs) {
                List<Module> modulesDuProf = new ArrayList<>();
                for (Module m : tousModules) {
                    if (m.getProfesseur() != null && m.getProfesseur().getId() == p.getId()) {
                        modulesDuProf.add(m);
                    }
                }
                resultat.put(p, modulesDuProf);
            }
            
            return resultat;
            
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des professeurs avec modules", e);
        }
    }
    
    /**
     * retirer un professeur d'un module
     */
    public void retirerProfesseurModule(int idModule) throws ServiceException {
        try {
            Module module = moduleDAO.findById(idModule);
            if (module != null && module.getProfesseur() != null) {
                System.out.println("Retrait du professeur " + module.getProfesseur().getNom() + " du module " + module.getNom());
                module.setProfesseur(null);
                moduleDAO.update(module);
                System.out.println("✅ Professeur retiré du module " + idModule);
            } else {
                System.out.println("⚠️ Module " + idModule + " n'a pas de professeur");
            }
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors du retrait", e);
        }
    }
    
    /**
     * récupérer tous les modules avec leur professeur (pour l'affichage)
     */
    public List<Map<String, Object>> getModulesAvecProfesseurs() throws ServiceException {
        try {
            List<Module> modules = moduleDAO.findAll();
            List<Map<String, Object>> resultats = new ArrayList<>();
            
            for (Module m : modules) {
                Map<String, Object> item = new HashMap<>();
                item.put("module", m);
                item.put("professeur", m.getProfesseur());
                resultats.add(item);
            }
            
            return resultats;
            
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des affectations", e);
        }
    }
    
    // ========== méthodes pour les inscriptions ==========
    
    /**
     * inscrire un étudiant à un module
     */
    public void inscrireEtudiantModule(int idEtudiant, int idModule, String annee) 
            throws ServiceException, ValidationException {
        
        try {
            if (inscriptionDAO.exists(idEtudiant, idModule, annee)) {
                throw new ValidationException("Cet étudiant est déjà inscrit à ce module pour l'année " + annee);
            }
            
            inscriptionDAO.create(idEtudiant, idModule, annee);
            System.out.println("✅ Étudiant " + idEtudiant + " inscrit au module " + idModule);
            
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de l'inscription", e);
        }
    }
    
    /**
     * désinscrire un étudiant
     */
    public void desinscrireEtudiant(int idInscription) throws ServiceException {
        try {
            inscriptionDAO.delete(idInscription);
            System.out.println("✅ Inscription " + idInscription + " supprimée");
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la désinscription", e);
        }
    }
    
    /**
     * récupérer toutes les inscriptions
     */
    public List<Inscription> getAllInscriptions() throws ServiceException {
        try {
            return inscriptionDAO.findAll();
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des inscriptions", e);
        }
    }
    
    /**
     * récupérer les inscriptions par étudiant
     */
    public List<Inscription> getInscriptionsByEtudiant(int idEtudiant) throws ServiceException {
        try {
            return inscriptionDAO.findByEtudiant(idEtudiant);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des inscriptions de l'étudiant", e);
        }
    }
    
    /**
     * récupérer les inscriptions par module
     */
    public List<Inscription> getInscriptionsByModule(int idModule) throws ServiceException {
        try {
            return inscriptionDAO.findByModule(idModule);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des inscriptions du module", e);
        }
    }
}