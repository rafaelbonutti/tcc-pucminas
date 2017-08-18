package br.pucminas.aluno;

import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.topology.Advertise;

@Advertise("aluno-service")
public class AlunoMain {

	public static void main(String[] args) throws Exception {
		Swarm swarm = new Swarm();
		swarm.start();
		swarm.deploy();
	}
}