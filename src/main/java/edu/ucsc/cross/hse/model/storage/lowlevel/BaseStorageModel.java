package edu.ucsc.cross.hse.model.storage.lowlevel;

import java.util.ArrayList;
import java.util.HashMap;

import com.carrotsearch.sizeof.RamUsageEstimator;

import edu.ucsc.cross.hse.core.modeling.HybridSystem;
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
			arg1.totalDataTransferred += RamUsageEstimator.sizeOf(currentTask.variable);
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
			tasks.remove(0);
		} catch (Exception e)
		{
			System.out.println("nojobs");
		}
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