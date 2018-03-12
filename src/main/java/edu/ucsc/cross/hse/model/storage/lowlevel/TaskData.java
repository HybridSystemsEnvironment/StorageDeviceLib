package edu.ucsc.cross.hse.model.storage.lowlevel;

public class TaskData
{

	public PendingResponse callLink;
	public Object variable;
	public Object value;
	public StorageInstruction instruction;

	public TaskData(PendingResponse callLink, Object variable, Object value, StorageInstruction instruction)
	{
		this.callLink = callLink;
		this.variable = variable;
		this.value = value;
		this.instruction = instruction;
	}

}