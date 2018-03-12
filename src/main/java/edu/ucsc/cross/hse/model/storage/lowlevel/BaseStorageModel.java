package edu.ucsc.cross.hse.model.storage.lowlevel;

import java.util.HashMap;

import com.carrotsearch.sizeof.RamUsageEstimator;

import edu.ucsc.cross.hse.core.modeling.HybridSystem;
import edu.ucsc.cross.hse.model.storage.parameters.StorageParameters;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;

public class BaseStorageModel extends HybridSystem<BaseStorageState> implements StorageDevice
{

	StorageParameters storageParams;
	GeneralQueue writeQueue;
	GeneralQueue readQueue;
	HashMap<Object, PendingResponse> pendingResponses;
	HashMap<Object, Object> storedContent;
	public Object pendingKey;

	public BaseStorageModel(BaseStorageState state, StorageParameters storeage_parameters,
	QueueProperties write_queue_properties, QueueProperties read_queue_properties)
	{
		super(state);
		storageParams = storeage_parameters;
		writeQueue = new GeneralQueue(write_queue_properties);
		readQueue = new GeneralQueue(read_queue_properties);
		pendingResponses = new HashMap<Object, PendingResponse>();
		storedContent = new HashMap<Object, Object>();
	}

	@Override
	public StorageDeviceStatus getStatus()
	{
		return null;
	}

	@Override
	public PendingResponse requestWrite(Object path, Object content)
	{
		PendingResponse response = screenRequest(writeQueue);
		if (response != null)
		{
			pendingResponses.put(path, response);
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
			pendingResponses.put(path, response);
			readQueue.addContent(path, null);
		}
		return response;
	}

	@Override
	public boolean C(BaseStorageState arg0)
	{
		boolean pendingWork = pendingResponses.size() > 0;
		return pendingWork;
	}

	@Override
	public boolean D(BaseStorageState arg0)
	{
		// TODO Auto-generated method stub
		return this.getComponents().getState().pendingDataTransfer <= 0 && pendingResponses.size() > 0;
	}

	@Override
	public void F(BaseStorageState arg0, BaseStorageState arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void G(BaseStorageState arg0, BaseStorageState arg1)
	{
		// TODO Auto-generated method stub

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
		pendingKey = pendingResponses
		.get(pendingResponses.keySet().toArray(new Object[pendingResponses.keySet().size()])[0]);
		arg1.pendingDataTransfer = RamUsageEstimator.sizeOfAll(writeQueue.getContent().get(pendingKey));
	}
}