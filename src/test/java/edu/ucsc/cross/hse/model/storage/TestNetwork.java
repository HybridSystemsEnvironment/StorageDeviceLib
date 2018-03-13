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
import edu.ucsc.cross.hse.core.modeling.SystemSet;
import edu.ucsc.cross.hse.core.trajectory.HybridTime;
import edu.ucsc.cross.hse.core.trajectory.TrajectorySet;
import edu.ucsc.cross.hse.model.data.objects.RealData;
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

		//		BaseStorageState state = new BaseStorageState();
		//		DependentVariable<ElectricalProperties> emmc = ConstructElectricalProfile
		//		.createDynamicVariable(ElectricalProperties.class, state, "status", StorageDeviceStatus.class, new File(""));
		//		//Console.getSettings().printIntegratorExceptions = false;
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
		ExecutionParameters parameters = new ExecutionParameters(45.0, 53320, .5);
		// initialize the node parameters

		// create environment
		HSEnvironment env = HSEnvironment.create(systems, parameters, settings);
		StorageParameters params = new StorageParameters(10, 10, 55);
		FIFOStorageController sq = new FIFOStorageController();
		for (int i = 0; i < 10; i++)
		{
			sq.storageQueue.write(new RealData("YEYSYADHSDHAIDHSIFHSKFLAKLFJS"));
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
