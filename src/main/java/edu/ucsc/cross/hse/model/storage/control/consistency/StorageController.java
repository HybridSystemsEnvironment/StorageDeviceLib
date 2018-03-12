package edu.ucsc.cross.hse.model.storage.control.consistency;

import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.storage.StorageDevice;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;

public interface StorageController
{

	public StorageDevice getInterface();

	public boolean isHardwareActionPending();

	public StorageDeviceStatus getIntendedHardwareStatus();

	public Data getNextDataTransfer();

	public void adknowledgeCompletedTransfer(Data done);

}
