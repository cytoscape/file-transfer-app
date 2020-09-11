package org.cytoscape.file_transfer.internal;


public class GetFileInfoResult extends CyRESTAbstractResult {
	public String filePath;
	public String modifiedTime;
	public boolean isFile;
	
	
	public GetFileInfoResult(){}
	
	public GetFileInfoResult(String filePath, String modifiedTime, boolean isFile) {
		this.filePath = filePath;
		this.modifiedTime = modifiedTime;
		this.isFile = isFile;
	}
}

