package edu.ucsc.cross.hse.model.storage.user;

import edu.ucsc.cross.hse.core.modeling.HybridSystem;
import edu.ucsc.cross.hse.model.storage.lowlevel.StorageDevice;

/*
 * Hybrid system model of a data generator, consisting of a timer that increments a memory state upon each expiration.
 * This is a simple way to emulate a periodic data source such as a sensor or a data query routine.
 */
public class SensorSystem extends HybridSystem<SensorState>
{

	public SensorParameters params;
	public Double receivedData;
	public StorageDevice storage;

	// Constructor that loads data generator state and parameters
	public SensorSystem(SensorState state, SensorParameters parameters, StorageDevice storage)

	{
		super(state); // load data generator state and parameters
		params = parameters;
		receivedData = 0.0;
		this.storage = storage;
	}

	// Checks if data generator state is in flow map
	public boolean C(SensorState x)
	{
		boolean waiting = x.timeToNextData > 0.0;
		return waiting;
	}

	// Computes continuous dynamics of data generator state
	public void F(SensorState x, SensorState x_dot)
	{
		x_dot.dataGenerated = 0.0;
		x_dot.timeToNextData = -1.0;
	}

	// Checks if data generator state is in jump map
	public boolean D(SensorState x)
	{
		boolean dataGenerated = x.timeToNextData <= 0.0;

		return dataGenerated || (receivedData > 0.0);
	}

	// Computes discrete dynamics of data generator state
	public void G(SensorState x, SensorState x_plus)
	{
		storage.requestWrite(this.toString(), 20 * Math.random() + this.params.dataItemSize + receivedData);
		if (x.timeToNextData <= 0.0)
		{

			x_plus.timeToNextData = params().generationInterval;
		}
		receivedData = -1.0;
	}

	// data generator parameters
	public SensorParameters params()
	{
		return params;

	}
}
