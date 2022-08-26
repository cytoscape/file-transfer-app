/*
Copyright 2021 The Cytoscape Consortium

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

import org.apache.commons.io.FileUtils;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class URLToSandboxTask extends CyRESTAbstractTask {
	
	@ProvidesTitle
	public String getTitle() { return "Transfer URL-named file to sandbox"; }

	@Tunable (description="sandboxName", longDescription="Name of sandbox to contain file", exampleStringValue="default_sandbox")
	public String sandboxName = "";
	
	@Tunable (description="fileName", longDescription="Sandbox-relative name of file to save.", exampleStringValue="GDS112_full.soft")
	public String fileName = "";
	
	@Tunable (description="sourceURL", longDescription="URL of the source file", exampleStringValue="https://www.dropbox.com/s/r15azh0xb53smu1/GDS112_full.soft?dl=0")
	public String sourceURL = "";

	@Tunable (description="overwrite", longDescription="True to overwrite a file if it already exists", exampleStringValue="true")
	public boolean overwrite = false;

	private File sandboxParentDirFile;
	
	
	public URLToSandboxTask(File sandboxParentDirFile){
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	@Override
	public void run(TaskMonitor taskMon) throws Exception {
		if (SandboxUtils.showDebug()) {
			System.out.println("In URLToSandbox");
			System.out.println(" sandboxName: " + sandboxName);
			System.out.println(" fileName: " + fileName);
			System.out.println(" sourceURL: " + sourceURL);
			System.out.println(" overwrite: " + overwrite);
			System.out.println(" sandboxParentDirFile: " + sandboxParentDirFile);
		}

		File fileFile = SandboxUtils.getAbsFileFile(sandboxParentDirFile, sandboxName, fileName, false);
		
		if (sourceURL == null || sourceURL.trim().length() == 0) {
			throw new Exception("Source URL cannot be null");
		}

		if (fileFile.exists()) {
			if (overwrite) {
				try {
					FileUtils.forceDelete(fileFile); // Kill file or directory
				} catch(Throwable e) {}
			} else {
				throw new Exception("'" + fileName + "' already exists.");
			}
		}
		
		taskMon.setStatusMessage("Writing file " + fileName);
		fileFile.getParentFile().mkdirs(); // be sure all (if any) parent directories are created
		
		String filePath = fileFile.getCanonicalPath();
		if (SandboxUtils.showDebug()) {
			System.out.println(" Downloading " + sourceURL + " to " + filePath);
		}
		long length = new CloudURL(sourceURL).readFile(filePath);
		if (SandboxUtils.showDebug()) {
			System.out.println(" Downloaded " + length);
		}
		
		result = new URLToSandboxResult(filePath, length);
	}
	
	public static String getExample() {
		return getJson(new URLToSandboxResult("/User/CytoscapeConfiguration/FileTransfer/default_sandbox/GDS112_full.soft", 5536880));
	}
}
