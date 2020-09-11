package org.cytoscape.file_transfer.internal;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class RemoveFileTask extends CyRESTAbstractTask {
	
	@ProvidesTitle
	public String getTitle() { return "Remove a sandboxed file or directory if one exists"; }

	@Tunable (description="sandboxName", longDescription="Name of sandbox containing file", exampleStringValue="mySandbox")
	public String sandboxName = "";
	
	@Tunable (description="fileName", longDescription="Sandbox-relative name of file or directory to delete.", exampleStringValue="myFile.png")
	public String fileName = "";
	
	private File sandboxParentDirFile;
	
	public RemoveFileTask(File sandboxParentDirFile){
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	@Override
	public void run(TaskMonitor taskMon) throws Exception {
		File fileFile = SandboxUtils.getAbsFileFile(sandboxParentDirFile, sandboxName, fileName, false);
		String filePath = fileFile.getCanonicalPath();

		if (fileFile.exists()) {
			FileUtils.forceDelete(fileFile); // delete existing file/directory if possible 
			result = new RemoveFileResult(filePath, true);
		} else {
			result = new RemoveFileResult(filePath, false);
		}
	}
		
	public static String getExample() {
		return getJson(new RemoveFileResult("/User/CytoscapeConfiguration/FileTransfer/MySandbox/MyFile.png", true));
	}
}
