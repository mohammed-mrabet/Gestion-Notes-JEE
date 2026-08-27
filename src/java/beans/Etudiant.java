package beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "etudiants")
@PrimaryKeyJoinColumn(name = "id_utilisateur")
public class Etudiant extends Utilisateur implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Column(unique = true, nullable = false, length = 20)
    private String cne; // Code National de l'Etudiant, important ça!
    
    @Column(name = "date_naissance")
    private LocalDate dateNaissance; // pour vérifier l'âge
    
    @Column(length = 200)
    private String adresse;
    
    @Column(length = 20)
    private String classe; // GL3, BD3, etc.
    
    @OneToMany(mappedBy = "etudiant", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Inscription> inscriptions; // ses inscriptions aux modules
    
    @OneToMany(mappedBy = "etudiant", fetch = FetchType.LAZY)
    private List<Note> notes; // ses notes
    
    public Etudiant() {}
    
    public Etudiant(String nom, String prenom, String email, String login, String motDePasse, 
                    String cne, LocalDate dateNaissance, String adresse, String classe) {
        super(nom, prenom, email, login, motDePasse, Role.ETUDIANT);
        this.cne = cne;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.classe = classe;
    }
    
    // tous les getters et setters
    public String getCne() { return cne; }
    public void setCne(String cne) { this.cne = cne; }
    
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    
    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }
    
    public List<Inscription> getInscriptions() { return inscriptions; }
    public void setInscriptions(List<Inscription> inscriptions) { this.inscriptions = inscriptions; }
    
    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes; }
}