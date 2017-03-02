package com.thecoverofnight.blumfyltr.service;

import java.util.HashMap;

public class BlumfyltrResult {
	
	public Boolean failed = false;
	public String reason = null;
	
	HashMap<String, Boolean> results = new HashMap<String, Boolean>();

	public Boolean getFailed() {
		return failed;
	}

	public void setFailed(Boolean failed) {
		this.failed = failed;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public BlumfyltrResult(String key, Boolean result){
		this.results.put(key, result);
	}
	
	public BlumfyltrResult(String key, String reason){
		this.results.put(key, false);
		this.failed = true;
		this.reason = reason;
	}
	
	public BlumfyltrResult(String reason) {
		this.reason = reason;
		this.failed = true;
	}

	public BlumfyltrResult(HashMap<String, Boolean> results) {
		for (String key : results.keySet()) {
			this.results.put(key, results.get(key));
		}
	}
}