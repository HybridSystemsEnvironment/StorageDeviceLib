package edu.ucsc.cross.hse.model.storage.user;

public class SensorParameters
{

	public double dataItemSize; // size of each data item generated
	public double generationInterval; // interval between data generation

	// Constructor to set data generator properties
	public SensorParameters(Double data_size, Double interval)
	{
		dataItemSize = data_size; // set data generation size
		generationInterval = interval; // set data generation interval
	}
}
