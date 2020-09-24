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

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.work.TaskFactory;

public class CyActivator extends AbstractCyActivator {

	public static final String FILE_TRANSFER_COMMAND_NAMESPACE = "filetransfer";

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) throws InvalidSyntaxException 
	{
		final CyApplicationConfiguration config = getService(bc, CyApplicationConfiguration.class);
		File configDirFile = new File(config.getConfigurationDirectoryLocation().getAbsoluteFile(), 
				                   FILE_TRANSFER_COMMAND_NAMESPACE);
		File installDirFile = config.getInstallationDirectoryLocation().getAbsoluteFile();

		// Register fromSandbox function
		Properties fromSandboxProperties = new Properties();
		fromSandboxProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		fromSandboxProperties.setProperty(COMMAND, "fromSandbox");
		fromSandboxProperties.setProperty(COMMAND_DESCRIPTION,  FromSandboxTaskFactory.DESCRIPTION);
		fromSandboxProperties.setProperty(COMMAND_LONG_DESCRIPTION, FromSandboxTaskFactory.LONG_DESCRIPTION);
		fromSandboxProperties.setProperty(COMMAND_EXAMPLE_JSON, FromSandboxTask.getExample());
		fromSandboxProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		fromSandboxProperties.setProperty(PREFERRED_MENU, "Transfer");
		fromSandboxProperties.setProperty(IN_MENU_BAR, "false");
		fromSandboxProperties.setProperty(IN_CONTEXT_MENU, "false");
		fromSandboxProperties.setProperty(TITLE, "From Sandbox");
		fromSandboxProperties.setProperty(TOOLTIP,  FromSandboxTaskFactory.DESCRIPTION);

		TaskFactory fromSandboxTaskFactory = new FromSandboxTaskFactory(configDirFile);
		registerAllServices(bc, fromSandboxTaskFactory, fromSandboxProperties);

		// Register toSandbox function
		Properties toSandboxProperties = new Properties();
		toSandboxProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		toSandboxProperties.setProperty(COMMAND, "toSandbox");
		toSandboxProperties.setProperty(COMMAND_DESCRIPTION,  ToSandboxTaskFactory.DESCRIPTION);
		toSandboxProperties.setProperty(COMMAND_LONG_DESCRIPTION, ToSandboxTaskFactory.LONG_DESCRIPTION);
		toSandboxProperties.setProperty(COMMAND_EXAMPLE_JSON, ToSandboxTask.getExample());
		toSandboxProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		toSandboxProperties.setProperty(PREFERRED_MENU, "Transfer");
		toSandboxProperties.setProperty(IN_MENU_BAR, "false");
		toSandboxProperties.setProperty(IN_CONTEXT_MENU, "false");
		toSandboxProperties.setProperty(TITLE, "To Sandbox");
		toSandboxProperties.setProperty(TOOLTIP, ToSandboxTaskFactory.DESCRIPTION);

		TaskFactory toSandboxTaskFactory = new ToSandboxTaskFactory(configDirFile);
		registerAllServices(bc, toSandboxTaskFactory, toSandboxProperties);
		
		// Register setSandbox function
		Properties setSandboxProperties = new Properties();
		setSandboxProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		setSandboxProperties.setProperty(COMMAND, "setSandbox");
		setSandboxProperties.setProperty(COMMAND_DESCRIPTION,  SetSandboxTaskFactory.DESCRIPTION);
		setSandboxProperties.setProperty(COMMAND_LONG_DESCRIPTION, SetSandboxTaskFactory.LONG_DESCRIPTION);
		setSandboxProperties.setProperty(COMMAND_EXAMPLE_JSON, SetSandboxTask.getExample());
		setSandboxProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		setSandboxProperties.setProperty(PREFERRED_MENU, "Transfer");
		setSandboxProperties.setProperty(IN_MENU_BAR, "false");
		setSandboxProperties.setProperty(IN_CONTEXT_MENU, "false");
		setSandboxProperties.setProperty(TITLE, "Set Sandbox");
		setSandboxProperties.setProperty(TOOLTIP, SetSandboxTaskFactory.DESCRIPTION);

		TaskFactory setSandboxTaskFactory = new SetSandboxTaskFactory(configDirFile, installDirFile);
		registerAllServices(bc, setSandboxTaskFactory, setSandboxProperties);
		
		// Register removeSandbox function
		Properties removeSandboxProperties = new Properties();
		removeSandboxProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		removeSandboxProperties.setProperty(COMMAND, "removeSandbox");
		removeSandboxProperties.setProperty(COMMAND_DESCRIPTION,  RemoveSandboxTaskFactory.DESCRIPTION);
		removeSandboxProperties.setProperty(COMMAND_LONG_DESCRIPTION, RemoveSandboxTaskFactory.LONG_DESCRIPTION);
		removeSandboxProperties.setProperty(COMMAND_EXAMPLE_JSON, RemoveSandboxTask.getExample());
		removeSandboxProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		removeSandboxProperties.setProperty(PREFERRED_MENU, "Transfer");
		removeSandboxProperties.setProperty(IN_MENU_BAR, "false");
		removeSandboxProperties.setProperty(IN_CONTEXT_MENU, "false");
		removeSandboxProperties.setProperty(TITLE, "Remove Sandbox");
		removeSandboxProperties.setProperty(TOOLTIP, RemoveSandboxTaskFactory.DESCRIPTION);

		TaskFactory removeSandboxTaskFactory = new RemoveSandboxTaskFactory(configDirFile);
		registerAllServices(bc, removeSandboxTaskFactory, removeSandboxProperties);
		
		// Register getFileInfo function
		Properties getFileInfoProperties = new Properties();
		getFileInfoProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		getFileInfoProperties.setProperty(COMMAND, "getFileInfo");
		getFileInfoProperties.setProperty(COMMAND_DESCRIPTION,  GetFileInfoTaskFactory.DESCRIPTION);
		getFileInfoProperties.setProperty(COMMAND_LONG_DESCRIPTION, GetFileInfoTaskFactory.LONG_DESCRIPTION);
		getFileInfoProperties.setProperty(COMMAND_EXAMPLE_JSON, GetFileInfoTask.getExample());
		getFileInfoProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		getFileInfoProperties.setProperty(PREFERRED_MENU, "Transfer");
		getFileInfoProperties.setProperty(IN_MENU_BAR, "false");
		getFileInfoProperties.setProperty(IN_CONTEXT_MENU, "false");
		getFileInfoProperties.setProperty(TITLE, "getFileInfo");
		getFileInfoProperties.setProperty(TOOLTIP,  GetFileInfoTaskFactory.DESCRIPTION);

		TaskFactory getFileInfoTaskFactory = new GetFileInfoTaskFactory(configDirFile);
		registerAllServices(bc, getFileInfoTaskFactory, getFileInfoProperties);		
		
		// Register removeFile function
		Properties removeFileProperties = new Properties();
		removeFileProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		removeFileProperties.setProperty(COMMAND, "removeFile");
		removeFileProperties.setProperty(COMMAND_DESCRIPTION,  RemoveFileTaskFactory.DESCRIPTION);
		removeFileProperties.setProperty(COMMAND_LONG_DESCRIPTION, RemoveFileTaskFactory.LONG_DESCRIPTION);
		removeFileProperties.setProperty(COMMAND_EXAMPLE_JSON, RemoveFileTask.getExample());
		removeFileProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		removeFileProperties.setProperty(PREFERRED_MENU, "Transfer");
		removeFileProperties.setProperty(IN_MENU_BAR, "false");
		removeFileProperties.setProperty(IN_CONTEXT_MENU, "false");
		removeFileProperties.setProperty(TITLE, "Remove File");
		removeFileProperties.setProperty(TOOLTIP,  RemoveFileTaskFactory.DESCRIPTION);

		TaskFactory removeFileTaskFactory = new RemoveFileTaskFactory(configDirFile);
		registerAllServices(bc, removeFileTaskFactory, removeFileProperties);		
	}
}

