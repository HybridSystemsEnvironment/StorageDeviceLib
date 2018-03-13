package edu.ucsc.cross.hse.model.storage.systems;

import edu.ucsc.cross.hse.core.modeling.HybridSystem;
import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.storage.StorageDevice;
import edu.ucsc.cross.hse.model.storage.control.StorageController;
import edu.ucsc.cross.hse.model.storage.parameters.StorageParameters;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;
import edu.ucsc.cross.hse.model.storage.states.StorageState;

public class StorageSystem extends HybridSystem<StorageState>
{

	StorageController controller;

	public StorageDevice getStorage()
	{
		return controller.getDevice();
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
		return ((arg0.status.equals(StorageDeviceStatus.READ) || arg0.status.equals(StorageDeviceStatus.WRITE)))
		&& (arg0.dataToTransfer >= 0.0);
	}

	@Override
	public boolean D(StorageState arg0)
	{
		return (pending == null && controller.isRequestPending()) || (arg0.dataToTransfer <= 0 && pending != null);//|| (arg0.dataToTransfer < -0.00001);

	}

	@Override
	public void F(StorageState arg0, StorageState arg1)
	{
		arg1.dataToTransfer = -params.getSpeed(arg0.status);
	}

	@Override
	public void G(StorageState arg0, StorageState arg1)
	{
		if ((pending != null && arg0.dataToTransfer <= 0))//|| arg0.dataToTransfer < 0.0)
		{
			arg1.storedData.put(pending.getId(), pending.copy());
			arg1.storedDataSize += pending.getSize();
			controller.adknowledgeCompletedRequest(pending);
			pending = null;

		}
		if (controller.isRequestPending() && pending == null)
		{
			pending = controller.getNextRequest();
			arg1.dataToTransfer = pending.getSize();
			arg1.status = controller.getHardwareStatus();
		}
	}

}
