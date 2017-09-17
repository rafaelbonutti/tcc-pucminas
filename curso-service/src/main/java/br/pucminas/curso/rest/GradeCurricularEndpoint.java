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
import br.pucminas.curso.model.GradeCurricular;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Stateless
@Path("/gradescurriculares")
@Api(value = "/gradescurriculares", tags = "gradescurriculares")
public class GradeCurricularEndpoint {

	@PersistenceContext(unitName = "curso-service-persistence-unit")
	private EntityManager em;

	@POST
	@Consumes("application/json")
	@ApiOperation(value = "Cria uma Grade Curricular para um determinado Curso",
	response = GradeCurricular.class)
	public Response create(@ApiParam(value = "Grade Curricular a ser inserida", required = true) GradeCurricular entity) {
		em.persist(entity);
		return Response.created(
				UriBuilder.fromResource(GradeCurricularEndpoint.class)
				.path(String.valueOf(entity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	@ApiOperation( 
			value = "Exclui uma Grade Curricular pelo ID",  
			response = Curso.class
			)
	@ApiResponses( {
		@ApiResponse( code = 200, message = "Operação realizada com sucesso" ),
		@ApiResponse( code = 404, message = "A Grade Curricular não existe" )    
	} )
	public Response deleteById(@PathParam("id") Long id) {
		GradeCurricular entity = em.find(GradeCurricular.class, id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		em.remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	@ApiOperation( 
			value = "Recupera uma Grade Curricular pelo ID",  
			response = Curso.class
			)
	@ApiResponses( {
		@ApiResponse( code = 200, message = "Operação realizada com sucesso" ),
		@ApiResponse( code = 404, message = "A Grade Curricular não existe" )
	} )
	public Response findById(@ApiParam( value = "ID da Grade Curricular", required = true) @PathParam("id") Long id) {
		TypedQuery<GradeCurricular> findByIdQuery = em
				.createQuery(
						"SELECT DISTINCT g FROM GradeCurricular g LEFT JOIN FETCH g.curriculo WHERE g.id = :entityId ORDER BY g.id",
						GradeCurricular.class);
		findByIdQuery.setParameter("entityId", id);
		GradeCurricular entity;
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
	@Produces("application/json")
	@ApiOperation( 
			value = "Lista todos as Grades Curriculares",
			notes = "Lista todos as Grades Curriculares. Suporta paginação dos resultados",
			response = Curso.class, 
			responseContainer = "List"
			)
	@ApiResponses(@ApiResponse( code = 200, message = "Operação realizada com sucesso" ))
	public List<GradeCurricular> listAll(@ApiParam( value = "Página", required = false) @QueryParam("start") Integer startPosition,
			@ApiParam( value = "Número de registros por página", required = false) @QueryParam("max") Integer maxResult)
	{
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
		final List<GradeCurricular> results = findAllQuery.getResultList();
		return results;
	}

	@GET
	@Produces("application/json")
	@Path("/curriculo/{id:[0-9][0-9]*}")
	@ApiOperation( 
			value = "Lista todos as Grades Curriculares de um determinado Currículo/Curso",
			notes = "Lista todos as Grades Curriculares de um determinado Currículo/Curso. Suporta paginação dos resultados",
			response = Curso.class, 
			responseContainer = "List"
			)
	@ApiResponses(@ApiResponse( code = 200, message = "Operação realizada com sucesso" ))
	public List<GradeCurricular> listAllByCurriculo(
			@PathParam("id") Long curriculoId) {
		TypedQuery<GradeCurricular> findAllQuery = em
				.createQuery(
						"SELECT DISTINCT g FROM GradeCurricular g LEFT JOIN FETCH g.curriculo LEFT JOIN FETCH g.disciplina WHERE g.curriculo.id = :curriculoId ORDER BY g.id",
						GradeCurricular.class)
				.setParameter("curriculoId", curriculoId);

		final List<GradeCurricular> results = findAllQuery.getResultList();
		return results;
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	@ApiOperation( 
			value = "Atualiza uma Grade Curricular pelo ID",  
			response = GradeCurricular.class
			)
	@ApiResponses( {
		@ApiResponse( code = 200, message = "Operação realizada com sucesso" ),
		@ApiResponse( code = 400, message = "A Grade Curricular ou ID está nulo" ),
		@ApiResponse( code = 404, message = "A Grade Curricular não existe" ),
		@ApiResponse( code = 409, message = "ID diferente do ID da Grade Curricular informada" )
	} )
	public Response update(@PathParam("id") Long id, GradeCurricular entity) {
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!id.equals(entity.getId())) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (em.find(GradeCurricular.class, id) == null) {
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
