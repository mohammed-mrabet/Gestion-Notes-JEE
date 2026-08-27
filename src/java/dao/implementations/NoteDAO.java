package dao.implementations;

import beans.Etudiant;
import beans.Note;
import dao.interfaces.INoteDAO;
import exceptions.DAOException;
import utils.JpaUtil;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO implements INoteDAO {
    
    @Override
    public Note findById(int id) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Note.class, id);
        } catch (Exception e) {
            throw new DAOException("Erreur findById", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Note> findByEtudiant(int idEtudiant) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT n FROM Note n WHERE n.etudiant.id = :id ORDER BY n.module.nom", Note.class)
                    .setParameter("id", idEtudiant)
                    .getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur findByEtudiant", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Note> findByMatiere(int idMatiere) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            System.out.println("🔍 NoteDAO.findByMatiere: " + idMatiere);
            
            TypedQuery<Note> query = em.createQuery(
                "SELECT n FROM Note n JOIN FETCH n.etudiant e WHERE n.module.id = :id ORDER BY e.nom, e.prenom", 
                Note.class);
            query.setParameter("id", idMatiere);
            List<Note> result = query.getResultList();
            System.out.println("📊 NoteDAO: " + result.size() + " notes trouvées pour matière " + idMatiere);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException("Erreur findByMatiere: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Etudiant> findEtudiantsByModuleDirect(int idModule) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            System.out.println("🔍 NoteDAO.findEtudiantsByModuleDirect: " + idModule);
            
            String sql = "SELECT e.id_utilisateur, u.nom, u.prenom, e.cne, e.classe " +
                         "FROM inscriptions i " +
                         "JOIN etudiants e ON i.id_etudiant = e.id_utilisateur " +
                         "JOIN utilisateurs u ON e.id_utilisateur = u.id " +
                         "WHERE i.id_module = ?";
            
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, idModule);
            
            List<Object[]> results = query.getResultList();
            System.out.println("📊 Résultats bruts: " + results.size());
            
            List<Etudiant> etudiants = new ArrayList<>();
            for (Object[] row : results) {
                Etudiant etu = new Etudiant();
                etu.setId(((Number) row[0]).intValue());
                etu.setNom((String) row[1]);
                etu.setPrenom((String) row[2]);
                etu.setCne((String) row[3]);
                etu.setClasse((String) row[4]);
                etudiants.add(etu);
                
                System.out.println("   + " + etu.getPrenom() + " " + etu.getNom());
            }
            
            return etudiants;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException("Erreur lors de la récupération des étudiants", e);
        } finally {
            em.close();
        }
    }    
    
    @Override
    public List<Note> findByProfesseur(int idProfesseur) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            System.out.println("🔍 NoteDAO.findByProfesseur: " + idProfesseur);
            
            TypedQuery<Note> query = em.createQuery(
                "SELECT n FROM Note n JOIN FETCH n.etudiant e WHERE n.professeur.id = :idProfesseur ORDER BY n.dateSaisie DESC", 
                Note.class);
            query.setParameter("idProfesseur", idProfesseur);
            List<Note> result = query.getResultList();
            System.out.println("📊 NoteDAO: " + result.size() + " notes trouvées pour professeur " + idProfesseur);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException("Erreur lors de la recherche des notes du professeur", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Note findByEtudiantAndMatiere(int idEtudiant, int idMatiere, String session) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Note> results = em.createQuery(
                "SELECT n FROM Note n WHERE n.etudiant.id = :idE AND n.module.id = :idM AND n.session = :s", Note.class)
                    .setParameter("idE", idEtudiant)
                    .setParameter("idM", idMatiere)
                    .setParameter("s", session)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            throw new DAOException("Erreur findByEtudiantAndMatiere", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Note> findAll() throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT n FROM Note n", Note.class).getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur findAll", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void create(Note note) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(note);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Erreur create", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void update(Note note) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(note);
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
            Note n = em.find(Note.class, id);
            if (n != null) em.remove(n);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Erreur delete", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public double calculerMoyenneEtudiant(int idEtudiant) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Double moyenne = em.createQuery(
                "SELECT AVG(n.note) FROM Note n WHERE n.etudiant.id = :id", Double.class)
                    .setParameter("id", idEtudiant)
                    .getSingleResult();
            return moyenne != null ? Math.round(moyenne * 100.0) / 100.0 : 0.0;
        } catch (Exception e) {
            throw new DAOException("Erreur calcul moyenne", e);
        } finally {
            em.close();
        }
    }
}