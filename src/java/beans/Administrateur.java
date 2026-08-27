package beans;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "administrateurs")
@PrimaryKeyJoinColumn(name = "id_utilisateur")
public class Administrateur extends Utilisateur implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Column(length = 50)
    private String fonction; // la fonction de l'admin dans l'établissement
    
    public Administrateur() {
        // constructeur par défaut, j'ai failli oublier de le mettre
    }
    
    public Administrateur(String nom, String prenom, String email, String login, 
                         String motDePasse, String fonction) {
        super(nom, prenom, email, login, motDePasse, Role.ADMIN);
        this.fonction = fonction;
    }
    
    // getters et setters basiques
    public String getFonction() { return fonction; }
    public void setFonction(String fonction) { this.fonction = fonction; }
}