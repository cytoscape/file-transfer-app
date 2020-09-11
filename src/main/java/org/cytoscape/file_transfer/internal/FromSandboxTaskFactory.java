package org.cytoscape.file_transfer.internal;

import java.io.File;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class FromSandboxTaskFactory extends AbstractTaskFactory{
	
	public static final String DESCRIPTION = "Transfer a file from sandbox";
	//Note: we can use markdown in our long descriptions, hence the ``` code block style.
	public static final String LONG_DESCRIPTION = "Given a sandbox (as ```sandboxName```) and file within it (as ```fileName```), returns a Base64-encoded file image ```fileBase64```, the full file path ```filePath``` and a small amount of file metadata ```fileByteCount``` and ```modifiedTime```.";
	
	private File sandboxParentDirFile;
	
	public FromSandboxTaskFactory(File sandboxParentDirFile) {
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	public boolean isReady() {
		return true;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new FromSandboxTask(sandboxParentDirFile));
	}
}
