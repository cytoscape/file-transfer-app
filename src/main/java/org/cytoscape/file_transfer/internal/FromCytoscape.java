package org.cytoscape.file_transfer.internal;

import java.io.File;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;

import com.google.gson.Gson;

public class FromCytoscape extends AbstractTask implements ObservableTask {
	
	public FromCytoscape(){
		super();
	}
	
	@ProvidesTitle
	public String getTitle() { return "Transfer file from Cytoscape"; }

	@Tunable (description="file", longDescription="The name of the file to read and return", exampleStringValue="test.png")
	public String file = "";
	
	private FromCytoscapeResult result;
	
	@Override
	public void run(TaskMonitor taskMon) throws Exception {
		if (file == null || file.length() == 0) {
			throw new Exception("File name cannot be empty.");
		}

		try {
			File f = new File(file);
			SimpleDateFormat s = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSSS");
			String modifiedTime = s.format(new Date(f.lastModified()));
			
			taskMon.setStatusMessage("Reading file " + file);
			
			byte[] allBytes = Files.readAllBytes(Paths.get(file));
			long fileByteCount = allBytes.length;
			String fileBase64 = new String(Base64.encodeBase64(allBytes), StandardCharsets.UTF_8);

			result = new FromCytoscapeResult(modifiedTime, fileByteCount, fileBase64);
		} catch (Throwable e) {
			throw new Exception(e.toString());
		}
	}
	
	public static final String getJson(FromCytoscapeResult result) {
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
