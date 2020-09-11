package org.cytoscape.file_transfer.internal;


import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_LONG_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.COMMAND_SUPPORTS_JSON;
import static org.cytoscape.work.ServiceProperties.COMMAND_EXAMPLE_JSON;
import static org.cytoscape.work.ServiceProperties.IN_CONTEXT_MENU;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TOOLTIP;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.work.TaskFactory;

public class CyActivator extends AbstractCyActivator {

	public static final String FILE_TRANSFER_COMMAND_NAMESPACE = "filetransfer";

	public CyActivator() {
		super();
	}

	public static final String getFromCytoscapeExample() {
		return FromCytoscapeTask.getJson(new FromCytoscapeResult("2020-07-29 03:00:00.0000", 10, "iVBORw0KGgoAAAANSUhEUgAABY="));
	}
	
	public static final String getToCytoscapeExample() {
		return ToCytoscapeTask.getJson(new ToCytoscapeResult("MyFile.png"));
	}
	
	public static final String getSetSandboxExample() {
		return SetSandboxTask.getJson(new SetSandboxResult("MySandbox"));
	}	
	
	public static final String getFileInfoExample() {
		return GetFileInfoTask.getJson(new GetFileInfoResult("/User/CytoscapeConfiguration/FileTransfer/MySandbox/MyFile.png", "2020-07-29 03:00:00.0000", true));
	}	
	
	public static final String getRemoveFileExample() {
		return RemoveFileTask.getJson(new RemoveFileResult(true));
	}	
	
	public static final String getRemoveSandboxExample() {
		return RemoveSandboxTask.getJson(new RemoveSandboxResult(true));
	}	
	
	public void start(BundleContext bc) throws InvalidSyntaxException 
	{
		final CyApplicationConfiguration config = getService(bc, CyApplicationConfiguration.class);
		File configDirFile = new File(config.getConfigurationDirectoryLocation().getAbsoluteFile(), 
				                   FILE_TRANSFER_COMMAND_NAMESPACE);
		File installDirFile = config.getInstallationDirectoryLocation().getAbsoluteFile();

		// Register FromCytoscape function
		String transferFromCytoscapeDescription = "Transfer a file from Cytoscape";
		
		//Note: we can use markdown in our long descriptions, hence the ``` code block style.
		String transferFromCytoscapeLongDescription = "Returns a Base64-encoded file image ```fileBase64``` and a small amount of file metadata ```fileByteCount``` and ```modifiedTime```."; 
		
		Properties fromCytoscapeProperties = new Properties();
		fromCytoscapeProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		fromCytoscapeProperties.setProperty(COMMAND, "fromCytoscape");
		fromCytoscapeProperties.setProperty(COMMAND_DESCRIPTION,  transferFromCytoscapeDescription);
		fromCytoscapeProperties.setProperty(COMMAND_LONG_DESCRIPTION, transferFromCytoscapeLongDescription);
		fromCytoscapeProperties.setProperty(COMMAND_EXAMPLE_JSON, getFromCytoscapeExample());
		fromCytoscapeProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		fromCytoscapeProperties.setProperty(PREFERRED_MENU, "Transfer");
		fromCytoscapeProperties.setProperty(IN_MENU_BAR, "true");
		fromCytoscapeProperties.setProperty(IN_CONTEXT_MENU, "false");
		fromCytoscapeProperties.setProperty(TITLE, "From Cytoscape");
		fromCytoscapeProperties.setProperty(TOOLTIP,  transferFromCytoscapeDescription);

		TaskFactory fromCytoscapeTaskFactory = new FromCytoscapeTaskFactory(configDirFile);
		registerAllServices(bc, fromCytoscapeTaskFactory, fromCytoscapeProperties);

		// Register ToCytoscape function
		String transferToCytoscapeDescription = "Transfer a file to Cytoscape";
		
		//Note: we can use markdown in our long descriptions, hence the ``` code block style.
		String transferToCytoscapeLongDescription = "Accepts a Base64-encoded file image ```fileBase64```, decodes it, verifies the size as ```fileByteCount```, and then writes it to ```file```."; 
		
		Properties toCytoscapeProperties = new Properties();
		toCytoscapeProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		toCytoscapeProperties.setProperty(COMMAND, "toCytoscape");
		toCytoscapeProperties.setProperty(COMMAND_DESCRIPTION,  transferToCytoscapeDescription);
		toCytoscapeProperties.setProperty(COMMAND_LONG_DESCRIPTION, transferToCytoscapeLongDescription);
		toCytoscapeProperties.setProperty(COMMAND_EXAMPLE_JSON, getToCytoscapeExample());
		toCytoscapeProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		toCytoscapeProperties.setProperty(PREFERRED_MENU, "Transfer");
		toCytoscapeProperties.setProperty(IN_MENU_BAR, "true");
		toCytoscapeProperties.setProperty(IN_CONTEXT_MENU, "false");
		toCytoscapeProperties.setProperty(TITLE, "To Cytoscape");
		toCytoscapeProperties.setProperty(TOOLTIP, transferToCytoscapeDescription);

		TaskFactory toCytoscapeTaskFactory = new ToCytoscapeTaskFactory();
		registerAllServices(bc, toCytoscapeTaskFactory, toCytoscapeProperties);
		
		// Register setSandbox function
		String setSandboxDescription = "Create a Cytoscape sandbox";
		
		//Note: we can use markdown in our long descriptions, hence the ``` code block style.
		String setSandboxLongDescription = "Accepts the directory name for a file system sandbox (as ```sandboxName```) and optionally pre-populates it with Cytoscape's sample files (as ```copySamples```). If the sandbox already exists, it can be preserved or reinitialized (per ```reinitialize```)."; 
		
		Properties setSandboxProperties = new Properties();
		setSandboxProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		setSandboxProperties.setProperty(COMMAND, "setSandbox");
		setSandboxProperties.setProperty(COMMAND_DESCRIPTION,  setSandboxDescription);
		setSandboxProperties.setProperty(COMMAND_LONG_DESCRIPTION, setSandboxLongDescription);
		setSandboxProperties.setProperty(COMMAND_EXAMPLE_JSON, getSetSandboxExample());
		setSandboxProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		setSandboxProperties.setProperty(PREFERRED_MENU, "Transfer");
		setSandboxProperties.setProperty(IN_MENU_BAR, "true");
		setSandboxProperties.setProperty(IN_CONTEXT_MENU, "false");
		setSandboxProperties.setProperty(TITLE, "Set Sandbox");
		setSandboxProperties.setProperty(TOOLTIP, setSandboxDescription);

		TaskFactory setSandboxTaskFactory = new SetSandboxTaskFactory(configDirFile, installDirFile);
		registerAllServices(bc, setSandboxTaskFactory, setSandboxProperties);
		
		// Register removeSandbox function
		String removeSandboxDescription = "Remove a Cytoscape sandbox";
		
		//Note: we can use markdown in our long descriptions, hence the ``` code block style.
		String removeSandboxLongDescription = "Accepts the directory name for a file system sandbox (as ```sandboxName```) and removes the sandbox and all files/directories in it."; 
		
		Properties removeSandboxProperties = new Properties();
		removeSandboxProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		removeSandboxProperties.setProperty(COMMAND, "removeSandbox");
		removeSandboxProperties.setProperty(COMMAND_DESCRIPTION,  removeSandboxDescription);
		removeSandboxProperties.setProperty(COMMAND_LONG_DESCRIPTION, removeSandboxLongDescription);
		removeSandboxProperties.setProperty(COMMAND_EXAMPLE_JSON, getRemoveSandboxExample());
		removeSandboxProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		removeSandboxProperties.setProperty(PREFERRED_MENU, "Transfer");
		removeSandboxProperties.setProperty(IN_MENU_BAR, "true");
		removeSandboxProperties.setProperty(IN_CONTEXT_MENU, "false");
		removeSandboxProperties.setProperty(TITLE, "Remove Sandbox");
		removeSandboxProperties.setProperty(TOOLTIP, removeSandboxDescription);

		TaskFactory removeSandboxTaskFactory = new RemoveSandboxTaskFactory(configDirFile);
		registerAllServices(bc, removeSandboxTaskFactory, removeSandboxProperties);
		
		// Register getFileInfo function
		String getFileInfoDescription = "Return metadata for a sandboxed file or directory";
		
		//Note: we can use markdown in our long descriptions, hence the ``` code block style.
		String getFileInfoLongDescription = "Accepts the sandbox (```sandboxName```) and object name (```fileName```) and returns the corresponding absolute path and whether the object exists. If it does, the modified date and whether the object is a file or directory are returned, too."; 
		
		Properties getFileInfoProperties = new Properties();
		getFileInfoProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		getFileInfoProperties.setProperty(COMMAND, "getFileInfo");
		getFileInfoProperties.setProperty(COMMAND_DESCRIPTION,  getFileInfoDescription);
		getFileInfoProperties.setProperty(COMMAND_LONG_DESCRIPTION, getFileInfoLongDescription);
		getFileInfoProperties.setProperty(COMMAND_EXAMPLE_JSON, getFileInfoExample());
		getFileInfoProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		getFileInfoProperties.setProperty(PREFERRED_MENU, "Transfer");
		getFileInfoProperties.setProperty(IN_MENU_BAR, "true");
		getFileInfoProperties.setProperty(IN_CONTEXT_MENU, "false");
		getFileInfoProperties.setProperty(TITLE, "getFileInfo");
		getFileInfoProperties.setProperty(TOOLTIP,  getFileInfoDescription);

		TaskFactory getFileInfoTaskFactory = new GetFileInfoTaskFactory(configDirFile);
		registerAllServices(bc, getFileInfoTaskFactory, getFileInfoProperties);		
		
		// Register removeFile function
		String removeFileDescription = "Remove a sandboxed file or directory if one exists";
		
		//Note: we can use markdown in our long descriptions, hence the ``` code block style.
		String removeFileLongDescription = "Accepts the sandbox (```sandboxName```) and file name (```file```) and removes the file if it exists."; 
		
		Properties removeFileProperties = new Properties();
		removeFileProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		removeFileProperties.setProperty(COMMAND, "removeFile");
		removeFileProperties.setProperty(COMMAND_DESCRIPTION,  removeFileDescription);
		removeFileProperties.setProperty(COMMAND_LONG_DESCRIPTION, removeFileLongDescription);
		removeFileProperties.setProperty(COMMAND_EXAMPLE_JSON, getRemoveFileExample());
		removeFileProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		removeFileProperties.setProperty(PREFERRED_MENU, "Transfer");
		removeFileProperties.setProperty(IN_MENU_BAR, "true");
		removeFileProperties.setProperty(IN_CONTEXT_MENU, "false");
		removeFileProperties.setProperty(TITLE, "Remove File");
		removeFileProperties.setProperty(TOOLTIP,  removeFileDescription);

		TaskFactory removeFileTaskFactory = new RemoveFileTaskFactory(configDirFile);
		registerAllServices(bc, removeFileTaskFactory, removeFileProperties);		
	}
}

