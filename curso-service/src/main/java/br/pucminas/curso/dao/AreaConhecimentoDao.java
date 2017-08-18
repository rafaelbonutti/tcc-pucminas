package br.pucminas.curso.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import br.pucminas.curso.model.AreaConhecimento;

/**
 * DAO for AreaConhecimento
 */
@Stateless
public class AreaConhecimentoDao {
	@PersistenceContext(unitName = "curso-service-persistence-unit")
	private EntityManager em;

	public void create(AreaConhecimento entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		AreaConhecimento entity = em.find(AreaConhecimento.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public AreaConhecimento findById(Long id) {
		return em.find(AreaConhecimento.class, id);
	}

	public AreaConhecimento update(AreaConhecimento entity) {
		return em.merge(entity);
	}

	public List<AreaConhecimento> listAll(Integer startPosition,
			Integer maxResult) {
		TypedQuery<AreaConhecimento> findAllQuery = em.createQuery(
				"SELECT DISTINCT a FROM AreaConhecimento a ORDER BY a.id",
				AreaConhecimento.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}
}
