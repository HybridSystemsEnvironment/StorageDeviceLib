package edu.ucsc.cross.hse.model.storage.lowlevel;

import java.util.HashMap;

public class GeneralQueue implements DataQueue<Object, Object>
{

	private QueueProperties properties;
	private HashMap<Object, Object> contents;

	public GeneralQueue(QueueProperties properties)
	{
		this.properties = properties;
		contents = new HashMap<Object, Object>();
	}

	@Override
	public boolean isFull()
	{
		return contents.size() >= properties.queueCapacity;
	}

	@Override
	public HashMap<Object, Object> getContent()
	{
		return contents;
	}

	@Override
	public boolean addContent(Object variable, Object object)
	{
		if (isFull())
		{
			return false;
		}
		contents.put(variable, object);
		return true;
	}

	@Override
	public boolean removeContent(Object object)
	{
		try
		{
			contents.remove(object);
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

	@Override
	public boolean clearQueue()
	{
		contents.clear();
		return true;
	}
}
