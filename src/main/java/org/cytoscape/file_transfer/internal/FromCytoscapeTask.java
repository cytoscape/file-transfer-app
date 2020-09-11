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

public class FromCytoscapeTask extends AbstractTask implements ObservableTask {
	
	private File sandboxParentDirFile;
	
	public FromCytoscapeTask(File sandboxParentDirFile) {
		super();
		this.sandboxParentDirFile = sandboxParentDirFile;
	}
	
	@ProvidesTitle
	public String getTitle() { return "Transfer file from a Cytoscape sandbox"; }

	@Tunable (description="sandboxName", longDescription="Name of sandbox containing file", exampleStringValue="mySandbox")
	public String sandboxName = "";
	
	@Tunable (description="fileName", longDescription="Sandbox-relative name of file.", exampleStringValue="myFile.png")
	public String fileName = "";
	
	private FromCytoscapeResult result;
	
	@Override
	public void run(TaskMonitor taskMon) throws Exception {
		File fileFile = SandboxUtils.getAbsFileFile(sandboxParentDirFile, sandboxName, fileName, false);
		if (fileFile.exists() && !fileFile.isFile()) {
			throw new Exception("'" + fileName + "' must identify a file, not a directory, in sandbox '" + sandboxName + "'.");
		}

		try {
			String modifiedTime = SandboxUtils.getModifiedTime(fileFile); 
			
			taskMon.setStatusMessage("Reading file " + fileFile.getName());

			byte[] allBytes = Files.readAllBytes(Paths.get(fileFile.getCanonicalPath()));
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
