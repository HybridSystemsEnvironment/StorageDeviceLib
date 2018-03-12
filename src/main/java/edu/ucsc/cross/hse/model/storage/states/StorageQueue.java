package edu.ucsc.cross.hse.model.storage.states;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.data.objects.RealData;
import edu.ucsc.cross.hse.model.storage.StorageDevice;

public class StorageQueue implements StorageDevice
{

	public ArrayList<Data> pendingWrites;
	public ArrayList<Data> pendingReads;
	public ArrayList<Data> ordered;

	public HashMap<Object, Data> completedReads;
	public HashMap<Object, Data> completedWrites;

	public Data pending;

	public StorageQueue()
	{
		pendingWrites = new ArrayList<Data>();
		pendingReads = new ArrayList<Data>();
		ordered = new ArrayList<Data>();
		completedReads = new HashMap<Object, Data>();
		completedWrites = new HashMap<Object, Data>();
		pending = null;
	}

	@Override
	public <T> void write(T data)
	{
		Data dataItem = new RealData(data);
		pendingWrites.add(dataItem);
		ordered.add(dataItem);
	}

	@Override
	public <T> void write(Data store)
	{
		pendingWrites.add(store);
		ordered.add(store);
	}

	@Override
	public <T> void read(Data store)
	{
		pendingReads.add(store);
		ordered.add(store);

	}

	@Override
	public HashMap<Object, Data> completedReads()
	{
		// TODO Auto-generated method stub
		return completedReads;
	}

	@Override
	public HashMap<Object, Data> completedWrites()
	{
		// TODO Auto-generated method stub
		return completedWrites;
	}
}
