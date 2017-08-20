package br.pucminas.aluno.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
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

import br.pucminas.aluno.dao.AlunoDao;
import br.pucminas.aluno.model.Aluno;

import javax.ws.rs.core.UriBuilder;

@Stateless
@Path("/alunos")
public class AlunoEndpoint {
	
	@Inject
	private AlunoDao alunoDao;

	@POST
	@Consumes("application/json; charset=utf-8")
	public Response create(Aluno entity) {
		alunoDao.create(entity);
		return Response.created(
				UriBuilder.fromResource(AlunoEndpoint.class)
				.path(String.valueOf(entity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") Long id) {
		Aluno entity = alunoDao.findById(id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		alunoDao.deleteById(id);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json; charset=utf-8")
	public Response findById(@PathParam("id") Long id) {
		Aluno entity = null;
		try {
			entity = alunoDao.findById(id);
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
	public List<Aluno> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		return alunoDao.listAll(startPosition, maxResult);
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json; charset=utf-8")
	public Response update(@PathParam("id") Long id, Aluno entity) {
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!id.equals(entity.getId())) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (alunoDao.findById(id) == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		try {
			entity = alunoDao.update(entity);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT)
					.entity(e.getEntity()).build();
		}

		return Response.noContent().build();
	}
}
