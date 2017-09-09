package br.pucminas.disciplina.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@ApplicationPath("/rest")
@SwaggerDefinition(
		info = @Info(
				description = "Gerencia Disciplinas",
				version = "1.0.0",
				title = "Disciplinas Microservice"
				),
		basePath = "/rest",
		consumes = {MediaType.APPLICATION_JSON},
		produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML},
		schemes = {SwaggerDefinition.Scheme.HTTP}
		)
public class RestApplication extends Application {
}