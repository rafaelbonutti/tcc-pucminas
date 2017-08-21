package br.pucminas.web.consul;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ConsulServices
@ApplicationScoped
public class ConsulDiscovery extends ServiceDiscovery {

    @Inject
    @ConsulServices
    ServiceRegistry services;

    @Override
    public String getProfessorServiceURI() {
        return services.discoverServiceURI("professor-service");
    }

    @Override
    public String getCursoServiceURI() {
        return services.discoverServiceURI("curso-service");
    }
    
	@Override
	public String getGradeCurricularServiceURI() {
		return services.discoverServiceURI("curso-service");
	}

	@Override
	public String getCurriculoServiceURI() {
		return services.discoverServiceURI("curso-service");
	}

    @Override
    public String getAlunoServiceURI() {
        return services.discoverServiceURI("aluno-service");
    }
    
    @Override
    public String getDisciplinaServiceURI() {
        return services.discoverServiceURI("disciplina-service");
    }

}
