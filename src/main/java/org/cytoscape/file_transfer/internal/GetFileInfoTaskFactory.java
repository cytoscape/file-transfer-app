package org.cytoscape.file_transfer.internal;

import java.io.File;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class GetFileInfoTaskFactory extends AbstractTaskFactory{

	public static final String DESCRIPTION = "Return metadata for a sandboxed file or directory";
	//Note: we can use markdown in our long descriptions, hence the ``` code block style.
	public static final String LONG_DESCRIPTION = "Given the sandbox (```sandboxName```) and file/directory name (```fileName```), returns the corresponding full path (```filePath```). If it doesn't exist, ```modifiedTime`` is returned empty. Otherwise, it contains the file system's modified time and whether the object is a file (```isFile```), too.";
	
	private File sandboxParentDirFile;
	
	public GetFileInfoTaskFactory(File sandboxParentDirFile) {
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	public boolean isReady() {
		return true;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new GetFileInfoTask(sandboxParentDirFile));
	}
}
