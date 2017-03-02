package com.thecoverofnight.blumfyltr.service;

public class BlumfyltrCreateNewRequest {
	String name = null;
	Integer insertions = null;
	Double fpp = null;
	public BlumfyltrCreateNewRequest (){}
	public BlumfyltrCreateNewRequest (String name, Integer insertions, Double fpp){
		this.name = name;
		this.insertions = insertions;
		this.fpp = fpp;
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
