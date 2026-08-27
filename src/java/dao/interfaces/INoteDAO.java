package dao.interfaces;

import beans.Etudiant;
import beans.Note;
import exceptions.DAOException;
import java.util.List;

/**
 * interface pour gérer les notes
 * y'a pas mal de méthodes de recherche
 */
public interface INoteDAO {
    
    // note par ID
    Note findById(int id) throws DAOException;
    
    // notes d'un étudiant
    List<Note> findByEtudiant(int idEtudiant) throws DAOException;
    
    // notes pour une matière (module)
    List<Note> findByMatiere(int idMatiere) throws DAOException;
    
    // notes saisies par un professeur
    List<Note> findByProfesseur(int idProfesseur) throws DAOException;
    
    // méthode directe pour récupérer les étudiants d'un module (via SQL natif)
    List<Etudiant> findEtudiantsByModuleDirect(int idModule) throws DAOException;
    
    // trouver une note spécifique (étudiant + matière + session)
    Note findByEtudiantAndMatiere(int idEtudiant, int idMatiere, String session) throws DAOException;
    
    // toutes les notes
    List<Note> findAll() throws DAOException;
    
    // ajouter une note
    void create(Note note) throws DAOException;
    
    // modifier une note
    void update(Note note) throws DAOException;
    
    // supprimer une note
    void delete(int id) throws DAOException;
    
    // calculer la moyenne d'un étudiant
    double calculerMoyenneEtudiant(int idEtudiant) throws DAOException;
}