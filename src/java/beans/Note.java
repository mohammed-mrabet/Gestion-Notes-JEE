package beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Note implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "id_etudiant", nullable = false)
    private Etudiant etudiant; // l'étudiant concerné
    
    @ManyToOne
    @JoinColumn(name = "id_module", nullable = false)
    private Module module; // le module concerné (j'avais mis matière avant, j'ai changé)
    
    @Column(precision = 4, scale = 2)
    private double note; // la note sur 20
    
    @Column(length = 20)
    private String session = "normale"; // normale ou rattrapage
    
    @Column(name = "date_saisie")
    private LocalDateTime dateSaisie; // quand la note a été saisie
    
    @ManyToOne
    @JoinColumn(name = "id_professeur")
    private Professeur professeur; // qui a saisi la note
    
    public Note() {
        this.dateSaisie = LocalDateTime.now();
    }
    
    public Note(Etudiant etudiant, Module module, double note, String session, Professeur professeur) {
        this.etudiant = etudiant;
        this.module = module;
        this.note = note;
        this.session = session;
        this.professeur = professeur;
        this.dateSaisie = LocalDateTime.now();
    }
    
    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }
    
    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
    
    public double getNote() { return note; }
    public void setNote(double note) { this.note = note; }
    
    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }
    
    public LocalDateTime getDateSaisie() { return dateSaisie; }
    public void setDateSaisie(LocalDateTime dateSaisie) { this.dateSaisie = dateSaisie; }
    
    public Professeur getProfesseur() { return professeur; }
    public void setProfesseur(Professeur professeur) { this.professeur = professeur; }
}