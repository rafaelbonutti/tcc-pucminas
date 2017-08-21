package br.pucminas.web.consul;

import java.net.URI;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

@Named
@RequestScoped
public abstract class ServiceDiscovery {

    private WebTarget professorService;
    private WebTarget cursoService;
    private WebTarget alunoService;
    private WebTarget disciplinaService;
    private WebTarget curriculoService;
    private WebTarget gradeCurricularService;
    
    public abstract String getProfessorServiceURI();
    public abstract String getCursoServiceURI();
    public abstract String getAlunoServiceURI();
    public abstract String getDisciplinaServiceURI();
    public abstract String getGradeCurricularServiceURI();
    public abstract String getCurriculoServiceURI();
    
    public WebTarget getProfessorService() {
        if (null == professorService) {
            professorService = ClientBuilder
                    .newClient()
                    .target(
                            UriBuilder.fromUri(URI.create(getProfessorServiceURI()))
                                    .path("/rest/professores")
                                    .build()
                    );
        }

        return professorService;
    }
    
    public WebTarget getCursoService() {
        if (null == cursoService) {
        	cursoService = ClientBuilder
                    .newClient()
                    .target(
                            UriBuilder.fromUri(URI.create(getCursoServiceURI()))
                                    .path("/rest/cursos")
                                    .build()
                    );
        }

        return cursoService;
    }
    
    public WebTarget getAlunoService() {
        if (null == alunoService) {
        	alunoService = ClientBuilder
                    .newClient()
                    .target(
                            UriBuilder.fromUri(URI.create(getAlunoServiceURI()))
                                    .path("/rest/alunos")
                                    .build()
                    );
        }

        return alunoService;
    }
    
    public WebTarget getDisciplinaService() {
        if (null == disciplinaService) {
        	disciplinaService = ClientBuilder
                    .newClient()
                    .target(
                            UriBuilder.fromUri(URI.create(getDisciplinaServiceURI()))
                                    .path("/rest/disciplinas")
                                    .build()
                    );
        }

        return disciplinaService;
    }
    
    public WebTarget getGradeCurricularService() {
        if (null == gradeCurricularService) {
        	gradeCurricularService = ClientBuilder
                    .newClient()
                    .target(
                            UriBuilder.fromUri(URI.create(getGradeCurricularServiceURI()))
                                    .path("/rest/gradescurriculares")
                                    .build()
                    );
        }

        return gradeCurricularService;
    }
    
    public WebTarget getCurriculoService() {
        if (null == curriculoService) {
        	curriculoService = ClientBuilder
                    .newClient()
                    .target(
                            UriBuilder.fromUri(URI.create(getGradeCurricularServiceURI()))
                                    .path("/rest/curriculos")
                                    .build()
                    );
        }

        return curriculoService;
    }

}
