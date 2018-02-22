package edu.ucsc.cross.hse.model.storage;

import java.util.HashMap;

import edu.ucsc.cross.hse.model.data.general.DataItem;

public interface StorageInterface
{

	public <T> void write(T data);

	public <T> void write(DataItem<T> store);

	public <T> void read(DataItem<T> store);

	public HashMap<Object, DataItem<?>> completedReads();

	public HashMap<Object, DataItem<?>> completedWrites();

}
