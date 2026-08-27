package dao.interfaces;

import beans.Inscription;
import exceptions.DAOException;
import java.util.List;

/**
 * interface pour gérer les inscriptions des étudiants aux modules
 * j'ai mis les méthodes de base + quelques recherches spécifiques
 */
public interface IInscriptionDAO {
    
    // trouver une inscription par son ID
    Inscription findById(int id) throws DAOException;
    
    // toutes les inscriptions
    List<Inscription> findAll() throws DAOException;
    
    // inscriptions d'un étudiant
    List<Inscription> findByEtudiant(int idEtudiant) throws DAOException;
    
    // inscriptions à un module
    List<Inscription> findByModule(int idModule) throws DAOException;
    
    // inscriptions pour une année donnée
    List<Inscription> findByAnnee(String annee) throws DAOException;
    
    // vérifier si une inscription existe déjà
    boolean exists(int idEtudiant, int idModule, String annee) throws DAOException;
    
    // créer une nouvelle inscription
    void create(int idEtudiant, int idModule, String annee) throws DAOException;
    
    // supprimer une inscription
    void delete(int id) throws DAOException;
}