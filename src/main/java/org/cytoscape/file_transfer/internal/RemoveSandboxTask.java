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
		if (SandboxUtils.showDebug()) {
			System.out.println("In RemoveSandbox");
			System.out.println(" sandboxName: " + sandboxName);
			System.out.println(" sandboxParentDirFile: " + sandboxParentDirFile);
		}

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
