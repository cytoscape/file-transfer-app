package org.cytoscape.file_transfer.internal;

import java.io.File;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class GetFileInfoTask extends CyRESTAbstractTask {
	
	@ProvidesTitle
	public String getTitle() { return "Return metadata for a sandboxed file or directory"; }

	@Tunable (description="sandboxName", longDescription="Name of sandbox containing file", exampleStringValue="mySandbox")
	public String sandboxName = "";
	
	@Tunable (description="fileName", longDescription="Sandbox-relative name of file or directory.", exampleStringValue="myFile.png")
	public String fileName = "";
	
	private File sandboxParentDirFile;
	
	public GetFileInfoTask(File sandboxParentDirFile){
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	@Override
	public void run(TaskMonitor taskMon) throws Exception {
		File fileFile = SandboxUtils.getAbsFileFile(sandboxParentDirFile, sandboxName, fileName, true);
		String modifiedTime = SandboxUtils.getModifiedTime(fileFile);
		boolean isFile = modifiedTime.length() != 0 && fileFile.isFile();
		
		result = new GetFileInfoResult(fileFile.getCanonicalPath(), modifiedTime, isFile);
	}
	
	public static String getExample() {
		return getJson(new GetFileInfoResult("/User/CytoscapeConfiguration/FileTransfer/MySandbox/MyFile.png", "2020-07-29 03:00:00.0000", true));
	}
}
