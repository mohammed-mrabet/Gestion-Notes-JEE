package services;

import beans.Utilisateur;
import dao.implementations.UtilisateurDAO;
import dao.interfaces.IUtilisateurDAO;
import exceptions.DAOException;
import exceptions.ServiceException;
import exceptions.ValidationException;

import java.util.List;

public class UtilisateurService {
    
    private final IUtilisateurDAO utilisateurDAO;
    
    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }
    
    public List<Utilisateur> getAllUtilisateurs() throws ServiceException {
        try {
            return utilisateurDAO.findAll();
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des utilisateurs", e);
        }
    }
    
    public Utilisateur getUtilisateurById(int id) throws ServiceException {
        try {
            return utilisateurDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération de l'utilisateur", e);
        }
    }
    
    public void ajouterUtilisateur(Utilisateur utilisateur) throws ServiceException, ValidationException {
        try {
            if (utilisateurDAO.loginExists(utilisateur.getLogin())) {
                throw new ValidationException("Ce login existe déjà");
            }
            if (utilisateurDAO.emailExists(utilisateur.getEmail())) {
                throw new ValidationException("Cet email existe déjà");
            }
            
            utilisateurDAO.create(utilisateur);
            System.out.println("✅ Utilisateur ajouté: " + utilisateur.getLogin());
            
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de l'ajout de l'utilisateur", e);
        }
    }
    
    public void modifierUtilisateur(Utilisateur utilisateur) throws ServiceException {
        try {
            utilisateurDAO.update(utilisateur);
            System.out.println("✅ Utilisateur modifié: " + utilisateur.getLogin());
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la modification de l'utilisateur", e);
        }
    }
    
    public void supprimerUtilisateur(int id) throws ServiceException {
        try {
            utilisateurDAO.delete(id);
            System.out.println("✅ Utilisateur supprimé: ID " + id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la suppression de l'utilisateur", e);
        }
    }
    
    public List<Utilisateur> getUtilisateursByRole(String role) throws ServiceException {
        try {
            return utilisateurDAO.findByRole(role);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des utilisateurs par rôle", e);
        }
    }
}