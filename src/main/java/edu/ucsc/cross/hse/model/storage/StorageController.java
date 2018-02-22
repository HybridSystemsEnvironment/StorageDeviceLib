package edu.ucsc.cross.hse.model.storage;

public interface StorageController extends StorageDevice
{

	public boolean actionReady();

	public StorageDeviceStatus nextStatus();

	public <T> DataItem<?> nextTransfer();

	public void completed(DataItem<?> done);

}
