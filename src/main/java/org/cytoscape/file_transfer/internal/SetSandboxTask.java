package org.cytoscape.file_transfer.internal;

import java.io.File;

import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import org.apache.commons.io.FileUtils;

public class SetSandboxTask extends CyRESTAbstractTask {
	
	@ProvidesTitle
	public String getTitle() { return "Create a Cytoscape sandbox"; }

	@Tunable (description="sandboxName", longDescription="Name of sandbox to create or reinitialize", exampleStringValue="mySandbox")
	public String sandboxName = "";
	
	@Tunable (description="copySamples", longDescription="Determines whether to copy Cytoscape's sampleData directory to the sandbox.", exampleStringValue="true")
	public boolean copySamples = true;
	
	@Tunable (description="reinitialize", longDescription="Determines whether existing files are erased from the sandbox.", exampleStringValue="true")
	public boolean reinitialize = true;
	
	private File sandboxParentDirFile;
	private File cytoscapeInstallDirFile;
	
	public SetSandboxTask(File sandboxParentDirFile, File cytoscapeInstallDirFile){
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
		this.cytoscapeInstallDirFile = cytoscapeInstallDirFile;
	}
	
	@Override
	public void run(TaskMonitor taskMon) throws Exception {
		File sandboxDirFile = SandboxUtils.getAbsSandboxFile(sandboxParentDirFile, sandboxName);
		
		if (reinitialize) {
			// Start sandbox over by deleting it
			try {
				FileUtils.forceDelete(sandboxDirFile); // delete directory if it's there
			} catch(Throwable e) {}
		}

		// Create new sandbox if it doesn't already exist
		FileUtils.forceMkdir(sandboxDirFile); // if this fails, let exception propagate up
		
		if (copySamples) {
			// Bring in Cytoscape sample files
			try {
				File samplesDirFile = new File(cytoscapeInstallDirFile, "sampleData");
				FileUtils.copyDirectoryToDirectory(samplesDirFile, sandboxDirFile);
			} catch (Throwable e) {
				try {
					FileUtils.forceDelete(sandboxDirFile); // delete directory if it's there
				} catch(Throwable e1) {}
				throw e;
			}
		}
		
		result = new SetSandboxResult(sandboxDirFile.getCanonicalPath());
	}
	
	public static String getExample() {
		return getJson(new SetSandboxResult("/User/CytoscapeConfiguration/FileTransfer/MySandbox"));
	}
}
