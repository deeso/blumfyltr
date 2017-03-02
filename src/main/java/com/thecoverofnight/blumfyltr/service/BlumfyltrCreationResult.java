package com.thecoverofnight.blumfyltr.service;

public class BlumfyltrCreationResult {
	String reason;
	Boolean failed;
	String name;
	Integer insertions;
	Double fpp;
	
	public BlumfyltrCreationResult(String reason) {
		this.failed = true;
		this.reason = reason;
	}
	
	public BlumfyltrCreationResult(String name, Integer insertions, Double fpp, boolean failed) {
		this.failed = failed;
		this.name = name;
		this.insertions = insertions;
		this.fpp = fpp;
		this.reason = failed ? "Unknown" : null;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Boolean getFailed() {
		return failed;
	}

	public void setFailed(Boolean failed) {
		this.failed = failed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getInsertions() {
		return insertions;
	}

	public void setInsertions(Integer insertions) {
		this.insertions = insertions;
	}

	public Double getFpp() {
		return fpp;
	}

	public void setFpp(Double fpp) {
		this.fpp = fpp;
	}
	

}
