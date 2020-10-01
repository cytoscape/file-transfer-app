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
//		System.out.println("sandboxName: " + sandboxName);
//		System.out.println("fileName: " + fileName);

		File fileFile = SandboxUtils.getAbsFileFile(sandboxParentDirFile, sandboxName, fileName, true);
		String modifiedTime = SandboxUtils.getModifiedTime(fileFile);
		boolean isFile = modifiedTime.length() != 0 && fileFile.isFile();
		
		result = new GetFileInfoResult(fileFile.getCanonicalPath(), modifiedTime, isFile);
	}
	
	public static String getExample() {
		return getJson(new GetFileInfoResult("/User/CytoscapeConfiguration/FileTransfer/MySandbox/MyFile.png", "2020-07-29 03:00:00.0000", true));
	}
}
