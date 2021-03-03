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
import java.nio.file.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class ToSandboxTask extends CyRESTAbstractTask {
	
	@ProvidesTitle
	public String getTitle() { return "Transfer file to sandbox"; }

	@Tunable (description="sandboxName", longDescription="Name of sandbox to contain file", exampleStringValue="mySandbox")
	public String sandboxName = "";
	
	@Tunable (description="fileName", longDescription="Sandbox-relative name of file to save.", exampleStringValue="myFile.png")
	public String fileName = "";
	
	@Tunable (description="fileBase64", longDescription="The file content as Base64", exampleStringValue="iVBORw0KGgoAAAANSUhEUgAABY=")
	public String fileBase64 = "";

	@Tunable (description="fileByteCount", longDescription="The count of bytes in the raw file", exampleStringValue="10")
	public long fileByteCount = 0;

	@Tunable (description="overwrite", longDescription="True to overwrite a file if it already exists", exampleStringValue="true")
	public boolean overwrite = false;

	private File sandboxParentDirFile;
	
	
	public ToSandboxTask(File sandboxParentDirFile){
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	@Override
	public void run(TaskMonitor taskMon) throws Exception {
		File fileFile = SandboxUtils.getAbsFileFile(sandboxParentDirFile, sandboxName, fileName, false);
//		System.out.println("sandboxName: " + sandboxName);
//		System.out.println("fileName: " + fileName);
//		System.out.println("fileBase64Len: " + fileBase64.length());
//		System.out.println("fileByteCount: " + fileByteCount);
//		System.out.println("overwrite: " + overwrite);
//		System.out.println("fileBase64: " + fileBase64);
		
		if (fileBase64 == null || fileBase64.length() == 0) {
			throw new Exception("File content cannot be empty.");
		}
		
		byte[] fileRaw = Base64.decodeBase64(fileBase64);
		if (fileRaw.length != fileByteCount) {
			throw new Exception("File '" + fileName + "' contains " + fileRaw.length + " bytes but should contain " + fileByteCount + " bytes");
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
		Files.write(Paths.get(filePath), fileRaw);
		
		result = new ToSandboxResult(filePath);
	}
	
	public static String getExample() {
		return getJson(new ToSandboxResult("/User/CytoscapeConfiguration/FileTransfer/MySandbox/MyFile.png"));
	}
}
