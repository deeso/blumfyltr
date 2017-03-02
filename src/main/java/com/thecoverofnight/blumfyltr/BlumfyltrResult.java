package com.thecoverofnight.blumfyltr;

public class BlumfyltrResult {

	public String key;
	public boolean result;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
	
	public BlumfyltrResult(String key, boolean result){
		this.key = key;
		this.result = result;
	}
	
	
}