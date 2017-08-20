package br.pucminas.curso.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import br.pucminas.curso.model.Curriculo;

/**
 * DAO for Curriculo
 */
@Stateless
public class CurriculoDao {
	@PersistenceContext(unitName = "curso-service-persistence-unit")
	private EntityManager em;

	public void create(Curriculo entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		Curriculo entity = em.find(Curriculo.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public Curriculo findById(Long id) {
		return em.find(Curriculo.class, id);
	}

	public Curriculo update(Curriculo entity) {
		return em.merge(entity);
	}

	public List<Curriculo> listAll(Integer startPosition, Integer maxResult) {
		TypedQuery<Curriculo> findAllQuery = em
				.createQuery(
						"SELECT DISTINCT c FROM Curriculo c LEFT JOIN FETCH c.curso ORDER BY c.id",
						Curriculo.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}
}
