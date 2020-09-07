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

	public static final String getExample() {
		return FromCytoscape.getJson(new FromCytoscapeResult("2020-07-29 03:00:00.0000", 10, "iVBORw0KGgoAAAANSUhEUgAABY="));
	}
	
	public void start(BundleContext bc) throws InvalidSyntaxException 
	{
		// Register FromCytoscape function
		String transferFromCytoscapeDescription = "Transfer a file from Cytoscape";
		
		//Note: we can use markdown in our long descriptions, hence the ``` code block style.
		String transferFromCytoscapeLongDescription = "Returns a Base64-encoded file image ```fileBase64``` and a small amount of file metadata ```fileByteCount``` and ```modifiedTime```."; 
		
		Properties fromCytoscapeProperties = new Properties();
		fromCytoscapeProperties.setProperty(COMMAND_NAMESPACE, FILE_TRANSFER_COMMAND_NAMESPACE);
		fromCytoscapeProperties.setProperty(COMMAND, "fromCytoscape");
		fromCytoscapeProperties.setProperty(COMMAND_DESCRIPTION,  transferFromCytoscapeDescription);
		fromCytoscapeProperties.setProperty(COMMAND_LONG_DESCRIPTION, transferFromCytoscapeLongDescription);
		fromCytoscapeProperties.setProperty(COMMAND_EXAMPLE_JSON, getExample());
		fromCytoscapeProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		fromCytoscapeProperties.setProperty(PREFERRED_MENU, "Transfer");
		fromCytoscapeProperties.setProperty(IN_MENU_BAR, "true");
		fromCytoscapeProperties.setProperty(IN_CONTEXT_MENU, "false");
		fromCytoscapeProperties.setProperty(TITLE, "From Cytoscape");
		fromCytoscapeProperties.setProperty(TOOLTIP,  transferFromCytoscapeDescription);

		TaskFactory fromCytoscapeTaskFactory = new FromCytoscapeTaskFactory();
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
		toCytoscapeProperties.setProperty(COMMAND_EXAMPLE_JSON, getExample());
		toCytoscapeProperties.setProperty(COMMAND_SUPPORTS_JSON, "true");
		toCytoscapeProperties.setProperty(PREFERRED_MENU, "Transfer");
		toCytoscapeProperties.setProperty(IN_MENU_BAR, "true");
		toCytoscapeProperties.setProperty(IN_CONTEXT_MENU, "false");
		toCytoscapeProperties.setProperty(TITLE, "To Cytoscape");
		toCytoscapeProperties.setProperty(TOOLTIP,  transferToCytoscapeDescription);

		TaskFactory toCytoscapeTaskFactory = new ToCytoscapeTaskFactory();
		registerAllServices(bc, toCytoscapeTaskFactory, toCytoscapeProperties);
		
		final CyApplicationConfiguration config = getService(bc, CyApplicationConfiguration.class);
		File appConfigDir = config.getAppConfigurationDirectoryLocation(CyActivator.class);
		System.out.println("App Config: " + appConfigDir.getAbsolutePath());
		File installDir = config.getInstallationDirectoryLocation();
		System.out.println("Install: " + installDir.getAbsolutePath());
		File configDir = config.getConfigurationDirectoryLocation();
		System.out.println("Config: " + configDir.getAbsolutePath());
	
	}
}

