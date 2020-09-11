package org.cytoscape.file_transfer.internal;

import java.io.File;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class RemoveFileTaskFactory extends AbstractTaskFactory{
	
	public static final String DESCRIPTION = "Remove a sandboxed file or directory";
	//Note: we can use markdown in our long descriptions, hence the ``` code block style.
	public static final String LONG_DESCRIPTION = "Given the sandbox (```sandboxName```) and file name (```fileName```), removes the file (if it exists) and return the full path (```filePath```).";
	
	private File sandboxParentDirFile;
	
	public RemoveFileTaskFactory(File sandboxParentDirFile) {
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	public boolean isReady() {
		return true;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new RemoveFileTask(sandboxParentDirFile));
	}
}
