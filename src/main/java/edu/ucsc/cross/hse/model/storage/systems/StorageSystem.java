package edu.ucsc.cross.hse.model.storage.systems;

import edu.ucsc.cross.hse.core.modeling.HybridSystem;
import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.storage.StorageInterface;
import edu.ucsc.cross.hse.model.storage.control.StorageController;
import edu.ucsc.cross.hse.model.storage.parameters.StorageParameters;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;
import edu.ucsc.cross.hse.model.storage.states.StorageState;

public class StorageSystem extends HybridSystem<StorageState>
{

	StorageController controller;

	public StorageInterface getStorage()
	{
		return controller.getStorage();
	}

	StorageParameters params;
	Data pending;

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
			arg1.storedData.put(pending.getId(), pending.copy());
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

	public static double getStoredDataSize(StorageState x)
	{
		double stored = 0.0;
		System.out.println(x.storedData.size());
		for (Data p : x.storedData.values())
		{
			stored += p.getSize();
		}
		return stored;
	}
}
