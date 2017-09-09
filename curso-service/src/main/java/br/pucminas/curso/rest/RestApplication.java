package br.pucminas.curso.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@ApplicationPath("/rest")
@SwaggerDefinition(
		info = @Info(
				description = "Gerencia Cursos, Currículos e Grades Curriculares",
				version = "1.0.0",
				title = "Cursos Microservice"
				),
		basePath = "/rest",
		consumes = {"application/json"},
		produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML},
		schemes = {SwaggerDefinition.Scheme.HTTP}
		)
public class RestApplication extends Application {
}