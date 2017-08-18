package br.pucminas.professor;

import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.topology.Advertise;

@Advertise("professor-service")
public class ProfessorMain {

	public static void main(String[] args) throws Exception {
		Swarm swarm = new Swarm();
		swarm.start();
		swarm.deploy();
	}
}