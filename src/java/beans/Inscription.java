package beans;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "inscriptions")
public class Inscription implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "id_etudiant", nullable = false)
    private Etudiant etudiant; // l'étudiant qui s'inscrit
    
    @ManyToOne
    @JoinColumn(name = "id_module", nullable = false)
    private Module module; // le module choisi
    
    @Column(name = "annee", nullable = false, length = 10)
    private String annee; // année universitaire, ex: 2025-2026
    
    @Column(name = "date_inscription")
    private java.time.LocalDateTime dateInscription;
    
    public Inscription() {
        this.dateInscription = java.time.LocalDateTime.now();
    }
    
    public Inscription(Etudiant etudiant, Module module, String annee) {
        this.etudiant = etudiant;
        this.module = module;
        this.annee = annee;
        this.dateInscription = java.time.LocalDateTime.now();
    }
    
    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }
    
    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
    
    public String getAnnee() { return annee; }
    public void setAnnee(String annee) { this.annee = annee; }
    
    public java.time.LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(java.time.LocalDateTime dateInscription) { 
        this.dateInscription = dateInscription; 
    }
}