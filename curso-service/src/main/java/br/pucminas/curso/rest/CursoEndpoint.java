package br.pucminas.curso.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import br.pucminas.curso.model.Curso;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Stateless
@Path("/cursos")
@Api(value = "/cursos", tags = "cursos")
public class CursoEndpoint {
	
	@PersistenceContext(unitName = "curso-service-persistence-unit")
	private EntityManager em;

	@POST
	@Consumes("application/json; charset=utf-8; charset=utf-8")
	@ApiOperation(value = "Cria um Curso",
    response = Curso.class)
	public Response create(@ApiParam(value = "Curso a ser inserido", required = true) Curso entity) {
		em.persist(entity);
		return Response.created(
				UriBuilder.fromResource(CursoEndpoint.class)
						.path(String.valueOf(entity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") Long id) {
		Curso entity = em.find(Curso.class, id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		em.remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json; charset=utf-8; charset=utf-8")
	public Response findById(@PathParam("id") Long id) {
		TypedQuery<Curso> findByIdQuery = em
				.createQuery(
						"SELECT DISTINCT c FROM Curso c LEFT JOIN FETCH c.curriculo WHERE c.id = :entityId ORDER BY c.id",
						Curso.class);
		findByIdQuery.setParameter("entityId", id);
		Curso entity;
		try {
			entity = findByIdQuery.getSingleResult();
		} catch (NoResultException nre) {
			entity = null;
		}
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(entity).build();
	}

	@GET
	@Produces("application/json; charset=utf-8; charset=utf-8")
	public List<Curso> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		TypedQuery<Curso> findAllQuery = em
				.createQuery(
						"SELECT DISTINCT c FROM Curso c LEFT JOIN FETCH c.curriculo ORDER BY c.id",
						Curso.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<Curso> results = findAllQuery.getResultList();
		return results;
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json; charset=utf-8; charset=utf-8")
	public Response update(@PathParam("id") Long id, Curso entity) {
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!id.equals(entity.getId())) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (em.find(Curso.class, id) == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		try {
			entity = em.merge(entity);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT)
					.entity(e.getEntity()).build();
		}

		return Response.noContent().build();
	}
}
