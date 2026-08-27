package beans;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "professeurs")
@PrimaryKeyJoinColumn(name = "id_utilisateur")
public class Professeur extends Utilisateur implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Column(length = 100)
    private String specialite; // sa matière de prédilection
    
    @Column(length = 50)
    private String grade; // professeur principal, etc.
    
    @OneToMany(mappedBy = "professeur", fetch = FetchType.LAZY)
    private List<Module> modules; // les modules qu'il enseigne
    
    @OneToMany(mappedBy = "professeur", fetch = FetchType.LAZY)
    private List<Note> notesSaisies; // les notes qu'il a saisies
    
    public Professeur() {}
    
    public Professeur(String nom, String prenom, String email, String login, String motDePasse,
                      String specialite, String grade) {
        super(nom, prenom, email, login, motDePasse, Role.PROFESSEUR);
        this.specialite = specialite;
        this.grade = grade;
    }
    
    // getters/setters
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public List<Module> getModules() { return modules; }
    public void setModules(List<Module> modules) { this.modules = modules; }
    
    public List<Note> getNotesSaisies() { return notesSaisies; }
    public void setNotesSaisies(List<Note> notesSaisies) { this.notesSaisies = notesSaisies; }
}