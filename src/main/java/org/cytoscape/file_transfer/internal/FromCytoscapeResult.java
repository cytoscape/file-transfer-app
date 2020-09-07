package org.cytoscape.file_transfer.internal;


public class FromCytoscapeResult {
	public String modifiedTime;
	public long fileByteCount;
	public String fileBase64;
	
	
	public FromCytoscapeResult(){}
	
	public FromCytoscapeResult(String modifiedTime, long fileByteCount, String fileBase64) {
		this.modifiedTime = modifiedTime;
		this.fileByteCount = fileByteCount;
		this.fileBase64 = fileBase64;
	}
}
