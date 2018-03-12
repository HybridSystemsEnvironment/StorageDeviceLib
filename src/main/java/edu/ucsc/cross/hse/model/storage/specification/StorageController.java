package edu.ucsc.cross.hse.model.storage.specification;

import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.storage.StorageDevice;

public interface StorageController
{

	public StorageDevice getInterface();

	public boolean isHardwareActionPending();

	public StorageDeviceStatus getIntendedHardwareStatus();

	public Data getNextDataTransfer();

	public void adknowledgeCompletedTransfer(Data done);

}
