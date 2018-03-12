package edu.ucsc.cross.hse.model.storage.control;

import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.storage.StorageDevice;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;

public interface StorageController
{

	public StorageDevice getDevice();

	public boolean isRequestPending();

	public StorageDeviceStatus getHardwareStatus();

	public Data getNextRequest();

	public void adknowledgeCompletedRequest(Data done);

}
