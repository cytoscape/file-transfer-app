package org.cytoscape.file_transfer.internal;


public class ToSandboxResult extends CyRESTAbstractResult {
	public String filePath;
	
	public ToSandboxResult(){}
	
	public ToSandboxResult(String filePath) {
		this.filePath = filePath;
	}
}
