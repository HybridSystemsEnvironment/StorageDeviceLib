package edu.ucsc.cross.hse.model.storage.states;

import java.util.HashMap;

import com.carrotsearch.sizeof.RamUsageEstimator;

import edu.ucsc.cross.hse.core.modeling.DataStructure;
import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;

public class StorageState extends DataStructure
{

	public StorageDeviceStatus status;
	public double storedDataSize;
	public double dataToTransfer;
	public HashMap<Object, Data> storedData;

	public StorageState(HashMap<Object, Data> storedData)
	{
		dataToTransfer = 0.0;
		this.storedData = storedData;
		storedDataSize = 0.0;
		for (Object p : storedData.values())
		{
			storedDataSize += RamUsageEstimator.sizeOf(p);
		}
		status = StorageDeviceStatus.OFF;

	}

	public StorageState()
	{
		dataToTransfer = 0.0;
		status = StorageDeviceStatus.OFF;
		this.storedData = new HashMap<Object, Data>();
		storedDataSize = 0.0;
	}
}
