package edu.ucsc.cross.hse.model.storage.lowlevel;

import edu.ucsc.cross.hse.core.modeling.DataStructure;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;

public class BaseStorageState extends DataStructure
{

	public StorageDeviceStatus status;
	public double pendingDataTransfer;
	public double totalDataTransferred;

	public BaseStorageState()
	{
		this.pendingDataTransfer = 0;
		totalDataTransferred = 0;
	}
}
