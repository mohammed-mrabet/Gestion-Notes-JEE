package services;

import beans.Module;
import dao.implementations.ModuleDAO;
import dao.interfaces.IModuleDAO;
import exceptions.DAOException;
import exceptions.ServiceException;
import java.util.List;

public class ModuleService {
    
    private final IModuleDAO moduleDAO;
    
    public ModuleService() {
        this.moduleDAO = new ModuleDAO();
    }
    
    public List<Module> getAllModules() throws ServiceException {
        try {
            return moduleDAO.findAll();
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des modules", e);
        }
    }
    
    public Module getModuleById(int id) throws ServiceException {
        try {
            return moduleDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération du module", e);
        }
    }
    
    public void ajouterModule(Module module) throws ServiceException {
        try {
            if (moduleDAO.codeExists(module.getCode())) {
                throw new ServiceException("Un module avec ce code existe déjà");
            }
            moduleDAO.create(module);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de l'ajout du module", e);
        }
    }
    
    public void modifierModule(Module module) throws ServiceException {
        try {
            moduleDAO.update(module);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la modification du module", e);
        }
    }
    
    public void supprimerModule(int id) throws ServiceException {
        try {
            moduleDAO.delete(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la suppression du module", e);
        }
    }
}