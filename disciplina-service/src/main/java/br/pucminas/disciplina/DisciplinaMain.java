package br.pucminas.disciplina;

import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.topology.Advertise;

@Advertise(value="disciplina-service", tags={"urlprefix-/rest/disciplinas"})
public class DisciplinaMain {

	public static void main(String[] args) throws Exception {
		Swarm swarm = new Swarm();
		swarm.start();
		swarm.deploy();
	}
}