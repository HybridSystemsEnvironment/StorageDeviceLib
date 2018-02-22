package edu.ucsc.cross.hse.model.storage;

import edu.ucsc.cross.hse.core.modelling.DataStructure;

public class StorageParameters extends DataStructure
{

	public double writeSpeed;
	public double readSpeed;

	public StorageParameters(double write_speed, double read_speed)
	{
		writeSpeed = write_speed;
		readSpeed = read_speed;
	}

	public double getSpeed(StorageDeviceStatus status)
	{
		if (status.equals(StorageDeviceStatus.READ))
		{
			return readSpeed;
		} else if (status.equals(StorageDeviceStatus.WRITE))
		{
			return writeSpeed;
		} else
		{
			return 0.0;
		}
	}
}
