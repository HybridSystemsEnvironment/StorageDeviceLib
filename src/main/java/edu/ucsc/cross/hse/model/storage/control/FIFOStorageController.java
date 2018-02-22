package edu.ucsc.cross.hse.model.storage.control;

import edu.ucsc.cross.hse.model.data.general.DataItem;
import edu.ucsc.cross.hse.model.storage.StorageInterface;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;
import edu.ucsc.cross.hse.model.storage.states.StorageQueue;

public class FIFOStorageController implements StorageController
{

	public StorageQueue storageQueue;

	public FIFOStorageController()
	{
		storageQueue = new StorageQueue();
	}

	public StorageInterface getStorage()
	{
		return storageQueue;
	}

	@Override
	public boolean actionReady()
	{
		// TODO Auto-generated method stub
		return storageQueue.pending == null && storageQueue.ordered.size() > 0;
	}

	@Override
	public StorageDeviceStatus nextStatus()
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
	public DataItem<?> nextTransfer()
	{
		storageQueue.pending = storageQueue.ordered.get(0);
		return storageQueue.ordered.get(0);
	}

	@Override
	public void completed(DataItem<?> data)
	{
		if (storageQueue.pendingReads.contains(data))
		{
			storageQueue.completedReads.put(data.getAddress(), data.copy());
			storageQueue.pendingReads.remove(data);
		} else
		{
			storageQueue.completedWrites.put(data.getAddress(), data.copy());
			storageQueue.pendingWrites.remove(data);
		}
		storageQueue.ordered.remove(0);
		storageQueue.pending = null;
	}
}
