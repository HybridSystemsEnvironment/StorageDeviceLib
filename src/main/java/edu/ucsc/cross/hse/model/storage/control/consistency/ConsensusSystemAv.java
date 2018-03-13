package edu.ucsc.cross.hse.model.storage.control.consistency;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

import edu.ucsc.cross.hse.core.chart.ChartType;
import edu.ucsc.cross.hse.core.chart.ChartUtils;
import edu.ucsc.cross.hse.core.environment.EnvironmentSettings;
import edu.ucsc.cross.hse.core.environment.ExecutionParameters;
import edu.ucsc.cross.hse.core.environment.HSEnvironment;
import edu.ucsc.cross.hse.core.figure.Figure;
import edu.ucsc.cross.hse.core.logging.Console;
import edu.ucsc.cross.hse.core.modeling.SystemSet;
import edu.ucsc.cross.hse.core.trajectory.HybridTime;
import edu.ucsc.cross.hse.core.trajectory.TrajectorySet;
import edu.ucsc.cross.hse.core.variable.RandomVariable;
import edu.ucsc.cross.hse.lib.network.Network;
import edu.ucsc.cross.hse.lib.network.Node;
import edu.ucsc.cross.hse.model.network.ideal.IdealNetwork;
import edu.ucsc.cross.hse.model.node.simple.NetworkNode;
import edu.ucsc.cross.hse.model.storage.lowlevel.BaseStorageModel;
import edu.ucsc.cross.hse.model.storage.lowlevel.BaseStorageState;
import edu.ucsc.cross.hse.model.storage.parameters.StorageParameters;
import edu.ucsc.cross.hse.model.storage.user.SensorParameters;
import edu.ucsc.cross.hse.model.storage.user.SensorState;
import edu.ucsc.cross.hse.model.storage.user.SensorSystem;

public class ConsensusSystemAv
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

		BroadcastSystemAv bs = new BroadcastSystemAv(netb);
		// create environment
		HSEnvironment env = HSEnvironment.create(systems, parameters, settings);
		//BandwidthNetwork net = new BandwidthNetwork(new BandwidthNetworkState(),
		//new BandwidthParameters(1000, BandwidthConfiguration.CONSTANT));
		IdealNetwork net = new IdealNetwork();
		//DelayedNetworkSystem net = new DelayedNetworkSystem(new DelayedNetworkState(),
		//	new DelayedNetworkParameters(.5, 1.5);
		SystemSet agents = createAgents(net, bs, 5, 2, 7, 15, 21);
		connectRandomly(net, 1, 2);
		connectStorage(bs);
		env.getSystems().add(agents);
		env.getSystems().add(net);
		env.getSystems().add(bs);
		env.getSystems().add(netb);
		env.run();
		nodeChart2(env.run()).display();
		//storageChart(env.getTrajectories()).display();

		//nodeChart2(env.run()).display();
		//nodeChart3(env.run()).display();
		storageChart(env.getTrajectories()).display();
		//storageChart(env.getTrajectories()).display();

		//nodeChart2(env.run()).display();
		//nodeChart3(env.run()).display();
		storageChart(env.getTrajectories()).display();

		//	System.out.println(XMLParser.serializeObject(s));

	}

	public static SystemSet createAgents(Network net, BroadcastSystemAv internet, int quantity, double min_send,
	double max_send, double min_size, double max_size)
	{
		SystemSet set = new SystemSet();
		for (int i = 0; i < quantity; i++)
		{
			StorageParameters storParam = new StorageParameters(120, 102, 5020022);

			BaseStorageModel bss = new BaseStorageModel(new BaseStorageState(), storParam);
			NetworkNode node = new NetworkNode(internet.distributedNetwork);
			StorageConsistencyControl con = new StorageConsistencyControl(bss, internet, node);
			internet.controllers.add(con);

			SensorSystem s = new SensorSystem(new SensorState(), new SensorParameters(12.0, 3 * Math.random() + 2.0),
			con);
			set.add(bss, s, con);
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

	public static void connectStorage(BroadcastSystemAv net)
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

	public static Figure storageChart(TrajectorySet solution)
	{
		XYDataset dataset = ChartUtils.createXYDataset(solution, HybridTime.TIME, "dataGenerated");
		JFreeChart plot = ChartUtils.createXYChart(solution, dataset, null, null, ChartType.LINE);
		ChartPanel panel = ChartUtils.createPanel(plot);
		XYDataset dataset2 = ChartUtils.createXYDataset(solution, HybridTime.TIME, "timeToNextData");
		JFreeChart plot2 = ChartUtils.createXYChart(solution, dataset2, null, null, ChartType.LINE);
		ChartPanel panel2 = ChartUtils.createPanel(plot2);
		ChartUtils.configureLabels(panel, "Time (sec)", "dataGenerated", null, false);
		ChartUtils.configureLabels(panel2, "Time (sec)", "timeToNextData", null, false);

		Figure figure = new Figure(1000, 500);
		figure.addComponent(0, 0, panel);
		figure.addComponent(1, 0, panel2);
		return figure;
	}

	//	å
	public static Figure nodeChart2(TrajectorySet solution)
	{
		XYDataset dataset = ChartUtils.createXYDataset(solution, HybridTime.TIME, "totalDataTransferred");
		JFreeChart plot = ChartUtils.createXYChart(solution, dataset, null, null, ChartType.LINE);
		ChartPanel panel = ChartUtils.createPanel(plot);
		XYDataset dataset2 = ChartUtils.createXYDataset(solution, HybridTime.TIME, "pendingDataTransfer");
		JFreeChart plot2 = ChartUtils.createXYChart(solution, dataset2, null, null, ChartType.LINE);
		ChartPanel panel2 = ChartUtils.createPanel(plot2);
		ChartUtils.configureLabels(panel, "Time (sec)", "totalDataTransferred", null, false);
		ChartUtils.configureLabels(panel2, "Time (sec)", "pendingDataTransfer", null, false);

		Figure figure = new Figure(1000, 500);
		figure.addComponent(0, 0, panel);
		figure.addComponent(1, 0, panel2);
		return figure;
	}

	public static Figure nodeChart3(TrajectorySet solution, String title)
	{
		XYDataset dataset = ChartUtils.createXYDataset(solution, HybridTime.TIME, "sizeSent");
		JFreeChart plot = ChartUtils.createXYChart(solution, dataset, null, null, ChartType.LINE);
		ChartPanel panel = ChartUtils.createPanel(plot);
		XYDataset dataset2 = ChartUtils.createXYDataset(solution, HybridTime.TIME, "timeToNextData");
		JFreeChart plot2 = ChartUtils.createXYChart(solution, dataset2, null, null, ChartType.LINE);
		ChartPanel panel2 = ChartUtils.createPanel(plot2);
		XYDataset dataset3 = ChartUtils.createXYDataset(solution, HybridTime.TIME, "dataGenerated");
		JFreeChart plot3 = ChartUtils.createXYChart(solution, dataset3, null, null, ChartType.LINE);
		ChartPanel panel3 = ChartUtils.createPanel(plot3);
		ChartUtils.configureLabels(panel, "Time (sec)", "Sent Size", null, false);
		ChartUtils.configureLabels(panel2, "Time (sec)", "timeToNextData", null, false);
		ChartUtils.configureLabels(panel3, "Time (sec)", "dataGenerated", null, false);

		Figure figure = new Figure(1000, 1500);
		figure.addComponent(0, 0, panel);
		figure.addComponent(0, 1, panel2);
		figure.addComponent(0, 2, panel3);
		figure.getTitle().setText(title);
		return figure;
	}
}
