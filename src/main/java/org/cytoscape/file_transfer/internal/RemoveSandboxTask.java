package org.cytoscape.file_transfer.internal;

import java.io.File;

import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import org.apache.commons.io.FileUtils;

public class RemoveSandboxTask extends CyRESTAbstractTask {
	
	@ProvidesTitle
	public String getTitle() { return "Remove a Cytoscape sandbox"; }

	@Tunable (description="sandboxName", longDescription="Name of sandbox to remove", exampleStringValue="mySandbox")
	public String sandboxName = "";
	
	private File sandboxParentDirFile;
	
	public RemoveSandboxTask(File sandboxParentDirFile){
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	@Override
	public void run(TaskMonitor taskMon) throws Exception {
		File sandboxDirFile = SandboxUtils.getAbsSandboxFile(sandboxParentDirFile, sandboxName);
		String sandboxPath = sandboxDirFile.getCanonicalPath();

		if (sandboxDirFile.exists()) {
			FileUtils.forceDelete(sandboxDirFile); // delete existing directory if possible 
			result = new RemoveSandboxResult(sandboxPath, true);
		} else {
			result = new RemoveSandboxResult(sandboxPath, false);
		}
	}
	
	public static String getExample() {
		return getJson(new RemoveSandboxResult("/User/CytoscapeConfiguration/FileTransfer/MySandbox", true));
	}
}
