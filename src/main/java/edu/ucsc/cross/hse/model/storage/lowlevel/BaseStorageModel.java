package edu.ucsc.cross.hse.model.storage.lowlevel;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ucsc.cross.hse.core.environment.EnvironmentSettings;
import edu.ucsc.cross.hse.core.environment.ExecutionParameters;
import edu.ucsc.cross.hse.core.environment.HSEnvironment;
import edu.ucsc.cross.hse.core.logging.Console;
import edu.ucsc.cross.hse.core.modeling.HybridSystem;
import edu.ucsc.cross.hse.core.modeling.SystemSet;
import edu.ucsc.cross.hse.model.storage.parameters.StorageParameters;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;

public class BaseStorageModel extends HybridSystem<BaseStorageState> implements StorageDevice
{

	StorageParameters storageParams;
	ArrayList<TaskData> tasks;
	TaskData currentTask;
	HashMap<Object, Object> storedContent;

	public BaseStorageModel(BaseStorageState state, StorageParameters storeage_parameters)
	{
		super(state);
		storageParams = storeage_parameters;
		tasks = new ArrayList<TaskData>();
		storedContent = new HashMap<Object, Object>();
	}

	@Override
	public PendingResponse requestWrite(Object path, Object content)
	{
		PendingResponse response = screenRequest();
		if (response != null)
		{
			TaskData task = new TaskData(response, path, content, StorageInstruction.STORE);
			this.tasks.add(task);
		}
		return response;
	}

	@Override
	public PendingResponse requestRead(Object path)
	{
		PendingResponse response = screenRequest();
		if (response != null)
		{
			TaskData task = new TaskData(response, path, null, StorageInstruction.LOAD);
			this.tasks.add(task);
		}
		return response;
	}

	@Override
	public boolean C(BaseStorageState arg0)
	{
		return arg0.pendingDataTransfer >= 0.0;
		//return true;
	}

	@Override
	public void F(BaseStorageState arg0, BaseStorageState arg1)
	{
		arg1.pendingDataTransfer = -this.storageParams.writeSpeed;
	}

	@Override
	public void G(BaseStorageState arg0, BaseStorageState arg1)
	{
		if (cleanupTask(arg0))
		{
			arg1.totalDataTransferred = (double) currentTask.value;
			completeWriteProc();
		} else
		{
			assignJob(arg1);
		}
	}

	public void cleanLastJob()
	{
		if (currentTask != null)
		{
			currentTask.callLink.writeToPath(true);
			tasks.remove(currentTask);
			currentTask = null;
		}
	}

	public PendingResponse screenRequest()
	{
		PendingResponse maybe = null;
		//if (!(tasks.size() < this.storageParams.queueSize))
		{
			maybe = new PendingResponse();
		}
		return maybe;
	}

	public void assignJob(BaseStorageState arg1)
	{
		try
		{
			currentTask = tasks.get(0);
			arg1.pendingDataTransfer = (double) currentTask.value;
		} catch (Exception e)
		{
			System.out.println("nojobs");
		}
	}

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

		BaseStorageModel m = new BaseStorageModel(new BaseStorageState(), new StorageParameters(100, 100, 30));

		systems.add(m);
		HSEnvironment env2 = HSEnvironment.create(systems, parameters, settings);
		env2.run();
		//	System.out.println(XMLParser.serializeObject(s));

	}

	@Override
	public boolean D(BaseStorageState arg0)
	{

		boolean taskCompleted = arg0.pendingDataTransfer <= 0.0;
		boolean taskInProgress = currentTask != null;
		boolean tasksPending = tasks.size() > 1;
		boolean finalizeCurrentTask = taskCompleted && taskInProgress;
		boolean assignNewTask = !taskInProgress && tasksPending;
		return assignNewTask || finalizeCurrentTask;
	}

	public boolean cleanupTask(BaseStorageState arg0)
	{
		boolean taskInProgress = currentTask != null;
		boolean taskCompleted = arg0.pendingDataTransfer <= 0.0;
		return taskCompleted && taskInProgress;
	}

	@Override
	public StorageDeviceStatus getStatus()
	{
		StorageDeviceStatus stat = StorageDeviceStatus.IDLE;
		if (currentTask.instruction.equals(StorageInstruction.STORE))
		{
			stat = StorageDeviceStatus.WRITE;
		} else if (currentTask.instruction.equals(StorageInstruction.LOAD))
		{
			stat = StorageDeviceStatus.READ;
		}
		return stat;
	}

	public void completeWriteProc()
	{

		storedContent.put(currentTask.variable, currentTask.value);
		System.out.println("Stored " + currentTask.variable + " -> " + currentTask.value);
		cleanLastJob();
		tasks.remove(currentTask);
	}

	@Override
	public boolean changeStatus(StorageDeviceStatus status)
	{
		// TODO Auto-generated method stub
		return false;
	}

}