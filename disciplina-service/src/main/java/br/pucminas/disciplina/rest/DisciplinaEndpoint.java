package br.pucminas.disciplina.rest;

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

import br.pucminas.disciplina.model.Disciplina;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Stateless
@Path("/disciplinas")
@Api(value = "/disciplinas", tags = "disciplinas")
public class DisciplinaEndpoint {
	@PersistenceContext(unitName = "disciplina-service-persistence-unit")
	private EntityManager em;

	@POST
	@Consumes("application/json; charset=utf-8")
	@ApiOperation(value = "Cria uma Disciplina",
	response = Disciplina.class)
	@ApiResponses(
			@ApiResponse(
					code=201,
					message="Disciplina inserida com sucesso",
					response = Disciplina.class))
	public Response create(@ApiParam(value = "Disciplina a ser inserida", required = true) Disciplina entity) {
		em.persist(entity);
		return Response.created(
				UriBuilder.fromResource(DisciplinaEndpoint.class)
				.path(String.valueOf(entity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	@ApiOperation( 
			value = "Exclui uma Disciplina pelo ID",  
			response = Disciplina.class
			)
	@ApiResponses( {
		@ApiResponse( code = 200, message = "Operação realizada com sucesso" ),
		@ApiResponse( code = 404, message = "A Disciplina não existe" )    
	} )
	public Response deleteById(@PathParam("id") Long id) {
		Disciplina entity = em.find(Disciplina.class, id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		em.remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json; charset=utf-8")
	@ApiOperation( 
			value = "Recupera uma Disciplina pelo ID",  
			response = Disciplina.class
			)
	@ApiResponses( {
		@ApiResponse( code = 200, message = "Operação realizada com sucesso" ),
		@ApiResponse( code = 404, message = "A Disciplina não existe" )
	} )
	public Response findById(@PathParam("id") Long id) {
		TypedQuery<Disciplina> findByIdQuery = em
				.createQuery(
						"SELECT DISTINCT d FROM Disciplina d WHERE d.id = :entityId ORDER BY d.id",
						Disciplina.class);
		findByIdQuery.setParameter("entityId", id);
		Disciplina entity;
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
	@Produces("application/json; charset=utf-8")
	@ApiOperation( 
			value = "Lista todos as Disciplinas",
			notes = "Lista todos as Disciplinas. Suporta paginação dos resultados",
			response = Disciplina.class, 
			responseContainer = "List"
			)
	@ApiResponses(@ApiResponse( code = 200, message = "Operação realizada com sucesso" ))
	public List<Disciplina> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		TypedQuery<Disciplina> findAllQuery = em.createQuery(
				"SELECT DISTINCT d FROM Disciplina d ORDER BY d.id",
				Disciplina.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<Disciplina> results = findAllQuery.getResultList();
		return results;
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json; charset=utf-8")
	@ApiOperation( 
			value = "Atualiza uma Disciplina pelo ID",  
			response = Disciplina.class
			)
	@ApiResponses( {
		@ApiResponse( code = 200, message = "Operação realizada com sucesso" ),
		@ApiResponse( code = 400, message = "A Disciplina ou ID está nulo" ),
		@ApiResponse( code = 404, message = "A Disciplina não existe" ),
		@ApiResponse( code = 409, message = "ID diferente do ID da Disciplina informada" )
	} )
	public Response update(@PathParam("id") Long id, Disciplina entity) {
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!id.equals(entity.getId())) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (em.find(Disciplina.class, id) == null) {
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
