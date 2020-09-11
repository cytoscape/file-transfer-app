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
		File sandboxDirFile = SandboxUtils.getAbsSandboxFile(sandboxParentDirFile, sandboxName);
		
		if (fileName == null || fileName.trim().length() == 0) {
			throw new Exception("File name cannot be empty.");			
		}

		if (allowSandboxOnly && fileName.trim().equals(".")) {
			return sandboxDirFile; // Allow check to see if sandbox exists
		}

		File fileFile = new File(sandboxDirFile, fileName);
		
		String sandboxPath = sandboxDirFile.getCanonicalPath() + File.separator;
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
}
