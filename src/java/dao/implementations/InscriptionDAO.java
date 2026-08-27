package dao.implementations;

import beans.Inscription;
import beans.Etudiant;
import beans.Module;
import dao.interfaces.IInscriptionDAO;
import exceptions.DAOException;
import utils.JpaUtil;

import jakarta.persistence.*;
import java.util.List;

public class InscriptionDAO implements IInscriptionDAO {
    
    @Override
    public Inscription findById(int id) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Inscription.class, id);
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la recherche de l'inscription", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Inscription> findAll() throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Inscription> query = em.createQuery(
                "SELECT i FROM Inscription i JOIN FETCH i.etudiant e JOIN FETCH i.module m " +
                "ORDER BY i.annee DESC, e.nom, e.prenom", Inscription.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la récupération des inscriptions", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Inscription> findByEtudiant(int idEtudiant) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Inscription> query = em.createQuery(
                "SELECT i FROM Inscription i WHERE i.etudiant.id = :id", Inscription.class);
            query.setParameter("id", idEtudiant);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la recherche par étudiant", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Inscription> findByModule(int idModule) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            System.out.println("🔍 InscriptionDAO.findByModule: " + idModule);
            
            TypedQuery<Inscription> query = em.createQuery(
                "SELECT i FROM Inscription i " +
                "JOIN FETCH i.etudiant e " +
                "JOIN FETCH e.utilisateur u " +
                "WHERE i.module.id = :idModule " +
                "ORDER BY u.nom, u.prenom", 
                Inscription.class);
            query.setParameter("idModule", idModule);
            
            List<Inscription> result = query.getResultList();
            System.out.println("📊 Inscriptions trouvées: " + result.size());
            
            return result;
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la recherche par module", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Inscription> findByAnnee(String annee) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Inscription> query = em.createQuery(
                "SELECT i FROM Inscription i WHERE i.annee = :annee", Inscription.class);
            query.setParameter("annee", annee);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la recherche par année", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean exists(int idEtudiant, int idModule, String annee) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(i) FROM Inscription i WHERE i.etudiant.id = :idE AND i.module.id = :idM AND i.annee = :a", 
                Long.class);
            query.setParameter("idE", idEtudiant);
            query.setParameter("idM", idModule);
            query.setParameter("a", annee);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la vérification d'existence", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void create(int idEtudiant, int idModule, String annee) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Etudiant etudiant = em.getReference(Etudiant.class, idEtudiant);
            Module module = em.getReference(Module.class, idModule);
            
            Inscription inscription = new Inscription();
            inscription.setEtudiant(etudiant);
            inscription.setModule(module);
            inscription.setAnnee(annee);
            
            em.persist(inscription);
            tx.commit();
            
            System.out.println("✅ Inscription créée: étudiant " + idEtudiant + " au module " + idModule);
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Erreur lors de la création de l'inscription", e);
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
            Inscription inscription = em.find(Inscription.class, id);
            if (inscription != null) {
                em.remove(inscription);
                System.out.println("✅ Inscription supprimée: " + id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Erreur lors de la suppression de l'inscription", e);
        } finally {
            em.close();
        }
    }
}