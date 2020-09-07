package org.cytoscape.file_transfer.internal;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class FromCytoscapeTaskFactory extends AbstractTaskFactory{
	
	public FromCytoscapeTaskFactory() {
		super();
	}
	
	public boolean isReady() {
		return true;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new FromCytoscape());
	}
}
