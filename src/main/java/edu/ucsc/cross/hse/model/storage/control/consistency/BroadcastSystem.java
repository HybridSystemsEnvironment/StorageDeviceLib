package edu.ucsc.cross.hse.model.storage.control.consistency;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.modeling.HybridSystem;
import edu.ucsc.cross.hse.lib.network.Network;
import edu.ucsc.cross.hse.lib.network.Node;
import edu.ucsc.cross.hse.model.storage.StorageInterface;
import edu.ucsc.cross.hse.model.storage.states.StorageQueue;

public class BroadcastSystem extends HybridSystem<BroadcastState>
{

	public StorageQueue storageQueue;
	public Network distributedNetwork;
	public Integer cycleTurn;
	public ArrayList<ParamConsistencyController> controllers;

	public BroadcastSystem(Network distributed_network)
	{
		super(new BroadcastState());
		distributedNetwork = distributed_network;
		storageQueue = new StorageQueue();
		cycleTurn = 0;
		controllers = new ArrayList<ParamConsistencyController>();
	}

	public StorageInterface getInterface()
	{
		return storageQueue;
	}

	@Override
	public boolean C(BroadcastState arg0)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean D(BroadcastState arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void F(BroadcastState arg0, BroadcastState arg1)
	{
		// TODO Auto-generated method stub
		arg1.time = 1.0;
	}

	@Override
	public void G(BroadcastState arg0, BroadcastState arg1)
	{
		// TODO Auto-generated method stub

	}

	public Node currentBroadcaster()
	{
		Node returnNode = distributedNetwork.getTopology().vertexSet()
		.toArray(new Node[distributedNetwork.getTopology().vertexSet().size()])[cycleTurn];
		return returnNode;
	}

	public void nextTurn()
	{

		if (pendingUpdates())
		{
			cycleTurn = cycleTurn + 1;
			if (cycleTurn >= distributedNetwork.getTopology().vertexSet().size())
			{
				cycleTurn = 0;
			}
		}
	}

	public boolean pendingUpdates()
	{
		boolean updates = false;
		for (ParamConsistencyController c : controllers)
		{
			updates = updates || c.isHardwareActionPending();// .updates || n.getReceivingBuffer().size() > 0 || n.getTransmittingBuffer().size() > 0;
		}
		return updates;
	}
}
