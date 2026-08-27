package services;

import beans.Etudiant;
import beans.Module;
import beans.Note;
import dao.implementations.InscriptionDAO;
import dao.implementations.ModuleDAO;
import dao.implementations.NoteDAO;
import dao.implementations.UtilisateurDAO;
import dao.interfaces.IInscriptionDAO;
import dao.interfaces.IModuleDAO;
import dao.interfaces.INoteDAO;
import dao.interfaces.IUtilisateurDAO;
import exceptions.DAOException;
import exceptions.ServiceException;

import java.util.List;
import java.util.stream.Collectors;

public class ProfesseurService {
    
    private final IModuleDAO moduleDAO;
    private final INoteDAO noteDAO;
    private final IUtilisateurDAO utilisateurDAO;
    private final IInscriptionDAO inscriptionDAO;
    
    public ProfesseurService() {
        this.moduleDAO = new ModuleDAO();
        this.noteDAO = new NoteDAO();
        this.utilisateurDAO = new UtilisateurDAO();
        this.inscriptionDAO = new InscriptionDAO();
    }
    
    /**
     * récupérer tous les modules enseignés par un professeur
     */
    public List<Module> getModulesByProfesseur(int idProfesseur) throws ServiceException {
        try {
            List<Module> tousModules = moduleDAO.findAll();
            
            List<Module> modulesProf = tousModules.stream()
                .filter(module -> module.getProfesseur() != null && module.getProfesseur().getId() == idProfesseur)
                .collect(Collectors.toList());
            
            System.out.println("📚 Modules trouvés pour professeur " + idProfesseur + ": " + modulesProf.size());
            
            for (Module m : modulesProf) {
                System.out.println("   - " + m.getCode() + ": " + m.getNom());
            }
            
            return modulesProf;
                
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des modules du professeur", e);
        }
    }
    
    /**
     * récupérer tous les étudiants
     */
    public List<Etudiant> getAllEtudiants() throws ServiceException {
        try {
            return utilisateurDAO.findByRole("ETUDIANT").stream()
                .map(u -> (Etudiant) u)
                .collect(Collectors.toList());
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des étudiants", e);
        }
    }
    
    /**
     * nombre total d'étudiants
     */
    public int getNombreEtudiants() throws ServiceException {
        try {
            return utilisateurDAO.findByRole("ETUDIANT").size();
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors du comptage des étudiants", e);
        }
    }
    
    /**
     * nombre d'étudiants inscrits dans un module
     */
    public int getNombreEtudiantsParModule(int moduleId) throws ServiceException {
        try {
            List<Note> notes = noteDAO.findByMatiere(moduleId);
            return (int) notes.stream()
                .map(n -> n.getEtudiant().getId())
                .distinct()
                .count();
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors du comptage des étudiants par module", e);
        }
    }
    
    /**
     * récupérer les étudiants inscrits à un module spécifique
     */
    public List<Etudiant> getEtudiantsByModule(int idModule) throws ServiceException {
        try {
            System.out.println("🔍 ProfesseurService.getEtudiantsByModule: " + idModule);
            
            // d'abord via les inscriptions
            List<beans.Inscription> inscriptions = inscriptionDAO.findByModule(idModule);
            
            if (!inscriptions.isEmpty()) {
                System.out.println("📊 Étudiants trouvés via inscriptions: " + inscriptions.size());
                
                List<Etudiant> etudiants = inscriptions.stream()
                    .map(insc -> insc.getEtudiant())
                    .distinct()
                    .collect(Collectors.toList());
                
                for (Etudiant e : etudiants) {
                    System.out.println("   - " + e.getPrenom() + " " + e.getNom() + " (" + e.getCne() + ")");
                }
                
                return etudiants;
            }
            
            // sinon via les notes
            System.out.println("⚠️ Aucune inscription, recherche via notes...");
            List<Note> notes = noteDAO.findByMatiere(idModule);
            
            if (!notes.isEmpty()) {
                System.out.println("📊 Étudiants trouvés via notes: " + notes.size());
                return notes.stream()
                    .map(Note::getEtudiant)
                    .distinct()
                    .collect(Collectors.toList());
            }
            
            // si rien trouvé, tous les étudiants
            System.out.println("⚠️ Aucune inscription ni note, retour de tous les étudiants");
            return getAllEtudiants();
            
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des étudiants par module", e);
        }
    }
    
    // =====================================================
    // méthodes pour le calcul des moyennes
    // =====================================================
    
    /**
     * moyenne générale d'un étudiant
     */
    public double calculerMoyenneEtudiant(int idEtudiant) throws ServiceException {
        try {
            List<Note> notes = noteDAO.findByEtudiant(idEtudiant);
            
            if (notes.isEmpty()) {
                return 0.0;
            }
            
            double somme = 0;
            int totalCoeff = 0;
            
            for (Note note : notes) {
                somme += note.getNote() * note.getModule().getCoefficient();
                totalCoeff += note.getModule().getCoefficient();
            }
            
            double moyenne = totalCoeff > 0 ? somme / totalCoeff : 0;
            return Math.round(moyenne * 100.0) / 100.0;
            
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors du calcul de la moyenne de l'étudiant", e);
        }
    }
    
    /**
     * moyenne d'un étudiant pour un module spécifique
     */
    public double calculerMoyenneEtudiantParModule(int idEtudiant, int idModule) throws ServiceException {
        try {
            List<Note> notes = noteDAO.findByEtudiant(idEtudiant);
            
            if (notes.isEmpty()) {
                return 0.0;
            }
            
            List<Note> notesModule = notes.stream()
                .filter(note -> note.getModule().getId() == idModule)
                .collect(Collectors.toList());
            
            if (notesModule.isEmpty()) {
                return 0.0;
            }
            
            double somme = 0;
            int totalCoeff = 0;
            
            for (Note note : notesModule) {
                somme += note.getNote() * note.getModule().getCoefficient();
                totalCoeff += note.getModule().getCoefficient();
            }
            
            double moyenne = totalCoeff > 0 ? somme / totalCoeff : 0;
            return Math.round(moyenne * 100.0) / 100.0;
            
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors du calcul de la moyenne de l'étudiant par module", e);
        }
    }
    
    /**
     * moyenne d'un module (tous étudiants confondus)
     */
    public double calculerMoyenneModule(int idModule) throws ServiceException {
        try {
            List<Note> notes = noteDAO.findByMatiere(idModule);
            
            if (notes.isEmpty()) {
                return 0.0;
            }
            
            double somme = 0;
            for (Note note : notes) {
                somme += note.getNote();
            }
            
            double moyenne = somme / notes.size();
            return Math.round(moyenne * 100.0) / 100.0;
            
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors du calcul de la moyenne du module", e);
        }
    }
}