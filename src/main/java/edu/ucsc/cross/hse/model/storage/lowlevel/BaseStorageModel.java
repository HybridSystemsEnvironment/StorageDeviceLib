package edu.ucsc.cross.hse.model.storage.lowlevel;

import java.util.ArrayList;
import java.util.HashMap;

import com.carrotsearch.sizeof.RamUsageEstimator;

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
	GeneralQueue writeQueue;
	GeneralQueue readQueue;
	HashMap<PendingResponse, Object> pendingResponses;
	ArrayList<PendingResponse> orderedResponses;
	HashMap<Object, Object> storedContent;
	public PendingResponse pendingTask;

	public BaseStorageModel(BaseStorageState state, StorageParameters storeage_parameters,
	QueueProperties write_queue_properties, QueueProperties read_queue_properties)
	{
		super(state);
		storageParams = storeage_parameters;
		writeQueue = new GeneralQueue(write_queue_properties);
		readQueue = new GeneralQueue(read_queue_properties);
		pendingResponses = new HashMap<PendingResponse, Object>();
		storedContent = new HashMap<Object, Object>();
		orderedResponses = new ArrayList<PendingResponse>();
	}

	@Override
	public PendingResponse requestWrite(Object path, Object content)
	{
		PendingResponse response = screenRequest(writeQueue);
		if (response != null)
		{
			orderedResponses.add(response);
			pendingResponses.put(response, path);
			writeQueue.addContent(path, content);
		}
		return response;
	}

	@Override
	public PendingResponse requestRead(Object path)
	{
		PendingResponse response = screenRequest(readQueue);
		if (response != null)
		{
			pendingResponses.put(response, path);
			readQueue.addContent(path, null);
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
			completeWriteProc();
		} else
		{
			assignJob(arg1);

		}
	}

	public void cleanLastJob()
	{
		if (pendingTask != null)
		{
			pendingTask.writeToPath(true);
			orderedResponses.remove(pendingTask);
			pendingResponses.remove(pendingTask);
			pendingTask = null;
		}
	}

	public PendingResponse screenRequest(GeneralQueue queue)
	{
		PendingResponse maybe = null;
		if (!queue.isFull())
		{
			maybe = new PendingResponse();
		}
		return maybe;
	}

	public void assignJob(BaseStorageState arg1)
	{
		try
		{
			pendingTask = orderedResponses.get(0);
			arg1.pendingDataTransfer = RamUsageEstimator.sizeOfAll(pendingTask);
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

		BaseStorageModel m = new BaseStorageModel(new BaseStorageState(), new StorageParameters(100, 100),
		new QueueProperties(130), new QueueProperties(130));
		m.requestWrite("daffafs", "DSSDSdDSD");
		m.requestWrite("dafsfafs", "DSsSDSdDSD");
		m.requestWrite("dafsfafs", "DSSDSdDSD");
		m.requestWrite("dafsfafs", "DSSDSdDSD");
		m.requestWrite("daffafs", "DSSDSdDSD");

		systems.add(m);
		HSEnvironment env2 = HSEnvironment.create(systems, parameters, settings);
		env2.run();
		//	System.out.println(XMLParser.serializeObject(s));

	}

	@Override
	public boolean D(BaseStorageState arg0)
	{
		boolean tasksQueued = orderedResponses.size() > 0;
		boolean taskInProgress = pendingTask == null;
		boolean taskCompleted = arg0.pendingDataTransfer <= 0.0;

		boolean assignNewTask = tasksQueued && taskInProgress;
		boolean finalizeCurrentTask = taskCompleted && !taskInProgress;

		return assignNewTask || finalizeCurrentTask;
	}

	public boolean cleanupTask(BaseStorageState arg0)
	{

		boolean taskInProgress = pendingTask != null;
		boolean taskCompleted = arg0.pendingDataTransfer <= 0.0;
		boolean finalizeCurrentTask = taskCompleted && !taskInProgress;
		return taskCompleted && taskInProgress;
	}

	@Override
	public StorageDeviceStatus getStatus()
	{
		StorageDeviceStatus stat = StorageDeviceStatus.IDLE;
		if (this.writeQueue.getContent().containsKey(pendingResponses.get(pendingTask)))
		{
			stat = StorageDeviceStatus.WRITE;
		} else if (this.readQueue.getContent().containsKey(pendingResponses.get(pendingTask)))
		{
			stat = StorageDeviceStatus.READ;
		}
		return stat;
	}

	public void completeWriteProc()
	{
		Object var = pendingResponses.get(pendingTask);
		Object val = writeQueue.getContent().get(var);
		storedContent.put(var, val);
		System.out.println("Stored " + var + " -> " + val);
		cleanLastJob();
	}

}