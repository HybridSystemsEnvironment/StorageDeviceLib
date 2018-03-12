package edu.ucsc.cross.hse.model.storage.lowlevel;

import edu.ucsc.cross.hse.core.modeling.DataStructure;

public class BaseStorageState extends DataStructure
{

	public double pendingDataTransfer;

	public BaseStorageState()
	{
		this.pendingDataTransfer = 0;
	}
}
