package edu.ucsc.cross.hse.model.storage.lowlevel;

import edu.ucsc.cross.hse.core.modeling.DataStructure;

public class QueueProperties extends DataStructure
{

	public Integer queueCapacity;

	public QueueProperties(Integer queue_capacity)
	{
		queueCapacity = queue_capacity;
	}
}
