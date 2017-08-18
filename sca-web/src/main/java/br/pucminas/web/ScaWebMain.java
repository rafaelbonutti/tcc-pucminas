package br.pucminas.web;

import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.topology.Advertise;

@Advertise("sca-web")
public class ScaWebMain {

	public static void main(String[] args) throws Exception {
		Swarm swarm = new Swarm();
		swarm.start();
		swarm.deploy();
	}
}