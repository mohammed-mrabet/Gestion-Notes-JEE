package dto;

import jakarta.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class GroupeTPDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String nom;
    private String creneau;
    
    @JsonbProperty("placesMax")
    private int capaciteMax;
    
    @JsonbProperty("placesOccupees")
    private int placesOccupees;
    
    // Constructeurs
    public GroupeTPDTO() {}
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getCreneau() { return creneau; }
    public void setCreneau(String creneau) { this.creneau = creneau; }
    
    public int getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(int capaciteMax) { this.capaciteMax = capaciteMax; }
    
    public int getPlacesOccupees() { return placesOccupees; }
    public void setPlacesOccupees(int placesOccupees) { this.placesOccupees = placesOccupees; }
    
    // Méthodes calculées
    public int getPlacesRestantes() {
        return capaciteMax - placesOccupees;
    }
    
    public double getTauxOccupation() {
        if (capaciteMax <= 0) return 0;
        return Math.round((placesOccupees * 100.0 / capaciteMax) * 100.0) / 100.0;
    }
    
    @Override
    public String toString() {
        return "GroupeTPDTO{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", creneau='" + creneau + '\'' +
                ", capaciteMax=" + capaciteMax +
                ", placesOccupees=" + placesOccupees +
                '}';
    }
}