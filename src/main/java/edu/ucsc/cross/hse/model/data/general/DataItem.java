package edu.ucsc.cross.hse.model.data.general;

import com.carrotsearch.sizeof.RamUsageEstimator;

public class DataItem<D> implements Data<D>
{

	D data;
	Object address;
	double timeStamp;

	public DataItem(D data)
	{
		this.data = data;
		address = this.toString();
	}

	@Override
	public Object getAddress()
	{
		// TODO Auto-generated method stub
		return address;
	}

	@Override
	public double getSize()
	{
		// TODO Auto-generated method stub
		return RamUsageEstimator.sizeOf(data);
	}

	@Override
	public D getData()
	{
		// TODO Auto-generated method stub
		return data;
	}

	public DataItem<D> copy()
	{
		DataItem<D> dataItem = new DataItem<D>(data);
		dataItem.address = this.address;

		return dataItem;
	}

}
