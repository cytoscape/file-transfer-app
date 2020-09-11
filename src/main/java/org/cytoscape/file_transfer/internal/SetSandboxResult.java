package org.cytoscape.file_transfer.internal;


public class SetSandboxResult extends CyRESTAbstractResult {
	public String sandboxPath;
	
	
	public SetSandboxResult(){}
	
	public SetSandboxResult(String sandboxPath) {
		this.sandboxPath = sandboxPath;
	}
}
