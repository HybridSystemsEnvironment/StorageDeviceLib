package edu.ucsc.cross.hse.model.storage.states;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ucsc.cross.hse.model.data.general.DataItem;
import edu.ucsc.cross.hse.model.storage.StorageInterface;

public class StorageQueue implements StorageInterface
{

	public ArrayList<DataItem<?>> pendingWrites;
	public ArrayList<DataItem<?>> pendingReads;
	public ArrayList<DataItem<?>> ordered;

	public HashMap<Object, DataItem<?>> completedReads;
	public HashMap<Object, DataItem<?>> completedWrites;

	public DataItem<?> pending;

	public StorageQueue()
	{
		pendingWrites = new ArrayList<DataItem<?>>();
		pendingReads = new ArrayList<DataItem<?>>();
		ordered = new ArrayList<DataItem<?>>();
		completedReads = new HashMap<Object, DataItem<?>>();
		completedWrites = new HashMap<Object, DataItem<?>>();
		pending = null;
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
}
