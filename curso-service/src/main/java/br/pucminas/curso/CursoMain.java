package br.pucminas.curso;

import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.topology.Advertise;

@Advertise(value="curso-service", tags={"urlprefix-/rest/cursos","urlprefix-/rest/curriculos","urlprefix-/rest/gradescurriculares"})
public class CursoMain {

	public static void main(String[] args) throws Exception {
		Swarm swarm = new Swarm();
		swarm.start();
		swarm.deploy();
	}
}