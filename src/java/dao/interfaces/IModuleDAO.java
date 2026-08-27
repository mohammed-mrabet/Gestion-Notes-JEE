package dao.interfaces;

import beans.Module;
import exceptions.DAOException;
import java.util.List;

/**
 * CRUD pour les modules
 */
public interface IModuleDAO {
    
    // trouver par ID
    Module findById(int id) throws DAOException;
    
    // trouver par code (ex: GL, BD)
    Module findByCode(String code) throws DAOException;
    
    // liste de tous les modules
    List<Module> findAll() throws DAOException;
    
    // ajouter un module
    void create(Module module) throws DAOException;
    
    // modifier un module
    void update(Module module) throws DAOException;
    
    // supprimer un module
    void delete(int id) throws DAOException;
    
    // vérifier si un code existe déjà (pour éviter les doublons)
    boolean codeExists(String code) throws DAOException;
}