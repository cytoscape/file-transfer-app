package org.cytoscape.file_transfer.internal;

import java.io.File;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;

import com.google.gson.Gson;

public class ToCytoscapeTask extends AbstractTask implements ObservableTask {
	
	public ToCytoscapeTask(){
		super();
	}
	
	@ProvidesTitle
	public String getTitle() { return "Transfer file to Cytoscape"; }

	@Tunable (description="file", longDescription="The name of the file to save", exampleStringValue="test.png")
	public String file = "";
	
	@Tunable (description="fileBase64", longDescription="The file content as Base64", exampleStringValue="iVBORw0KGgoAAAANSUhEUgAABY=")
	public String fileBase64 = "";

	@Tunable (description="fileByteCount", longDescription="The count of bytes in the raw file", exampleStringValue="10")
	public long fileByteCount = 0;

	@Tunable (description="overwrite", longDescription="True to overwrite a file if it already exists", exampleStringValue="true")
	public boolean overwrite = false;

	private ToCytoscapeResult result;
	
	@Override
	public void run(TaskMonitor taskMon) throws Exception {
		if (file == null || file.length() == 0) {
			throw new Exception("File name cannot be empty.");
		}
		if (fileBase64 == null || fileBase64.length() == 0) {
			throw new Exception("File content cannot be empty.");
		}
		
		if (!overwrite) {
			File f = new File(file);
			if (f.exists()) {
				throw new Exception("File '" + file + "' already exists");
			}
		}		
		
		byte[] fileRaw = Base64.decodeBase64(fileBase64);
		if (fileRaw.length != fileByteCount) {
			throw new Exception("File '" + file + "' contains " + fileRaw.length + " bytes but should contain " + fileByteCount + " bytes");
		}
		
		taskMon.setStatusMessage("Writing file " + file);
		Files.write(Paths.get(file), fileRaw);
		
		result = new ToCytoscapeResult(file);
	}
	
	public static final String getJson(ToCytoscapeResult result) {
		return new Gson().toJson(result);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		if (type.equals(String.class)) {	
			return (R) getJson(result);
		} 
		/* This is where we return JSON from this Task. 
		 */
		else if (type.equals(JSONResult.class)) {
			JSONResult res = () -> {return getJson(result);};
			return (R)(res);
		} else {
			return null;
		}
	}

	@Override 
	public List<Class<?>> getResultClasses() {
		return Arrays.asList(String.class, JSONResult.class);
	}
}
