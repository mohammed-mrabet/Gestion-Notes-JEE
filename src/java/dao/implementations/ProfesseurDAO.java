package dao.implementations;

import beans.Professeur;
import beans.Utilisateur;
import dao.interfaces.IProfesseurDAO;
import exceptions.DAOException;
import utils.JpaUtil;

import jakarta.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

public class ProfesseurDAO implements IProfesseurDAO {
    
    @Override
    public Professeur findById(int id) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Professeur.class, id);
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la recherche du professeur", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Professeur> findAll() throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.role = :role", Utilisateur.class);
            query.setParameter("role", Utilisateur.Role.PROFESSEUR);
            
            return query.getResultList().stream()
                .map(u -> (Professeur) u)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la récupération des professeurs", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Professeur> findBySpecialite(String specialite) throws DAOException {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Professeur> query = em.createQuery(
                "SELECT p FROM Professeur p WHERE p.specialite = :spec", Professeur.class);
            query.setParameter("spec", specialite);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la recherche par spécialité", e);
        } finally {
            em.close();
        }
    }
}