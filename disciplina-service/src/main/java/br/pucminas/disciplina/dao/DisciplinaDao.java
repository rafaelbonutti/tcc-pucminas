package br.pucminas.disciplina.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import br.pucminas.disciplina.model.Disciplina;

/**
 * DAO for Disciplina
 */
@Stateless
public class DisciplinaDao {
	@PersistenceContext(unitName = "disciplina-service-persistence-unit")
	private EntityManager em;

	public void create(Disciplina entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		Disciplina entity = em.find(Disciplina.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public Disciplina findById(Long id) {
		return em.find(Disciplina.class, id);
	}

	public Disciplina update(Disciplina entity) {
		return em.merge(entity);
	}

	public List<Disciplina> listAll(Integer startPosition, Integer maxResult) {
		TypedQuery<Disciplina> findAllQuery = em.createQuery(
				"SELECT DISTINCT d FROM Disciplina d ORDER BY d.id",
				Disciplina.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}
}
