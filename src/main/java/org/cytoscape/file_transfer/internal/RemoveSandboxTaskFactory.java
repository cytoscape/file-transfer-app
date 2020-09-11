package org.cytoscape.file_transfer.internal;

import org.cytoscape.work.AbstractTaskFactory;

import java.io.File;

import org.cytoscape.work.TaskIterator;

public class RemoveSandboxTaskFactory extends AbstractTaskFactory{
	
	public static final String DESCRIPTION = "Remove a Cytoscape sandbox";
	//Note: we can use markdown in our long descriptions, hence the ``` code block style.
	public static final String LONG_DESCRIPTION = "Given a sandbox name (```sandboxName```), delete it and all its contents, then return the sandbox' full path (```sandboxPath```) and whether it existed (```existed```).";
	
	private File sandboxParentDirFile;
	
	public RemoveSandboxTaskFactory(File sandboxParentDirFile) {
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	public boolean isReady() {
		return true;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new RemoveSandboxTask(sandboxParentDirFile));
	}
}
