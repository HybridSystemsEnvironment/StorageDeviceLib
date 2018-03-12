package edu.ucsc.cross.hse.model.storage.lowlevel;

import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;

public interface StorageDevice
{

	public StorageDeviceStatus getStatus();

	public PendingResponse requestWrite(Object path, Object content);

	public PendingResponse requestRead(Object path);

}
