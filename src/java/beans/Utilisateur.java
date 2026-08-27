package beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
// j'ai commenté cette ligne parce que ça posait problème avec la BD
// @DiscriminatorColumn(name = "type_utilisateur")
public class Utilisateur implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false, length = 50)
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false, length = 50)
    private String prenom;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @NotBlank(message = "Le login est obligatoire")
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String login;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(nullable = false, length = 255)
    private String motDePasse; // sera hashé avec BCrypt
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    
    @Column(name = "date_inscription")
    private LocalDateTime dateInscription; // date de création du compte
    
    public enum Role {
        ADMIN, PROFESSEUR, ETUDIANT
    }
    
    public Utilisateur() {
        this.dateInscription = LocalDateTime.now();
    }
    
    public Utilisateur(String nom, String prenom, String email, String login, String motDePasse, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.login = login;
        this.motDePasse = motDePasse;
        this.role = role;
        this.dateInscription = LocalDateTime.now();
    }
    
    // getters/setters en vrac
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
    
    public String getNomComplet() {
        return prenom + " " + nom; // pratique pour afficher
    }
}