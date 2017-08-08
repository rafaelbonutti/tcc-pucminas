package br.pucminas.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import br.pucminas.model.Aluno;

/**
 * DAO for Aluno
 */
@Stateless
public class AlunoDao {
	
	@PersistenceContext(unitName = "alunosPU")
	private EntityManager em;

	public void create(Aluno entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		Aluno entity = em.find(Aluno.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	public Aluno findById(Long id) {
		return em.find(Aluno.class, id);
	}

	public Aluno update(Aluno entity) {
		return em.merge(entity);
	}

	public List<Aluno> listAll(Integer startPosition, Integer maxResult) {
		TypedQuery<Aluno> findAllQuery = em.createQuery(
				"SELECT DISTINCT a FROM Aluno a ORDER BY a.id", Aluno.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}
}
