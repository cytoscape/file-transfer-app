/*
Copyright 2020 The Cytoscape Consortium

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the 
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit 
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the 
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

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
