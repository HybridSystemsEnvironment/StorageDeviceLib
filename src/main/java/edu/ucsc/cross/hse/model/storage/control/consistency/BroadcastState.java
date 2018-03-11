package edu.ucsc.cross.hse.model.storage.control.consistency;

import edu.ucsc.cross.hse.core.modeling.DataStructure;
import edu.ucsc.cross.hse.model.storage.StorageInterface;
import edu.ucsc.cross.hse.model.storage.states.StorageQueue;

public class BroadcastState extends DataStructure
{

	public StorageQueue storageQueue;
	public double time;

	public BroadcastState()
	{
		time = 0.0;
		storageQueue = new StorageQueue();
	}

	public StorageInterface getInterface()
	{
		return storageQueue;
	}

}
