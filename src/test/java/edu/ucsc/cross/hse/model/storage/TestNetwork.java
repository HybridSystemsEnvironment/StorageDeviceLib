package edu.ucsc.cross.hse.model.storage;

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
import edu.ucsc.cross.hse.core.modelling.SystemSet;
import edu.ucsc.cross.hse.core.trajectory.HybridTime;
import edu.ucsc.cross.hse.core.trajectory.TrajectorySet;
import edu.ucsc.cross.hse.model.storage.control.FIFOStorageController;
import edu.ucsc.cross.hse.model.storage.parameters.StorageParameters;
import edu.ucsc.cross.hse.model.storage.states.StorageState;
import edu.ucsc.cross.hse.model.storage.systems.StorageSystem;

public class TestNetwork
{

	/*
	 * Main class needed to run java application
	 */
	public static void main(String args[])
	{
		Console.getSettings().printIntegratorExceptions = false;
		ionAndPlot();
	}

	//	public static HashMap<BroadcastNodeState, Double> computeMaxTransmissionTimes(TrajectorySet trajectories)
	//	{
	//		// Get all node state trajectories 
	//		HashMap<BroadcastNodeState, HybridTrajectory<BroadcastNodeState>> nodeTrajectoryMap = trajectories
	//		.getHybridTrajectoryMapByObject(BroadcastNodeState.class);
	//
	//		// initialize maximum transmission time mapping;
	//		HashMap<BroadcastNodeState, Double> maxTransmissionTime = new HashMap<BroadcastNodeState, Double>();
	//
	//		// iterate through node states that have stored trajectories
	//		for (BroadcastNodeState state : nodeTrajectoryMap.keySet())
	//		{
	//			// get tge trajectory for the current node state
	//			HybridTrajectory<BroadcastNodeState> stateTrajectory = nodeTrajectoryMap.get(state);
	//			// compute the maximum transmission time of the node
	//			Double maxTransmitTime = 0.0;
	//			// get the state trajectory as a time domain mapping 
	//			HashMap<HybridTime, BroadcastNodeState> trajectoryMap = stateTrajectory.getDataPointMap();
	//			// iterate through the state trajectory to find the max time
	//			for (HybridTime time : trajectoryMap.keySet())
	//			{
	//				// get the state at the current trajectory time
	//				BroadcastNodeState trajState = trajectoryMap.get(time);
	//				// check if the time to transmit is greater than current max
	//				if (trajState.transmissionTimer > maxTransmitTime)
	//				{
	//					// update the new max transmit time 
	//					maxTransmitTime = trajState.transmissionTimer;
	//				}
	//			}
	//			// store the maximum transmission time in the mapping
	//			maxTransmissionTime.put(state, maxTransmitTime);
	//		}
	//		System.out.println(XMLParser.serializeObject(maxTransmissionTime));
	//		return maxTransmissionTime;
	//	}

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
		ExecutionParameters parameters = new ExecutionParameters(45.0, 53320, .5);
		// initialize the node parameters

		// create environment
		HSEnvironment env = HSEnvironment.create(systems, parameters, settings);
		StorageParameters params = new StorageParameters(10, 10);
		FIFOStorageController sq = new FIFOStorageController();
		for (int i = 0; i < 10; i++)
		{
			sq.storageQueue.write("YEYSYADHSDHAIDHSIFHSKFLAKLFJS");
		}
		StorageSystem s = new StorageSystem(new StorageState(), params, sq);
		env.getSystems().add(s);

		nodeChart2(env.run()).display();
		//	System.out.println(XMLParser.serializeObject(s));

	}

	//	å
	public static Figure nodeChart2(TrajectorySet solution)
	{
		XYDataset dataset = ChartUtils.createXYDataset(solution, HybridTime.TIME, "storedDataSize");
		JFreeChart plot = ChartUtils.createXYChart(solution, dataset, null, null, ChartType.LINE);
		ChartPanel panel = ChartUtils.createPanel(plot);
		XYDataset dataset2 = ChartUtils.createXYDataset(solution, HybridTime.TIME, "dataToTransfer");
		JFreeChart plot2 = ChartUtils.createXYChart(solution, dataset2, null, null, ChartType.LINE);
		ChartPanel panel2 = ChartUtils.createPanel(plot2);
		ChartUtils.configureLabels(panel, "Time (sec)", "Sent Size", null, false);
		ChartUtils.configureLabels(panel2, "Time (sec)", "Received Size", null, false);

		Figure figure = new Figure(1000, 500);
		figure.addComponent(0, 0, panel);
		figure.addComponent(1, 0, panel2);
		return figure;
	}
}
