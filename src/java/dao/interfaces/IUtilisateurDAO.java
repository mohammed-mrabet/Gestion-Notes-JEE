package dao.interfaces;

import beans.Utilisateur;
import exceptions.DAOException;
import java.util.List;

/**
 * interface de base pour tous les utilisateurs
 * (admin, prof, étudiant)
 */
public interface IUtilisateurDAO {
    
    // authentification
    Utilisateur authentifier(String login, String motDePasse) throws DAOException;
    
    // recherche par ID
    Utilisateur findById(int id) throws DAOException;
    
    // recherche par login
    Utilisateur findByLogin(String login) throws DAOException;
    
    // tous les utilisateurs
    List<Utilisateur> findAll() throws DAOException;
    
    // utilisateurs par rôle
    List<Utilisateur> findByRole(String role) throws DAOException;
    
    // créer un utilisateur
    void create(Utilisateur utilisateur) throws DAOException;
    
    // modifier un utilisateur
    void update(Utilisateur utilisateur) throws DAOException;
    
    // supprimer un utilisateur
    void delete(int id) throws DAOException;
    
    // vérifier si un login existe déjà
    boolean loginExists(String login) throws DAOException;
    
    // vérifier si un email existe déjà
    boolean emailExists(String email) throws DAOException;
}