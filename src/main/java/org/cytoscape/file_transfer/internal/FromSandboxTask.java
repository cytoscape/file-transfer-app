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
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class FromSandboxTask extends CyRESTAbstractTask {
	
	@ProvidesTitle
	public String getTitle() { return "Transfer file from a sandbox"; }

	@Tunable (description="sandboxName", longDescription="Name of sandbox containing file", exampleStringValue="default_sandbox")
	public String sandboxName = "";
	
	@Tunable (description="fileName", longDescription="Sandbox-relative name of file.", exampleStringValue="myFile.png")
	public String fileName = "";
	
	private File sandboxParentDirFile;
	
	public FromSandboxTask(File sandboxParentDirFile) {
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	@Override
	public void run(TaskMonitor taskMon) throws Exception {
		File fileFile = SandboxUtils.getAbsFileFile(sandboxParentDirFile, sandboxName, fileName, false);
		if (fileFile.exists() && !fileFile.isFile()) {
			throw new Exception("'" + fileName + "' must identify a file, not a directory, in sandbox '" + sandboxName + "'.");
		}

		try {
			String modifiedTime = SandboxUtils.getModifiedTime(fileFile); 
			
			taskMon.setStatusMessage("Reading file " + fileName);

			String filePath = fileFile.getCanonicalPath();
			byte[] allBytes = Files.readAllBytes(Paths.get(filePath));
			long fileByteCount = allBytes.length;
			String fileBase64 = new String(Base64.encodeBase64(allBytes), StandardCharsets.UTF_8);

			result = new FromSandboxResult(filePath, modifiedTime, fileByteCount, fileBase64);
		} catch (Throwable e) {
			throw new Exception(e.toString());
		}
	}	
	
	public static String getExample() {
		return getJson(new FromSandboxResult("/User/CytoscapeConfiguration/FileTransfer/default_sandbox/MyFile.png", "2020-07-29 03:00:00.0000", 10, "iVBORw0KGgoAAAANSUhEUgAABY="));
	}
}
