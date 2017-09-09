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

import br.pucminas.curso.model.Curriculo;
import br.pucminas.curso.model.Curso;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Stateless
@Path("/curriculos")
@Api(value = "curriculos", tags = "curriculos")
public class CurriculoEndpoint {
	@PersistenceContext(unitName = "curso-service-persistence-unit")
	private EntityManager em;

	@POST
	@Consumes("application/json; charset=utf-8")
	@ApiOperation(value = "Cria um novo Currículo")
	@ApiResponses(
			@ApiResponse(
					code=201,
					message="Currículo inserido com sucesso",
					response = Curriculo.class))
	public Response create(@ApiParam(value = "Curriculos a ser inserido", required = true) Curriculo entity) {
		em.persist(entity);
		return Response.created(
				UriBuilder.fromResource(CurriculoEndpoint.class)
				.path(String.valueOf(entity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	@ApiOperation( 
			value = "Exclui um Currículo pelo ID",  
			response = Curso.class
			)
	@ApiResponses( {
		@ApiResponse( code = 200, message = "Operação realizada com sucesso" ),
		@ApiResponse( code = 404, message = "O Currículo não existe" )    
	} )
	public Response deleteById(@PathParam("id") Long id) {
		Curriculo entity = em.find(Curriculo.class, id);
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
			value = "Recupera um Currículo pelo ID",  
			response = Curso.class
			)
	@ApiResponses( {
		@ApiResponse( code = 200, message = "Operação realizada com sucesso" ),
		@ApiResponse( code = 404, message = "O Currículo não existe" )
	} )
	public Response findById(@PathParam("id") Long id) {
		TypedQuery<Curriculo> findByIdQuery = em
				.createQuery(
						"SELECT DISTINCT c FROM Curriculo c LEFT JOIN FETCH c.curso WHERE c.id = :entityId ORDER BY c.id",
						Curriculo.class);
		findByIdQuery.setParameter("entityId", id);
		Curriculo entity;
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
			value = "Lista todos os Cursos",
			notes = "Lista todos os Cursos. Suporta paginação dos resultados",
			response = Curso.class, 
			responseContainer = "List"
			)
	@ApiResponses(@ApiResponse( code = 200, message = "Operação realizada com sucesso" ))
	public List<Curriculo> listAll(@ApiParam( value = "Página", required = false) @QueryParam("start") Integer startPosition,
			@ApiParam( value = "Número de registros por página", required = false) @QueryParam("max") Integer maxResult)
	{
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
		final List<Curriculo> results = findAllQuery.getResultList();
		return results;
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json; charset=utf-8")
	@ApiOperation( 
			value = "Atualiza um Currículo pelo ID",  
			response = Curso.class
			)
	@ApiResponses( {
		@ApiResponse( code = 200, message = "Operação realizada com sucesso" ),
		@ApiResponse( code = 400, message = "O Currículo ou ID está nulo" ),
		@ApiResponse( code = 404, message = "O Currículo não existe" ),
		@ApiResponse( code = 409, message = "ID diferente do ID do Currículo informado" )
	} )
	public Response update(@PathParam("id") Long id, Curriculo entity) {
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!id.equals(entity.getId())) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (em.find(Curriculo.class, id) == null) {
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
