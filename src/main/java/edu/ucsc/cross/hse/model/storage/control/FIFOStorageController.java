package edu.ucsc.cross.hse.model.storage.control;

import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.storage.StorageDevice;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;
import edu.ucsc.cross.hse.model.storage.states.StorageQueue;

public class FIFOStorageController implements StorageController
{

	public StorageQueue storageQueue;
	public StorageDevice storageDevice;

	public FIFOStorageController()
	{
		storageQueue = new StorageQueue();
	}

	public StorageDevice getDevice()
	{
		return storageQueue;
	}

	@Override
	public boolean isRequestPending()
	{
		// TODO Auto-generated method stub
		return storageQueue.pending == null && storageQueue.ordered.size() > 0;
	}

	@Override
	public StorageDeviceStatus getHardwareStatus()
	{
		// TODO Auto-generated method stub
		if (storageQueue.pendingReads.contains(storageQueue.ordered.get(0)))
		{
			return StorageDeviceStatus.READ;
		} else if (storageQueue.pendingWrites.contains(storageQueue.ordered.get(0)))
		{
			return StorageDeviceStatus.WRITE;
		} else
		{
			return StorageDeviceStatus.IDLE;
		}
	}

	@Override
	public Data getNextRequest()
	{
		storageQueue.pending = storageQueue.ordered.get(0);
		return storageQueue.ordered.get(0);
	}

	@Override
	public void adknowledgeCompletedRequest(Data data)
	{
		if (storageQueue.pendingReads.contains(data))
		{
			storageQueue.completedReads.put(data.getId(), data.copy());
			storageQueue.pendingReads.remove(data);
		} else
		{
			storageQueue.completedWrites.put(data.getId(), data.copy());
			storageQueue.pendingWrites.remove(data);
		}
		storageQueue.ordered.remove(0);
		storageQueue.pending = null;
	}
}
