package edu.ucsc.cross.hse.model.storage.lowlevel;

import java.util.HashMap;

public interface DataQueue<X, Y>
{

	public boolean isFull();

	public HashMap<X, Y> getContent();

	public boolean addContent(X variable, Y object);

	public boolean removeContent(X object);

	public boolean clearQueue();
}
