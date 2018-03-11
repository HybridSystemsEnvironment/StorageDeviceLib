package edu.ucsc.cross.hse.model.storage.control.consistency;

import java.util.HashMap;

import edu.ucsc.cross.hse.core.modeling.DataStructure;
import edu.ucsc.cross.hse.model.data.Data;

public class UpdateQueue extends DataStructure
{

	/**
	 * Full replica of the data set where each element is indexed by an object
	 */
	public HashMap<Object, Data> localDataReplica;

	public UpdateQueue(HashMap<Object, Data> updates)
	{
		localDataReplica = new HashMap<Object, Data>();
		for (Object key : updates.keySet())
		{
			localDataReplica.put(key, updates.get(key));
		}

	}

}
