package org.cytoscape.file_transfer.internal;

import java.io.File;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class FromCytoscapeTaskFactory extends AbstractTaskFactory{
	
	private File sandboxParentDirFile;
	
	public FromCytoscapeTaskFactory(File sandboxParentDirFile) {
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	public boolean isReady() {
		return true;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new FromCytoscapeTask(sandboxParentDirFile));
	}
}
