package edu.ucsc.cross.hse.model.storage.control;

import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.storage.StorageInterface;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;

public interface StorageController
{

	public StorageInterface getInterface();

	public boolean isHardwareActionPending();

	public StorageDeviceStatus getIntendedHardwareStatus();

	public Data getNextDataTransfer();

	public void adknowledgeCompletedTransfer(Data done);

}
