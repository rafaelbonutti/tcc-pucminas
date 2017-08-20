package br.pucminas.curso.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;

@ApplicationPath("/rest")
@SwaggerDefinition(
		info = @Info(
				description = "Gerencia Cursos, Currículos e Grade Curricular",
				version = "1.0.0",
				title = "Cursos Microservice",
				contact = @Contact(
						name = "Rafael Bonutti", 
						email = "rafael.bonutti@gmail.com"
						),
				license = @License(
						name = "Apache 2.0", 
						url = "http://www.apache.org/licenses/LICENSE-2.0"
						)
				),
		consumes = {"application/json"},
		produces = {"application/json"},
		schemes = {SwaggerDefinition.Scheme.HTTP}
		)
public class RestApplication extends Application {
}