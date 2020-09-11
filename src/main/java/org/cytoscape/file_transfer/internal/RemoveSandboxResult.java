package org.cytoscape.file_transfer.internal;


public class RemoveSandboxResult extends CyRESTAbstractResult {
	public String sandboxPath;
	public boolean existed;
	
	
	public RemoveSandboxResult(){}
	
	public RemoveSandboxResult(String sandboxPath, boolean existed) {
		this.sandboxPath = sandboxPath;
		this.existed = existed;
	}
}
