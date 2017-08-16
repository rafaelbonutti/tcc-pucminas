package br.pucminas.consul;

import java.net.URI;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

/**
 * @author arungupta
 */
public abstract class ServiceDiscovery {

    private WebTarget professorService;

    public abstract String getUserServiceURI();

    public WebTarget getProfessorService() {
        if (null == professorService) {
            professorService = ClientBuilder
                    .newClient()
                    .target(
                            UriBuilder.fromUri(URI.create(getUserServiceURI()))
                                    .path("/rest/professores")
                                    .build()
                    );
        }

        return professorService;
    }

}
