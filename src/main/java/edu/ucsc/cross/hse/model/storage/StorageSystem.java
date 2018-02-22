package edu.ucsc.cross.hse.model.storage;

import edu.ucsc.cross.hse.core.modelling.HybridSystem;

public class StorageSystem extends HybridSystem<StorageState>
{

	StorageController controller;
	StorageParameters params;
	DataItem<?> pending;

	public StorageSystem(StorageState state, StorageParameters params, StorageController controller)
	{
		super(state, params);
		this.params = params;
		this.controller = controller;
	}

	@Override
	public boolean C(StorageState arg0)
	{
		return (arg0.status.equals(StorageDeviceStatus.READ) || arg0.status.equals(StorageDeviceStatus.WRITE))
		&& pending != null;
	}

	@Override
	public boolean D(StorageState arg0)
	{
		return controller.actionReady() || (arg0.dataToTransfer <= 0.0 && C(arg0));

	}

	@Override
	public void F(StorageState arg0, StorageState arg1)
	{
		arg1.dataToTransfer = -params.getSpeed(arg0.status);
	}

	@Override
	public void G(StorageState arg0, StorageState arg1)
	{
		if (pending != null && arg1.dataToTransfer <= 0)
		{
			arg1.storedData.put(pending.getAddress(), pending.copy());
			controller.completed(pending);
			arg1.storedDataSize = getStoredDataSize(arg1);
			pending = null;

		}
		if (controller.actionReady())
		{
			arg1.status = controller.nextStatus();
			arg1.dataToTransfer = controller.nextTransfer().getSize();
			pending = controller.nextTransfer();
		}
	}

	public double getStoredDataSize(StorageState x)
	{
		double stored = 0.0;
		System.out.println(x.storedData.size());
		for (DataItem<?> p : x.storedData.values())
		{
			stored += p.getSize();
		}
		return stored;
	}
}
