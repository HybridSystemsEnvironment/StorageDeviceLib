package edu.ucsc.cross.hse.model.storage;

import java.util.ArrayList;
import java.util.HashMap;

public class StorageQUeue implements StorageController
{

	ArrayList<DataItem<?>> pendingWrites;
	ArrayList<DataItem<?>> pendingReads;
	ArrayList<DataItem<?>> ordered;
	HashMap<Object, DataItem<?>> completedReads;
	HashMap<Object, DataItem<?>> completedWrites;
	DataItem<?> pending;

	public StorageQUeue()
	{
		pendingWrites = new ArrayList<DataItem<?>>();
		pendingReads = new ArrayList<DataItem<?>>();
		ordered = new ArrayList<DataItem<?>>();
		completedReads = new HashMap<Object, DataItem<?>>();
		completedWrites = new HashMap<Object, DataItem<?>>();
	}

	@Override
	public <T> void write(T data)
	{
		DataItem<T> dataItem = new DataItem<T>(data);
		pendingWrites.add(dataItem);
		ordered.add(dataItem);
	}

	@Override
	public <T> void write(DataItem<T> store)
	{
		pendingWrites.add(store);
		ordered.add(store);
	}

	@Override
	public <T> void read(DataItem<T> store)
	{
		pendingReads.add(store);
		ordered.add(store);

	}

	@Override
	public HashMap<Object, DataItem<?>> completedReads()
	{
		// TODO Auto-generated method stub
		return completedReads;
	}

	@Override
	public HashMap<Object, DataItem<?>> completedWrites()
	{
		// TODO Auto-generated method stub
		return completedWrites;
	}

	@Override
	public boolean actionReady()
	{
		// TODO Auto-generated method stub
		return pending == null && ordered.size() > 0;
	}

	@Override
	public StorageDeviceStatus nextStatus()
	{
		// TODO Auto-generated method stub
		if (pendingReads.contains(ordered.get(0)))
		{
			return StorageDeviceStatus.READ;
		} else if (pendingWrites.contains(ordered.get(0)))
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
		pending = ordered.get(0);
		return ordered.get(0);
	}

	@Override
	public void completed(DataItem<?> data)
	{
		if (pendingReads.contains(data))
		{
			completedReads.put(data.getAddress(), data.copy());
			pendingReads.remove(data);
		} else
		{
			completedWrites.put(data.getAddress(), data.copy());
			pendingWrites.remove(data);
		}
		ordered.remove(0);
		pending = null;
	}
}
