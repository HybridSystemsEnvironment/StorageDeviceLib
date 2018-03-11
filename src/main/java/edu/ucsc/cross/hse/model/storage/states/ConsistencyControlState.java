package edu.ucsc.cross.hse.model.storage.states;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.modeling.DataStructure;
import edu.ucsc.cross.hse.lib.network.Node;

public class ConsistencyControlState extends DataStructure
{

	public double isTurn;
	public ArrayList<Node> pendingTransmissions;

	public ConsistencyControlState()
	{
		pendingTransmissions = new ArrayList<Node>();
	}
}
