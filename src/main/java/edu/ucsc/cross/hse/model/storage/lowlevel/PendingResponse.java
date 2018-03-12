package edu.ucsc.cross.hse.model.storage.lowlevel;

/**
 * This is a temporary container that can be used to transfer information
 * indirectly or at a later time. In the case of a storage device, a read
 * request might not be processed right away so this container can be returned
 * instead. Then when the read is addressed, the contents will be loaded into
 * this container and the clients read request has been completed.
 * 
 * @author beshort
 *
 */
public class PendingResponse
{

	Object content;

	public PendingResponse()
	{
		content = "ssssss";
	}

	public void writeToPath(Object object)
	{
		content = object;
	}

	public Object readFromPath()
	{
		return content;
	}
}
