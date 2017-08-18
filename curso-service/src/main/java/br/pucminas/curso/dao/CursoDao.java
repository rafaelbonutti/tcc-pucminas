package br.pucminas.curso.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import br.pucminas.curso.model.Curso;

/**
 * DAO for Curso
 */
@Stateless
public class CursoDao {
	@PersistenceContext(unitName = "curso-service-persistence-unit")
	private EntityManager em;

	public void create(Curso entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		Curso entity = em.find(Curso.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public Curso findById(Long id) {
		return em.find(Curso.class, id);
	}

	public Curso update(Curso entity) {
		return em.merge(entity);
	}

	public List<Curso> listAll(Integer startPosition, Integer maxResult) {
		TypedQuery<Curso> findAllQuery = em
				.createQuery(
						"SELECT DISTINCT c FROM Curso c LEFT JOIN FETCH c.areaConhecimento ORDER BY c.id",
						Curso.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}
}
