package org.cytoscape.file_transfer.internal;


public class RemoveFileResult extends CyRESTAbstractResult {
	public String filePath;
	public boolean existed;

	
	public RemoveFileResult(){}
	
	public RemoveFileResult(String filePath, boolean existed) {
		this.filePath = filePath;
		this.existed = existed;
	}
}
