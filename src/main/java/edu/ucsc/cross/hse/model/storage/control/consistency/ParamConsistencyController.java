package edu.ucsc.cross.hse.model.storage.control.consistency;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.modeling.HybridSystem;
import edu.ucsc.cross.hse.lib.network.Node;
import edu.ucsc.cross.hse.model.data.Data;
import edu.ucsc.cross.hse.model.data.objects.RealData;
import edu.ucsc.cross.hse.model.data.packet.BasicPacket;
import edu.ucsc.cross.hse.model.data.packet.Packet;
import edu.ucsc.cross.hse.model.data.packet.header.MinimalHeader;
import edu.ucsc.cross.hse.model.storage.StorageInterface;
import edu.ucsc.cross.hse.model.storage.control.StorageController;
import edu.ucsc.cross.hse.model.storage.specification.StorageDeviceStatus;
import edu.ucsc.cross.hse.model.storage.states.ConsistencyControlState;

// Events that can trigger jump:
// Became broadcast turn
// Received broadcast
// Received read/write/delete command
// Storage device has completed task and more are pending
public class ParamConsistencyController extends HybridSystem<ConsistencyControlState> implements StorageController
{

	public BroadcastSystem storageQueue;
	public ConsistentQueue consQueue;
	public Node node;
	public boolean writing = false;

	public ParamConsistencyController(BroadcastSystem storageQueue, Node stor)
	{
		super(new ConsistencyControlState());
		node = stor;
		this.storageQueue = storageQueue;
		consQueue = new ConsistentQueue();

	}

	public StorageInterface getInterface()
	{
		return consQueue;
	}

	@Override
	public boolean isHardwareActionPending()
	{
		// TODO Auto-generated method stub
		return (!writing) && (consQueue.pendingWrites.size() > 0 || consQueue.pendingReads.size() > 0);
	}

	@Override
	public StorageDeviceStatus getIntendedHardwareStatus()
	{
		//		// TODO Auto-generated method stub
		//		if (storageQueue.pendingReads.contains(storageQueue.ordered.get(0)))
		//		{
		//			return StorageDeviceStatus.READ;
		//		} else if (storageQueue.pendingWrites.contains(storageQueue.ordered.get(0)))
		//		{
		//			return StorageDeviceStatus.WRITE;
		//		} else
		//		{
		if (writing)
		{
			return StorageDeviceStatus.WRITE;
		} else
		{
			return StorageDeviceStatus.IDLE;
		}
		//	}
	}

	@Override
	public Data getNextDataTransfer()
	{
		System.out.println("n");
		writing = true;
		return consQueue.pendingWrites.get(0);
	}

	@Override
	public void adknowledgeCompletedTransfer(Data data)
	{
		consQueue.pendingWrites.remove(data);
		writing = false;
		System.out.println(data);
	}

	@Override
	public boolean C(ConsistencyControlState arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean D(ConsistencyControlState arg0)
	{
		boolean broadcast = broadcastAdknowledged(arg0);
		boolean turn = becomingBroadcaster(arg0);
		boolean receiveBroadcast = receivingBroadcast(arg0);
		boolean receiveAck = receivingAdknowledgement(arg0);
		return broadcast || turn || receiveBroadcast || receiveAck;
	}

	@Override
	public void F(ConsistencyControlState arg0, ConsistencyControlState arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void G(ConsistencyControlState arg0, ConsistencyControlState arg1)
	{
		processAdknowledgements(arg1);
		processReceivedPacket();
		processChangeBroadcaster(arg0, arg1);
	}

	public void processAdknowledgements(ConsistencyControlState arg1)
	{
		if (this.node.equals(storageQueue.currentBroadcaster()))
		{

			ArrayList<Packet> remove = new ArrayList<Packet>();
			for (Packet packet : this.node.getReceivingBuffer())
			{
				if (packet.getHeader().getIdTag(BroadCastMessage.class).equals(BroadCastMessage.RECEIVED_UPDATE))
				{
					arg1.pendingTransmissions.remove(packet.getHeader().getSource());
					remove.add(packet);
				}
			}
			this.node.getReceivingBuffer().removeAll(remove);
		}
	}

	public void processReceivedPacket()
	{
		if (!this.node.equals(storageQueue.currentBroadcaster()))
		{

			ArrayList<Packet> remove = new ArrayList<Packet>();

			for (Packet packet : this.node.getReceivingBuffer())
			{
				if (packet.getHeader().getIdTag(BroadCastMessage.class).equals(BroadCastMessage.SEND_UPDATE))
				{
					UpdateQueue q = packet.getPayload().getData(UpdateQueue.class);
					receiveUpdates(q);
					remove.add(packet);
				}
			}
			this.node.getReceivingBuffer().removeAll(remove);

		}
	}

	public void processChangeBroadcaster(ConsistencyControlState arg0, ConsistencyControlState arg1)
	{
		if (this.node.equals(storageQueue.currentBroadcaster()))
		{
			if (arg0.isTurn <= 0.0)
			{
				System.out.println(this + "'s turn");
				arg1.isTurn = 1.0;
			} else
			{
				if (storageQueue.pendingUpdates() || arg1.pendingTransmissions.size() <= 0.0)
				{
					arg1.isTurn = 0.0;
					storageQueue.nextTurn();
				}
			}
		}
	}

	public void receiveUpdates(UpdateQueue queue)
	{
		for (Object key : queue.localDataReplica.keySet())
		{
			if (!consQueue.localUpdateQueue.containsKey(key))
			{
				consQueue.localDataReplica.put(key, queue.localDataReplica.get(key));
			}
		}
	}

	public void adknowledgeBroadcast()
	{
		Node returnNode = storageQueue.distributedNetwork.getTopology().vertexSet()
		.toArray(new Node[storageQueue.distributedNetwork.getTopology().vertexSet().size()])[storageQueue.cycleTurn];
		RealData transmission = new RealData(true);
		MinimalHeader header = new MinimalHeader(BroadCastMessage.RECEIVED_UPDATE, this.node, returnNode);
		node.sendPacket(new BasicPacket(header, transmission));

	}

	public ArrayList<Node> transmitUpdates(UpdateQueue queue)
	{
		ArrayList<Node> pending = new ArrayList<Node>();

		if (queue.localDataReplica.size() > 0)
		{
			for (Node conn : storageQueue.distributedNetwork.getTopology().vertexSet())
			{
				if (!conn.equals(this.node))
				{
					RealData transmission = new RealData(queue);
					MinimalHeader header = new MinimalHeader(BroadCastMessage.SEND_UPDATE, this.node, conn);
					node.sendPacket(new BasicPacket(header, transmission));
					pending.add(conn);

				}
			}
		}
		return pending;
	}

	public boolean broadcastAdknowledged(ConsistencyControlState arg0)
	{
		if (arg0.isTurn > 0)
		{
			if (arg0.pendingTransmissions.size() == 0)
			{
				return true;
			}
		}
		return false;
	}

	public boolean becomingBroadcaster(ConsistencyControlState arg0)
	{
		if (storageQueue.pendingUpdates())
		{
			if (arg0.isTurn <= 0)
			{
				if (storageQueue.currentBroadcaster().equals(this.node))
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean receivingBroadcast(ConsistencyControlState arg0)
	{

		if (!storageQueue.currentBroadcaster().equals(this.node))
		{
			for (Packet packet : this.node.getReceivingBuffer())
			{
				if (packet.getHeader().getIdTag(BroadCastMessage.class).equals(BroadCastMessage.SEND_UPDATE))
				{
					return true;
				}
			}
		}
		return false;

	}

	public boolean receivingAdknowledgement(ConsistencyControlState arg0)
	{

		if (storageQueue.currentBroadcaster().equals(this.node))
		{
			for (Packet packet : this.node.getReceivingBuffer())
			{
				if (packet.getHeader().getIdTag(BroadCastMessage.class).equals(BroadCastMessage.RECEIVED_UPDATE))
				{
					return true;
				}
			}
		}
		return false;

	}

}
