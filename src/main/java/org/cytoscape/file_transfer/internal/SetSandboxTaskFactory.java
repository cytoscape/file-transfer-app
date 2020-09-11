package org.cytoscape.file_transfer.internal;

import org.cytoscape.work.AbstractTaskFactory;

import java.io.File;

import org.cytoscape.work.TaskIterator;

public class SetSandboxTaskFactory extends AbstractTaskFactory{
	
	public static final String DESCRIPTION = "Create a Cytoscape sandbox";
	//Note: we can use markdown in our long descriptions, hence the ``` code block style.
	public static final String LONG_DESCRIPTION = "Given a sandbox name (as ```sandboxName```), create the sandbox if it doesn't exist. If it does exist, optionally clear it (```reinitialize```). Also, optionally add Cytoscape's sample files (```copySamples```). Either way, return the sandbox' full path (```sandboxPath```)";
	
	private File sandboxParentDirFile;
	private File cytoscapeInstallDirFile;
	
	public SetSandboxTaskFactory(File sandboxParentDirFile, File cytoscapeInstallDirFile) {
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	public boolean isReady() {
		return true;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new SetSandboxTask(sandboxParentDirFile, cytoscapeInstallDirFile));
	}
}
