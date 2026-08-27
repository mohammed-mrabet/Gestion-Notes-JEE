package beans;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "modules")
public class Module implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(unique = true, nullable = false, length = 20)
    private String code; // ex: GL, BD, IA
    
    @Column(nullable = false, length = 100)
    private String nom; // nom complet du module
    
    @Column(nullable = false)
    private int coefficient = 1; // son importance dans le calcul
    
    @ManyToOne
    @JoinColumn(name = "id_professeur")
    private Professeur professeur; // qui enseigne ce module
    
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Note> notes; // les notes de ce module
    
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    private List<Inscription> inscriptions; // les étudiants inscrits
    
    public Module() {}
    
    public Module(String code, String nom, int coefficient) {
        this.code = code;
        this.nom = nom;
        this.coefficient = coefficient;
    }
    
    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public int getCoefficient() { return coefficient; }
    public void setCoefficient(int coefficient) { this.coefficient = coefficient; }
    
    public Professeur getProfesseur() { return professeur; }
    public void setProfesseur(Professeur professeur) { this.professeur = professeur; }
    
    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes; }
    
    public List<Inscription> getInscriptions() { return inscriptions; }
    public void setInscriptions(List<Inscription> inscriptions) { this.inscriptions = inscriptions; }
}