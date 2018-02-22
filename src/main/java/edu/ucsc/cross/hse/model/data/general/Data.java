package edu.ucsc.cross.hse.model.data.general;

public interface Data<D>
{

	public Object getAddress();

	public double getSize();

	public D getData();

}
