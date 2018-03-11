package edu.ucsc.cross.hse.model.storage.control.consistency;

import edu.ucsc.cross.hse.core.environment.EnvironmentSettings;
import edu.ucsc.cross.hse.core.environment.ExecutionParameters;
import edu.ucsc.cross.hse.core.environment.HSEnvironment;
import edu.ucsc.cross.hse.core.logging.Console;
import edu.ucsc.cross.hse.core.modeling.SystemSet;
import edu.ucsc.cross.hse.core.variable.RandomVariable;
import edu.ucsc.cross.hse.lib.network.Network;
import edu.ucsc.cross.hse.lib.network.Node;
import edu.ucsc.cross.hse.lib.network.framework.AgentSystem;
import edu.ucsc.cross.hse.lib.network.framework.SimulatedAgentParameters;
import edu.ucsc.cross.hse.lib.network.framework.SpammerState;
import edu.ucsc.cross.hse.model.network.ideal.IdealNetwork;
import edu.ucsc.cross.hse.model.node.simple.NetworkNode;
import edu.ucsc.cross.hse.model.storage.control.StorageController;
import edu.ucsc.cross.hse.model.storage.parameters.StorageParameters;
import edu.ucsc.cross.hse.model.storage.states.StorageState;
import edu.ucsc.cross.hse.model.storage.systems.StorageSystem;

public class ConsensusSystem
{

	/*
	 * Main class needed to run java application
	 */
	public static void main(String args[])
	{
		Console.getSettings().printIntegratorExceptions = false;
		ionAndPlot();
	}

	public static void ionAndPlot()
	{
		// initialize system set
		SystemSet systems = new SystemSet();
		// initialize environment settings 
		EnvironmentSettings settings = new EnvironmentSettings();
		settings.odeMaximumStepSize = .0005;
		settings.odeMinimumStepSize = .00000005;
		settings.eventHandlerMaximumCheckInterval = .00005;
		// initialize the execution parameters 
		ExecutionParameters parameters = new ExecutionParameters(35.0, 53320, .5);
		// initialize the node parameters
		IdealNetwork netb = new IdealNetwork();

		BroadcastSystem bs = new BroadcastSystem(netb);
		// create environment
		HSEnvironment env = HSEnvironment.create(systems, parameters, settings);
		//BandwidthNetwork net = new BandwidthNetwork(new BandwidthNetworkState(),
		//new BandwidthParameters(1000, BandwidthConfiguration.CONSTANT));
		IdealNetwork net = new IdealNetwork();
		//DelayedNetworkSystem net = new DelayedNetworkSystem(new DelayedNetworkState(),
		//	new DelayedNetworkParameters(.5, 1.5);
		SystemSet agents = createAgents(net, bs, 15, 1.2, 1.5, 50, 100);
		connectRandomly(net, 3, 8);
		connectStorage(bs);
		env.getSystems().add(agents);
		env.getSystems().add(net);
		env.getSystems().add(bs);
		env.getSystems().add(netb);
		env.run();
		//nodeChart2(env.run()).display();
		//storageChart(env.getTrajectories()).display();

		//nodeChart2(env.run()).display();
		// snodeChart3(env.run()).display();
		//storageChart(env.getTrajectories());

		//	System.out.println(XMLParser.serializeObject(s));

	}

	public static SystemSet createAgents(Network internet, BroadcastSystem net, int quantity, double min_send,
	double max_send, double min_size, double max_size)
	{
		SystemSet set = new SystemSet();
		for (int i = 0; i < quantity; i++)
		{
			StorageParameters storParam = new StorageParameters(10000, 10000);
			NetworkNode storNode = new NetworkNode(net.distributedNetwork);
			net.distributedNetwork.getTopology().addVertex(storNode);
			ParamConsistencyController con = new ParamConsistencyController(net, storNode);
			net.controllers.add(con);
			StorageSystem store = new StorageSystem(new StorageState(), storParam, (StorageController) con);
			AgentSystem agent = new AgentSystem(new SpammerState(0.0),
			new SimulatedAgentParameters(min_send, max_send, min_size, max_size), new NetworkNode(internet),
			con.consQueue);
			internet.getTopology().addVertex(agent.localNode);
			set.add(agent, store, con);
		}
		return set;
	}

	public static void connectRandomly(Network net, int connections_per_min, int connections_per_max)
	{
		int vertexCount = net.getTopology().vertexSet().size();
		Node[] nodes = net.getTopology().vertexSet().toArray(new Node[vertexCount]);
		for (Node node : net.getTopology().vertexSet())
		{
			int connections = (int) RandomVariable.generate(connections_per_min, connections_per_max);

			for (int i = 0; i < connections; i++)
			{
				assignEdge(node, net, vertexCount, nodes);

			}
		}
	}

	public static void assignEdge(Node node, Network net, int vertexCount, Node[] nodes)
	{
		try
		{
			Node nod = nodes[(int) Math.floor(vertexCount * Math.random())];
			if (nod.equals(node))
			{
				assignEdge(node, net, vertexCount, nodes);
			} else
			{
				System.out.println(node + " -> " + nod);
				net.getTopology().addEdge(node, nod);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			assignEdge(node, net, vertexCount, nodes);
		}
	}

	public static void connectStorage(BroadcastSystem net)
	{
		for (Node conn : net.distributedNetwork.getTopology().vertexSet())
		{
			for (Node conn2 : net.distributedNetwork.getTopology().vertexSet())
			{

				if (!conn.equals(conn2))
				{
					net.distributedNetwork.getTopology().addEdge(conn, conn2);
				}
			}
		}
	}
}
