package services;

import beans.Etudiant;
import beans.Note;
import dao.implementations.NoteDAO;
import dao.implementations.UtilisateurDAO;
import dao.interfaces.INoteDAO;
import dao.interfaces.IUtilisateurDAO;
import exceptions.DAOException;
import exceptions.ServiceException;
import exceptions.ValidationException;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class NoteService {
    
    private final INoteDAO noteDAO;
    private final IUtilisateurDAO utilisateurDAO;
    
    public NoteService() {
        this.noteDAO = new NoteDAO();
        this.utilisateurDAO = new UtilisateurDAO();
    }
    
    public List<Note> getNotesByEtudiant(int idEtudiant) throws ServiceException {
        try {
            return noteDAO.findByEtudiant(idEtudiant);
        } catch (DAOException e) {
            throw new ServiceException("Erreur récupération notes", e);
        }
    }
    
    public List<Note> getNotesByMatiere(int idMatiere) throws ServiceException {
        try {
            List<Note> notes = noteDAO.findByMatiere(idMatiere);
            System.out.println("📊 NoteService: " + notes.size() + " notes trouvées pour matière " + idMatiere);
            return notes;
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException("Erreur récupération notes matière", e);
        }
    }
    
    public List<Etudiant> getEtudiantsByModule(int idModule) throws ServiceException {
        try {
            System.out.println("🔍 NoteService.getEtudiantsByModule: " + idModule);
            
            List<Etudiant> etudiants = noteDAO.findEtudiantsByModuleDirect(idModule);
            
            System.out.println("📊 Résultat: " + (etudiants != null ? etudiants.size() : 0) + " étudiants");
            
            if (etudiants != null) {
                for (Etudiant e : etudiants) {
                    System.out.println("   - " + e.getPrenom() + " " + e.getNom());
                }
            }
            
            return etudiants;
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException("Erreur récupération étudiants", e);
        }
    }
    
public void enregistrerNotes(List<Note> notes) throws ServiceException, ValidationException {
    System.out.println("📝 NoteService.enregistrerNotes: " + notes.size() + " notes");
    
    for (Note note : notes) {
        if (note.getNote() < 0 || note.getNote() > 20) {
            throw new ValidationException("Note invalide: " + note.getNote());
        }
    }
    
    try {
        for (Note note : notes) {
            // Vérifier si la note existe déjà
            Note existing = noteDAO.findByEtudiantAndMatiere(
                note.getEtudiant().getId(), 
                note.getModule().getId(), 
                note.getSession()
            );
            
            if (existing != null) {
                // Mise à jour
                existing.setNote(note.getNote());
                if (note.getProfesseur() != null) {
                    existing.setProfesseur(note.getProfesseur());
                }
                noteDAO.update(existing);
                System.out.println("   ✅ Note mise à jour ID: " + existing.getId() + " = " + existing.getNote());
            } else {
                // Création
                noteDAO.create(note);
                System.out.println("   ✅ Nouvelle note créée pour étudiant " + note.getEtudiant().getId() + " = " + note.getNote());
            }
        }
    } catch (DAOException e) {
        e.printStackTrace();
        throw new ServiceException("Erreur enregistrement notes", e);
    }
}    
    public double calculerMoyenneEtudiant(int idEtudiant) throws ServiceException {
        try {
            return noteDAO.calculerMoyenneEtudiant(idEtudiant);
        } catch (DAOException e) {
            throw new ServiceException("Erreur calcul moyenne", e);
        }
    }
    
    /**
 * Récupère une note spécifique par étudiant, module et session
 */
public Note getNoteByEtudiantAndModule(int idEtudiant, int idModule, String session) throws ServiceException {
    try {
        return noteDAO.findByEtudiantAndMatiere(idEtudiant, idModule, session);
    } catch (DAOException e) {
        throw new ServiceException("Erreur lors de la récupération de la note", e);
    }
}
    /**
     * récupérer toutes les notes saisies par un professeur
     */
    public List<Note> getNotesByProfesseur(int idProfesseur) throws ServiceException {
        try {
            return noteDAO.findByProfesseur(idProfesseur);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des notes du professeur", e);
        }
    }
    
    public Map<String, Double> getStatistiquesGlobales() throws ServiceException {
        try {
            Map<String, Double> stats = new HashMap<>();
            List<Note> toutesNotes = noteDAO.findAll();
            
            if (toutesNotes.isEmpty()) {
                stats.put("moyenneGenerale", 0.0);
                stats.put("nombreNotes", 0.0);
                stats.put("noteMin", 0.0);
                stats.put("noteMax", 0.0);
                return stats;
            }
            
            double somme = 0;
            double noteMin = 20;
            double noteMax = 0;
            
            for (Note n : toutesNotes) {
                double note = n.getNote();
                somme += note;
                noteMin = Math.min(noteMin, note);
                noteMax = Math.max(noteMax, note);
            }
            
            stats.put("moyenneGenerale", Math.round((somme / toutesNotes.size()) * 100.0) / 100.0);
            stats.put("nombreNotes", (double) toutesNotes.size());
            stats.put("noteMin", noteMin);
            stats.put("noteMax", noteMax);
            
            return stats;
        } catch (DAOException e) {
            throw new ServiceException("Erreur calcul statistiques", e);
        }
    }
}