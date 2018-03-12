package edu.ucsc.cross.hse.model.storage;

import java.util.HashMap;

import edu.ucsc.cross.hse.model.data.Data;

public interface StorageDevice
{

	public <T> void write(T data);

	public <T> void write(Data store);

	public <T> void read(Data store);

	public HashMap<Object, Data> completedReads();

	public HashMap<Object, Data> completedWrites();

}
