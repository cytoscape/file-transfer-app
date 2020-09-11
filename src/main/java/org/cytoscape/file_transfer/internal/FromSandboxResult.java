package org.cytoscape.file_transfer.internal;


public class FromSandboxResult extends CyRESTAbstractResult {
	public String filePath;
	public String modifiedTime;
	public long fileByteCount;
	public String fileBase64;
	
	
	public FromSandboxResult(){}
	
	public FromSandboxResult(String filePath, String modifiedTime, long fileByteCount, String fileBase64) {
		this.filePath = filePath;
		this.modifiedTime = modifiedTime;
		this.fileByteCount = fileByteCount;
		this.fileBase64 = fileBase64;
	}
}
