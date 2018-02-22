package edu.ucsc.cross.hse.model.storage;

public interface Data<D>
{

	public Object getAddress();

	public double getSize();

	public D getData();

}
