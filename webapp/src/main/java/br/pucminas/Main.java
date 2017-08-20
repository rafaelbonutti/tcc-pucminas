package br.pucminas;

import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.topology.Advertise;

@Advertise("webapp")
public class Main {

	public static void main(String[] args) throws Exception {
		Swarm swarm = new Swarm();
		swarm.start();
		swarm.deploy();
	}
}