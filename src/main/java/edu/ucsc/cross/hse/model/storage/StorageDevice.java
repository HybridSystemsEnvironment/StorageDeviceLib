package edu.ucsc.cross.hse.model.storage;

import java.util.HashMap;

public interface StorageDevice
{

	public <T> void write(T data);

	public <T> void write(DataItem<T> store);

	public <T> void read(DataItem<T> store);

	public HashMap<Object, DataItem<?>> completedReads();

	public HashMap<Object, DataItem<?>> completedWrites();

}
