package br.pucminas.curso.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import br.pucminas.curso.model.GradeCurricular;

/**
 * DAO for GradeCurricular
 */
@Stateless
public class GradeCurricularDao {
	@PersistenceContext(unitName = "curso-service-persistence-unit")
	private EntityManager em;

	public void create(GradeCurricular entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		GradeCurricular entity = em.find(GradeCurricular.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public GradeCurricular findById(Long id) {
		return em.find(GradeCurricular.class, id);
	}

	public GradeCurricular update(GradeCurricular entity) {
		return em.merge(entity);
	}

	public List<GradeCurricular> listAll(Integer startPosition,
			Integer maxResult) {
		TypedQuery<GradeCurricular> findAllQuery = em
				.createQuery(
						"SELECT DISTINCT g FROM GradeCurricular g LEFT JOIN FETCH g.curriculo ORDER BY g.id",
						GradeCurricular.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}
}
