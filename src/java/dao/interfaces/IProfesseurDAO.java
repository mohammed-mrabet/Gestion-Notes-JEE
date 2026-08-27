package dao.interfaces;

import beans.Professeur;
import exceptions.DAOException;
import java.util.List;

/**
 * interface pour les professeurs
 * hérite de l'utilisateur de base
 */
public interface IProfesseurDAO {
    
    // trouver un prof par son ID
    Professeur findById(int id) throws DAOException;
    
    // tous les professeurs
    List<Professeur> findAll() throws DAOException;
    
    // professeurs par spécialité
    List<Professeur> findBySpecialite(String specialite) throws DAOException;
}