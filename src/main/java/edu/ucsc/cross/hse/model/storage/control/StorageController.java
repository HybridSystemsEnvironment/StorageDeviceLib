package edu.ucsc.cross.hse.model.storage.control;

import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.storage.StorageInterface;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;

public interface StorageController
{

	public StorageInterface getStorage();

	public boolean actionReady();

	public StorageDeviceStatus nextStatus();

	public Data nextTransfer();

	public void completed(Data done);

}