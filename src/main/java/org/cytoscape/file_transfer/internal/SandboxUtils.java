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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SandboxUtils {

	public static final File getAbsSandboxFile(File sandboxParentDirFile, String sandboxName) throws Exception {
		
		if (sandboxName == null || sandboxName.trim().length() == 0) {
			throw new Exception("Sandbox name cannot be empty.");
		}
	
		// This is ugly code because we're just trying to get a single path element.
		// The Java Paths class will could "C:\foo" as an absolute path, but not "\foo".
		// So, we have to revert to uglier means. Additionally, we don't want anything
		// like two path elements (e.g., foo/boo) or something that gets us anywhere but
		// below the parent directory (e.g., '..' or '.'), so anything with a '.' must have
		// a non-dot character.
		Path sandboxPath = Paths.get(sandboxName);
		if (sandboxPath.getNameCount() != 1 
				|| sandboxPath.isAbsolute() 
				|| !sandboxPath.getFileName().toString().matches(".*[^\\.].*")
				|| sandboxName.matches(".*[\\\\/].*")) { 
			throw new Exception("Sandbox name must be a simple directory name.");
		}
		
		return new File(sandboxParentDirFile, sandboxPath.toString());
	}
	
	public static final File getAbsFileFile(File sandboxParentDirFile, String sandboxName, String fileName, boolean allowSandboxOnly) throws Exception {
		if (fileName == null || fileName.trim().length() == 0) {
			throw new Exception("File name cannot be empty.");			
		}

		// If there is a sandbox, an absolute path can't be used to escape it
		if (sandboxName == null || sandboxName.trim().length() == 0) {
			return new File(fileName); // No sandbox ... all paths are allowed
		}
		
		File sandboxDirFile = SandboxUtils.getAbsSandboxFile(sandboxParentDirFile, sandboxName);
		String sandboxPath = sandboxDirFile.getCanonicalPath() + File.separator;
		if (!sandboxDirFile.exists()) {
			throw new Exception("Sandbox '" + sandboxPath + "' doesn't exist");
		}
		
		if (allowSandboxOnly && fileName.trim().equals(".")) {
			return sandboxDirFile; // Allow check to see if sandbox exists
		}

		File fileFile = new File(sandboxDirFile, fileName);
		
		// Check to make sure that ".." didn't escape sandbox
		String filePath = fileFile.getCanonicalPath();
		if (filePath.startsWith(sandboxPath)) {
			return fileFile;
		} else {
			throw new Exception("Files outside of sandbox are not allowed. '" + filePath + "' is not in '" + sandboxPath + "'");				
		}
	}
	
	public static final String getModifiedTime(File fileFile) {
		if (fileFile.exists()) {
			SimpleDateFormat s = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSSS");
			return s.format(new Date(fileFile.lastModified()));
		} else {
			return "";
		}
	}
	
	public static final boolean showDebug() {
		String env_var = System.getenv().getOrDefault("CY_FILETRANSFER_DEBUG", "false");
		
		return env_var.toLowerCase().equals("true");
	}
}
