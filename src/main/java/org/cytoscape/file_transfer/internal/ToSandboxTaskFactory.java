package org.cytoscape.file_transfer.internal;

import java.io.File;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class ToSandboxTaskFactory extends AbstractTaskFactory{
	
	public static final String DESCRIPTION = "Transfer a file to sandbox";
	//Note: we can use markdown in our long descriptions, hence the ``` code block style.
	public static final String LONG_DESCRIPTION = "Given a Base64-encoded file image ```fileBase64```, decode it, verify the size as ```fileByteCount```, and then write it to ```fileName``` in a sandbox (```sandboxName```). If the file already exists, it will be overwritten only if ```overwrite``` is true. Either way, return the file path (```filePath```).";
	
	private File sandboxParentDirFile;
	
	public ToSandboxTaskFactory(File sandboxParentDirFile) {
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	public boolean isReady() {
		return true;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ToSandboxTask(sandboxParentDirFile));
	}
}
