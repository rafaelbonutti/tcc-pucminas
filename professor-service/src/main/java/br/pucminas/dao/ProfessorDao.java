package br.pucminas.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import br.pucminas.model.Professor;

/**
 * DAO for Professor
 */
@Stateless
public class ProfessorDao {
	@PersistenceContext(unitName = "professor-service-persistence-unit")
	private EntityManager em;

	public void create(Professor entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		Professor entity = em.find(Professor.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public Professor findById(Long id) {
		return em.find(Professor.class, id);
	}

	public Professor update(Professor entity) {
		return em.merge(entity);
	}

	public List<Professor> listAll(Integer startPosition, Integer maxResult) {
		TypedQuery<Professor> findAllQuery = em.createQuery(
				"SELECT DISTINCT p FROM Professor p ORDER BY p.id",
				Professor.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}
}
