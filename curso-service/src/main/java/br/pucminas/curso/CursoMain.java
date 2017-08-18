package br.pucminas.curso;

import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.topology.Advertise;

@Advertise("curso-service")
public class CursoMain {

	public static void main(String[] args) throws Exception {
		Swarm swarm = new Swarm();
		swarm.start();
		swarm.deploy();
	}
}