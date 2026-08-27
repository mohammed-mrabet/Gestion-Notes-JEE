package services;

import dto.GroupeTPDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@ApplicationScoped
public class GroupeTPService {
    
    // 🔴 IMPORTANT : Remplacez cette IP par celle du binôme
    private static final String BINOME_IP = "100.101.59.118"; // À CHANGER !
    private static final String BASE_URL = "http://" + BINOME_IP + ":8080/mini-projet-be3-tp/api";
    private static final String GROUPE_PATH = "groupes";
    
    private Client client;
    private WebTarget target;
    private List<GroupeTPDTO> cacheGroupes;
    private String dernierStatut;
    private boolean serviceDisponible = false;
    
    public GroupeTPService() {
        this.cacheGroupes = new CopyOnWriteArrayList<>();
        this.dernierStatut = "Service non initialisé";
    }
    
    @PostConstruct
    public void init() {
        try {
            System.out.println("🔌 Initialisation du client REST pour l'API du binôme BE3...");
            System.out.println("   URL: " + BASE_URL);
            this.client = ClientBuilder.newClient();
            this.target = client.target(BASE_URL);
            
            // Tester la connexion
            testConnexion();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur initialisation: " + e.getMessage());
            this.dernierStatut = "Erreur de connexion - Utilisation de données de test";
            loadMockData();
        }
    }
    
    @PreDestroy
    public void cleanup() {
        if (client != null) {
            client.close();
            System.out.println("🔌 Client REST fermé");
        }
    }
    
    /**
     * Teste la connexion à l'API du binôme
     */
    private void testConnexion() {
        try {
            System.out.println("📡 Test de connexion à l'API...");
            int status = target.path(GROUPE_PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .head()
                    .getStatus();
            
            if (status == 200) {
                System.out.println("✅ API disponible (HTTP " + status + ")");
                serviceDisponible = true;
                refreshData();
            } else {
                System.out.println("⚠️ API a répondu avec le statut HTTP " + status);
                this.dernierStatut = "API répond mais statut: " + status;
                loadMockData();
            }
            
        } catch (Exception e) {
            System.err.println("❌ API inaccessible: " + e.getMessage());
            System.err.println("   Causes possibles :");
            System.err.println("   - Le serveur du binôme n'est pas démarré");
            System.err.println("   - L'IP est incorrecte (actuelle: " + BINOME_IP + ")");
            System.err.println("   - Problème de réseau");
            this.dernierStatut = "Service distant indisponible - Utilisation des données de test";
            loadMockData();
        }
    }
    
    /**
     * Récupère les vraies données depuis l'API du binôme
     */
    private List<GroupeTPDTO> fetchGroupesFromAPI() {
        try {
            System.out.println("📡 Appel API: " + BASE_URL + "/" + GROUPE_PATH);
            
            List<GroupeTPDTO> groupes = target.path(GROUPE_PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<GroupeTPDTO>>() {});
            
            if (groupes != null && !groupes.isEmpty()) {
                this.cacheGroupes = new ArrayList<>(groupes);
                this.dernierStatut = "✅ Succès: " + groupes.size() + " groupes récupérés depuis le binôme";
                this.serviceDisponible = true;
                System.out.println("✅ " + groupes.size() + " groupes récupérés");
                
                // Afficher les données pour déboguer
                for (GroupeTPDTO g : groupes) {
                    System.out.println("   - " + g.getNom() + " (" + g.getCreneau() + "): " 
                                     + g.getPlacesOccupees() + "/" + g.getCapaciteMax());
                }
                
                return groupes;
            } else {
                this.dernierStatut = "⚠️ API a retourné une liste vide";
                if (cacheGroupes.isEmpty()) loadMockData();
                return new ArrayList<>(cacheGroupes);
            }
            
        } catch (jakarta.ws.rs.ProcessingException e) {
            System.err.println("❌ Service distant indisponible: " + e.getMessage());
            this.dernierStatut = "❌ Service distant indisponible - Utilisation des données de test";
            this.serviceDisponible = false;
            if (cacheGroupes.isEmpty()) loadMockData();
            return new ArrayList<>(cacheGroupes);
            
        } catch (jakarta.ws.rs.WebApplicationException e) {
            int status = e.getResponse().getStatus();
            System.err.println("❌ Erreur HTTP " + status + ": " + e.getMessage());
            this.dernierStatut = "❌ Erreur HTTP " + status + " - Utilisation des données de test";
            this.serviceDisponible = false;
            if (cacheGroupes.isEmpty()) loadMockData();
            return new ArrayList<>(cacheGroupes);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue: " + e.getMessage());
            this.dernierStatut = "❌ Erreur: " + e.getMessage() + " - Utilisation des données de test";
            this.serviceDisponible = false;
            if (cacheGroupes.isEmpty()) loadMockData();
            return new ArrayList<>(cacheGroupes);
        }
    }
    
    /**
     * Charge les données de test (fallback)
     */
    private void loadMockData() {
        System.out.println("📊 Chargement des données de test...");
        
        List<GroupeTPDTO> mockGroupes = new ArrayList<>();
        
        GroupeTPDTO g1 = new GroupeTPDTO();
        g1.setId(1L);
        g1.setNom("TP1 - Génie Logiciel");
        g1.setCreneau("Lundi 08:30 - 10:30");
        g1.setCapaciteMax(20);
        g1.setPlacesOccupees(15);
        mockGroupes.add(g1);
        
        GroupeTPDTO g2 = new GroupeTPDTO();
        g2.setId(2L);
        g2.setNom("TP2 - Génie Logiciel");
        g2.setCreneau("Lundi 10:45 - 12:45");
        g2.setCapaciteMax(20);
        g2.setPlacesOccupees(20);
        mockGroupes.add(g2);
        
        GroupeTPDTO g3 = new GroupeTPDTO();
        g3.setId(3L);
        g3.setNom("TP1 - Bases de Données");
        g3.setCreneau("Mardi 14:00 - 16:00");
        g3.setCapaciteMax(18);
        g3.setPlacesOccupees(12);
        mockGroupes.add(g3);
        
        GroupeTPDTO g4 = new GroupeTPDTO();
        g4.setId(4L);
        g4.setNom("TP2 - Bases de Données");
        g4.setCreneau("Mercredi 09:00 - 11:00");
        g4.setCapaciteMax(18);
        g4.setPlacesOccupees(18);
        mockGroupes.add(g4);
        
        this.cacheGroupes = mockGroupes;
        
        if (!serviceDisponible) {
            this.dernierStatut = "✅ Données de test (mode démo) - Service distant indisponible";
        }
        
        System.out.println("✅ " + mockGroupes.size() + " groupes de test chargés");
    }
    
    /**
     * Rafraîchit les données (toujours essayer l'API d'abord)
     */
    public void refreshData() {
        if (serviceDisponible) {
            fetchGroupesFromAPI();
        } else {
            // Réessayer de se connecter
            testConnexion();
            if (serviceDisponible) {
                fetchGroupesFromAPI();
            }
        }
    }
    
    /**
     * Force une reconnexion à l'API
     */
    public void reconnecter() {
        serviceDisponible = false;
        testConnexion();
    }
    
    // ========== GETTERS ==========
    
    public List<GroupeTPDTO> getAllGroupes() {
        if (cacheGroupes.isEmpty()) {
            if (serviceDisponible) {
                return fetchGroupesFromAPI();
            } else {
                loadMockData();
            }
        }
        return new ArrayList<>(cacheGroupes);
    }
    
    public String getDernierStatut() {
        return dernierStatut;
    }
    
    public int getNombreGroupes() {
        return cacheGroupes.size();
    }
    
    public int getTotalPlaces() {
        return cacheGroupes.stream().mapToInt(GroupeTPDTO::getCapaciteMax).sum();
    }
    
    public int getTotalOccupees() {
        return cacheGroupes.stream().mapToInt(GroupeTPDTO::getPlacesOccupees).sum();
    }
    
    public int getTotalLibres() {
        return getTotalPlaces() - getTotalOccupees();
    }
    
    public boolean isServiceDisponible() {
        return serviceDisponible;
    }
    
    public String getStatistiquesGroupes() {
        List<GroupeTPDTO> groupes = getAllGroupes();
        if (groupes.isEmpty()) return "Aucune donnée disponible";
        
        int totalPlaces = getTotalPlaces();
        int totalOccupees = getTotalOccupees();
        double taux = totalPlaces > 0 ? (totalOccupees * 100.0) / totalPlaces : 0;
        
        return String.format(
            "Groupes: %d | Places: %d | Occupées: %d | Libres: %d | Taux: %.1f%%",
            groupes.size(), totalPlaces, totalOccupees, (totalPlaces - totalOccupees), taux
        );
    }
}