package org.cytoscape.file_transfer.internal;

import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.json.JSONResult;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import org.cytoscape.work.AbstractTask;


public abstract class CyRESTAbstractTask extends AbstractTask implements ObservableTask {
	
	public CyRESTAbstractResult result;

	
	public static String getExample() {
		return "";
	}
	
	public static final String getJson(CyRESTAbstractResult result) {
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
