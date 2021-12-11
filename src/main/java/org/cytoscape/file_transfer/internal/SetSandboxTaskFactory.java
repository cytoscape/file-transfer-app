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

import org.cytoscape.work.AbstractTaskFactory;

import java.io.File;

import org.cytoscape.work.TaskIterator;

public class SetSandboxTaskFactory extends AbstractTaskFactory{
	
	public static final String DESCRIPTION = "Create a Cytoscape sandbox";
	//Note: we can use markdown in our long descriptions, hence the ``` code block style.
	public static final String LONG_DESCRIPTION = "Given a sandbox name (as ```sandboxName```), create the sandbox if it doesn't exist. If it does exist, optionally clear it (```reinitialize```). Also, optionally add Cytoscape's sample files (```copySamples```). Either way, return the sandbox' full path (```sandboxPath```)";
	
	private File sandboxParentDirFile;
	private File cytoscapeInstallDirFile;
	
	public SetSandboxTaskFactory(File sandboxParentDirFile, File cytoscapeInstallDirFile) {
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
		this.cytoscapeInstallDirFile = cytoscapeInstallDirFile;
	}
	
	public boolean isReady() {
		return true;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new SetSandboxTask(sandboxParentDirFile, cytoscapeInstallDirFile));
	}
}
