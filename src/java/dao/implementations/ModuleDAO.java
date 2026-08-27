package dao.implementations;

import beans.Module;
import dao.interfaces.IModuleDAO;
import exceptions.DAOException;
import utils.JpaUtil;
import jakarta.persistence.*;
import java.util.List;

public class ModuleDAO implements IModuleDAO {
    
    @Override
    public Module findById(int id) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Module.class, id);
        } catch (Exception e) {
            throw new DAOException("Erreur findById", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Module findByCode(String code) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Module> query = em.createQuery(
                "SELECT m FROM Module m WHERE m.code = :code", Module.class);
            query.setParameter("code", code);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new DAOException("Erreur findByCode", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Module> findAll() throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Module> query = em.createQuery(
                "SELECT m FROM Module m LEFT JOIN FETCH m.professeur ORDER BY m.code", 
                Module.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la récupération des modules", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void create(Module module) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(module);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Erreur create", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void update(Module module) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(module);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Erreur update", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void delete(int id) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Module m = em.find(Module.class, id);
            if (m != null) em.remove(m);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Erreur delete", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean codeExists(String code) throws DAOException {
        return findByCode(code) != null;
    }
}