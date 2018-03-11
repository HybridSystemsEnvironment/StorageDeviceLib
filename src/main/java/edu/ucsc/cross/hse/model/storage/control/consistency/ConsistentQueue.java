package edu.ucsc.cross.hse.model.storage.control.consistency;

import java.util.HashMap;

import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.data.objects.RealData;
import edu.ucsc.cross.hse.model.storage.StorageInterface;

public class ConsistentQueue implements StorageInterface
{

	/**
	 * Full replica of the data set where each element is indexed by an object
	 */
	public HashMap<Object, Data> localDataReplica;
	public HashMap<Object, Data> localUpdateQueue;

	public HashMap<Object, Data> pendingReads;
	public HashMap<Object, Data> pendingWrites;

	public HashMap<Object, Data> delayedReads;
	public HashMap<Object, Data> delayedWrites;

	public HashMap<Object, Data> completedReads;
	public HashMap<Object, Data> completedWrites;

	public Data pending;

	public ConsistentQueue()
	{
		localDataReplica = new HashMap<Object, Data>();
		localUpdateQueue = new HashMap<Object, Data>();

		pendingReads = new HashMap<Object, Data>();
		pendingWrites = new HashMap<Object, Data>();

		completedReads = new HashMap<Object, Data>();
		completedWrites = new HashMap<Object, Data>();
	}

	public <T> void write(Object index, Data store)
	{
		System.out.println("new write: " + store);
		localDataReplica.put(index, store);
		localUpdateQueue.put(index, store);
		pendingWrites.put(index, store);
	}

	public <T> void read(Data store)
	{
		System.out.println("new read: " + store);
		pendingReads.put(store.getId(), store);

	}

	public boolean isReadComplete(Object index)
	{
		return completedReads.containsKey(index);
	}

	public Data getReadData(Object index)
	{
		Data dat = null;
		if (isReadComplete(index))
		{
			dat = completedReads.get(index);
		}
		completedReads.remove(index);
		return dat;
	}

	public boolean isWriteComplete(Object index)
	{
		return completedWrites.containsKey(index);
	}

	public boolean clearWriteComplete(Object index)
	{
		if (isWriteComplete(index))
		{
			completedWrites.remove(index);
			return true;
		}
		return false;

	}

	public HashMap<Object, Data> completedReads()
	{
		// TODO Auto-generated method stub
		return completedReads;
	}

	public HashMap<Object, Data> completedWrites()
	{
		// TODO Auto-generated method stub
		return completedWrites;
	}
	// public void processReadToQueue(Object index)

	@Override
	public <T> void write(T data)
	{
		RealData dat = new RealData(data);
		write(dat.getId(), dat);
	}

	@Override
	public <T> void write(Data store)
	{
		write(store.getId(), store);

	}
}
