package controllers;

import beans.Etudiant;
import beans.Note;
import beans.Professeur;
import services.NoteService;
import services.ExportService;
import exceptions.ServiceException;
import exceptions.ValidationException;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import services.ModuleService;

@Named
@SessionScoped
public class NoteController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private AuthentificationController authController;
    
    private final NoteService noteService;
    private final ExportService exportService;
    
    private List<Note> notes;
    private Map<Integer, Double> notesSaisies;
    private int idMatiereSelectionnee; // j'aurais dû l'appeler idModule
    private Map<String, Double> statistiques;
    private double moyenneEtudiant;
    private int nombreMatieresValidees;
    private double noteMin;
    private double noteMax;
    private String mention;
    private String dateNow;
    
    public NoteController() {
        this.noteService = new NoteService();
        this.exportService = new ExportService();
        this.notesSaisies = new HashMap<>();
        this.dateNow = java.time.LocalDate.now().toString();
    }
    
    public int getNombreNotes() {
        try {
            if (authController != null && authController.isAdmin()) {
                if (statistiques == null) {
                    calculerStatistiques();
                }
                return statistiques != null && statistiques.containsKey("nombreNotes") 
                       ? statistiques.get("nombreNotes").intValue() : 0;
            }
            return notes != null ? notes.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private List<Note> dernieresNotes;
    private String nomMatiereSelectionnee;

    public String getNomMatiereSelectionnee() {
        if (idMatiereSelectionnee > 0 && notes != null && !notes.isEmpty()) {
            nomMatiereSelectionnee = notes.get(0).getModule().getNom();
        }
        return nomMatiereSelectionnee;
    }

    public List<Note> getDernieresNotes() {
        try {
            if (authController != null && authController.isProfesseur()) {
                int idProfesseur = authController.getUtilisateurCourant().getId();
                List<Note> toutesNotes = noteService.getNotesByProfesseur(idProfesseur);
                
                if (toutesNotes != null && !toutesNotes.isEmpty()) {
                    return toutesNotes.stream()
                        .filter(n -> n.getDateSaisie() != null)
                        .sorted((n1, n2) -> n2.getDateSaisie().compareTo(n1.getDateSaisie()))
                        .limit(10)
                        .collect(Collectors.toList());
                }
            }
            return new ArrayList<>();
        } catch (ServiceException e) {
            System.err.println("❌ Erreur dans getDernieresNotes: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public void exporterCSVProfesseur() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        
        try {
            if (authController != null && authController.isProfesseur()) {
                if (idMatiereSelectionnee > 0) {
                    exportService.exporterNotesMatiereCSV(idMatiereSelectionnee, response);
                    context.responseComplete();
                    System.out.println("✅ Export CSV réussi pour la matière ID: " + idMatiereSelectionnee);
                } else {
                    context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, 
                            "Veuillez sélectionner un module", null));
                }
            }
        } catch (Exception e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur d'export: " + e.getMessage(), null));
        }
    }

    public String voirToutesLesNotes() {
        try {
            if (authController != null && authController.isAdmin()) {
                return "/views/admin/gestionNotes?faces-redirect=true";
            }
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur: " + e.getMessage(), null));
            return null;
        }
    }
    
    public void chargerNotesEtudiant() {
        if (authController != null && authController.isEtudiant()) {
            try {
                int idEtudiant = authController.getUtilisateurCourant().getId();
                this.notes = noteService.getNotesByEtudiant(idEtudiant);
                calculerMoyenneEtudiant();
                calculerMatieresValidees();
                calculerStatsNotes();
                System.out.println("✅ Notes chargées pour étudiant ID " + idEtudiant + 
                                 ": " + notes.size() + " notes");
                
            } catch (ServiceException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Erreur: " + e.getMessage(), null));
            }
        }
    }
    
    private void calculerMoyenneEtudiant() {
        try {
            this.moyenneEtudiant = noteService.calculerMoyenneEtudiant(
                authController.getUtilisateurCourant().getId());
        } catch (ServiceException e) {
            this.moyenneEtudiant = 0;
        }
    }
    
    private void calculerMatieresValidees() {
        int validees = 0;
        if (notes != null) {
            for (Note n : notes) {
                if (n.getNote() >= 10) validees++;
            }
        }
        this.nombreMatieresValidees = validees;
    }
    
    private void calculerStatsNotes() {
        if (notes != null && !notes.isEmpty()) {
            noteMin = notes.stream().mapToDouble(Note::getNote).min().orElse(0);
            noteMax = notes.stream().mapToDouble(Note::getNote).max().orElse(0);
        }
    }
    
    public String getMention() {
        if (moyenneEtudiant >= 16) mention = "Très Bien";
        else if (moyenneEtudiant >= 14) mention = "Bien";
        else if (moyenneEtudiant >= 12) mention = "Assez Bien";
        else if (moyenneEtudiant >= 10) mention = "Passable";
        else mention = "Insuffisant";
        return mention;
    }
    
    public void chargerNotesMatiere() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        try {
            System.out.println("\n=== CHARGEMENT DES NOTES ===");
            System.out.println("Module ID: " + idMatiereSelectionnee);
            
            if (idMatiereSelectionnee <= 0) {
                context.addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, 
                        "Veuillez sélectionner un module", null));
                return;
            }
            
            // récupérer le nom du module pour l'afficher
            String nomModule = "Module " + idMatiereSelectionnee;
            try {
                ModuleService moduleService = new ModuleService();
                beans.Module module = moduleService.getModuleById(idMatiereSelectionnee);
                if (module != null) {
                    nomModule = module.getNom();
                }
            } catch (Exception e) {
                // tant pis, on garde le nom par défaut
            }
            
            // récupérer les étudiants inscrits
            List<Etudiant> etudiantsInscrits = noteService.getEtudiantsByModule(idMatiereSelectionnee);
            System.out.println("📊 Étudiants trouvés: " + (etudiantsInscrits != null ? etudiantsInscrits.size() : 0));
            
            if (etudiantsInscrits == null || etudiantsInscrits.isEmpty()) {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, 
                        "Aucun étudiant inscrit à ce module", null));
                return;
            }
            
            // récupérer les notes existantes
            List<Note> notesExistantes = noteService.getNotesByMatiere(idMatiereSelectionnee);
            Map<Integer, Double> notesMap = new HashMap<>();
            
            if (notesExistantes != null) {
                for (Note n : notesExistantes) {
                    notesMap.put(n.getEtudiant().getId(), n.getNote());
                }
            }
            
            // créer la liste des notes à afficher
            this.notes = new ArrayList<>();
            notesSaisies.clear();
            
            for (Etudiant e : etudiantsInscrits) {
                Note note = new Note();
                note.setEtudiant(e);
                
                beans.Module m = new beans.Module();
                m.setId(idMatiereSelectionnee);
                m.setNom(nomModule);
                note.setModule(m);
                
                Double noteValue = notesMap.get(e.getId());
                if (noteValue != null) {
                    note.setNote(noteValue);
                    notesSaisies.put(e.getId(), noteValue);
                } else {
                    note.setNote(0.0);
                    notesSaisies.put(e.getId(), 0.0);
                }
                
                notes.add(note);
            }
            
            System.out.println("✅ " + notes.size() + " étudiants préparés");
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "✅ " + notes.size() + " étudiants chargés", null));
            
        } catch (ServiceException e) {
            e.printStackTrace();
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur: " + e.getMessage(), null));
        }
    }
    
    public void testerConnexionModule() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            System.out.println("\n=== TEST DE CONNEXION MODULE ===");
            System.out.println("ID module sélectionné: " + idMatiereSelectionnee);
            
            List<Etudiant> etudiants = noteService.getEtudiantsByModule(idMatiereSelectionnee);
            System.out.println("Étudiants trouvés: " + (etudiants != null ? etudiants.size() : 0));
            
            if (etudiants != null) {
                for (Etudiant e : etudiants) {
                    System.out.println("   - " + e.getPrenom() + " " + e.getNom() + " (" + e.getCne() + ")");
                }
            }
            
            List<Note> notesModule = noteService.getNotesByMatiere(idMatiereSelectionnee);
            System.out.println("Notes trouvées: " + (notesModule != null ? notesModule.size() : 0));
            
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "Test: " + (etudiants != null ? etudiants.size() : 0) + " étudiants", null));
                
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur: " + e.getMessage(), null));
        }
    }
    
public String enregistrerNotes() {
    FacesContext context = FacesContext.getCurrentInstance();
    
    try {
        System.out.println("🔍 Enregistrement des notes pour le module ID: " + idMatiereSelectionnee);
        System.out.println("Nombre de notes à enregistrer: " + notesSaisies.size());
        
        if (notesSaisies.isEmpty()) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, 
                    "Aucune note à enregistrer", null));
            return null;
        }
        
        List<Note> notesToSave = new ArrayList<>();
        int notesModifiees = 0;
        int notesCreees = 0;
        
        for (Map.Entry<Integer, Double> entry : notesSaisies.entrySet()) {
            int idEtudiant = entry.getKey();
            Double valeur = entry.getValue();
            
            // Vérifier si la note a été modifiée (pas 0.0 ou déjà existante)
            if (valeur == null || valeur == 0.0) {
                System.out.println("   ⚠️ Note ignorée pour étudiant ID " + idEtudiant + " (valeur 0.0)");
                continue; // Ignorer les notes à 0.0 (non saisies)
            }
            
            System.out.println("   - Étudiant ID " + idEtudiant + ": " + valeur);
            
            // Vérifier si une note existe déjà
            Note existing = noteService.getNoteByEtudiantAndModule(idEtudiant, idMatiereSelectionnee, "normale");
            
            if (existing != null) {
                // Mise à jour de la note existante
                existing.setNote(valeur);
                notesToSave.add(existing);
                notesModifiees++;
                System.out.println("   🔄 Mise à jour note existante");
            } else {
                // Création d'une nouvelle note
                Note note = new Note();
                Etudiant e = new Etudiant();
                e.setId(idEtudiant);
                
                beans.Module m = new beans.Module();
                m.setId(idMatiereSelectionnee);
                
                note.setEtudiant(e);
                note.setModule(m);
                note.setNote(valeur);
                note.setSession("normale");
                
                if (authController.getUtilisateurCourant() instanceof Professeur) {
                    note.setProfesseur((Professeur) authController.getUtilisateurCourant());
                }
                
                notesToSave.add(note);
                notesCreees++;
                System.out.println("   ✅ Création nouvelle note");
            }
        }
        
        if (notesToSave.isEmpty()) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, 
                    "Aucune note valide à enregistrer (les notes à 0.0 sont ignorées)", null));
            return null;
        }
        
        noteService.enregistrerNotes(notesToSave);
        
        context.addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, 
                "✅ Notes enregistrées: " + notesCreees + " créées, " + notesModifiees + " modifiées", null));
        
        // Réinitialiser les notes pour éviter les doublons
        this.notes = null;
        this.notesSaisies.clear();
        
        return "/views/professeur/dashboard?faces-redirect=true";
        
    } catch (ServiceException | ValidationException e) {
        System.err.println("❌ Erreur enregistrerNotes: " + e.getMessage());
        e.printStackTrace();
        context.addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Erreur: " + e.getMessage(), null));
        return null;
    }
}    
    public void calculerStatistiques() {
        try {
            this.statistiques = noteService.getStatistiquesGlobales();
        } catch (ServiceException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur: " + e.getMessage(), null));
        }
    }
    
    public void exporterCSV() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        
        try {
            if (authController != null && authController.isEtudiant()) {
                int idEtudiant = authController.getUtilisateurCourant().getId();
                exportService.exporterNotesCSV(idEtudiant, response);
                context.responseComplete();
                System.out.println("✅ Export CSV réussi pour l'étudiant ID: " + idEtudiant);
            } else {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, 
                        "Export CSV disponible uniquement pour les étudiants", null));
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur export CSV: " + e.getMessage());
            e.printStackTrace();
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur d'export CSV: " + e.getMessage(), null));
        }
    }
    
    public void exporterBulletinPDF() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        
        try {
            if (authController != null && authController.isEtudiant() && 
                authController.getUtilisateurCourant() instanceof Etudiant) {
                
                Etudiant etudiant = (Etudiant) authController.getUtilisateurCourant();
                
                if (notes == null || notes.isEmpty()) {
                    chargerNotesEtudiant();
                }
                
                exportService.exporterBulletinPDF(etudiant, notes, response);
                context.responseComplete();
                System.out.println("✅ Export PDF réussi pour l'étudiant: " + etudiant.getCne());
            } else {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, 
                        "Bulletin PDF disponible uniquement pour les étudiants", null));
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur export PDF: " + e.getMessage());
            e.printStackTrace();
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur d'export PDF: " + e.getMessage(), null));
        }
    }
    
    // tous les getters et setters
    public List<Note> getNotes() { 
        if (notes == null && authController != null && authController.isEtudiant()) {
            chargerNotesEtudiant();
        }
        return notes; 
    }
    public void setNotes(List<Note> notes) { this.notes = notes; }
    
    public Map<Integer, Double> getNotesSaisies() { return notesSaisies; }
    public void setNotesSaisies(Map<Integer, Double> notesSaisies) { this.notesSaisies = notesSaisies; }
    
    public int getIdMatiereSelectionnee() { return idMatiereSelectionnee; }
    public void setIdMatiereSelectionnee(int idMatiereSelectionnee) { this.idMatiereSelectionnee = idMatiereSelectionnee; }
    
    public Map<String, Double> getStatistiques() { return statistiques; }
    public void setStatistiques(Map<String, Double> statistiques) { this.statistiques = statistiques; }
    
    public double getMoyenneEtudiant() { return moyenneEtudiant; }
    public void setMoyenneEtudiant(double moyenneEtudiant) { this.moyenneEtudiant = moyenneEtudiant; }
    
    public int getNombreMatieresValidees() { return nombreMatieresValidees; }
    public void setNombreMatieresValidees(int nombreMatieresValidees) { this.nombreMatieresValidees = nombreMatieresValidees; }
    
    public double getNoteMin() { return noteMin; }
    public double getNoteMax() { return noteMax; }
    public String getDateNow() { return dateNow; }
}