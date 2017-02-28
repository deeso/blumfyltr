package com.thecoverofnight.blumfyltr;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Blumfyltr {
	
	
	int maxFilterLifetime;
	int maxNumFilters;
	
	int defaultInsertions;
	double defaultFpp;
	
	boolean saveOldFilters;
	String baseDirectory;
	ZonedDateTime start;
	ZonedDateTime last;
	
	Boolean modLock = true;
	
	// TODO start timer thread that checks if last time has expired
	
	// TODO JSON handler for the front end server
	
	ArrayList<NameedStringBloomFilter> chainedFilters = new ArrayList<NameedStringBloomFilter>();
	
	public NameedStringBloomFilter newBFTimeName(String bfname) throws IOException{
		return newBFTimeName(bfname, this.defaultInsertions, this.defaultFpp);
	}
	
	public NameedStringBloomFilter newBFTimeName(String bfname, int insertions, double fpp) throws IOException{
		NameedStringBloomFilter bf = new NameedStringBloomFilter(bfname, baseDirectory, insertions, fpp);
		addNewNamedStringBloomFilter(bf);
		return bf;
	}
	
	public NameedStringBloomFilter newBFTimeNameNow(int insertions, double fpp) throws IOException {
		ZonedDateTime now = ZonedDateTime.now( ZoneOffset.UTC ).withSecond(0).withNano(0);
		String bfname = now.format(DateTimeFormatter.ISO_INSTANT);
		return newBFTimeName(bfname, insertions, fpp);
	}
	
	public NameedStringBloomFilter newBFTimeNameNowNoMinute(int insertions, double fpp) throws IOException {
		ZonedDateTime now = ZonedDateTime.now( ZoneOffset.UTC ).withSecond(0).withNano(0).withMinute(0);
		String bfname = now.format(DateTimeFormatter.ISO_INSTANT);
		return newBFTimeName(bfname, insertions, fpp);
	}
	
	public NameedStringBloomFilter newBFTimeNameNowNoHour(int insertions, double fpp) throws IOException {
		ZonedDateTime now = ZonedDateTime.now( ZoneOffset.UTC ).withSecond(0).withNano(0).withMinute(0).withHour(0);
		String bfname = now.format(DateTimeFormatter.ISO_INSTANT);
		return newBFTimeName(bfname, insertions, fpp);
	}
	
	public void addNewNamedStringBloomFilter(NameedStringBloomFilter bf) throws IOException {
		synchronized (chainedFilters) {
			chainedFilters.add(bf);
			if (chainedFilters.size() >= maxNumFilters) {
				NameedStringBloomFilter obf = chainedFilters.remove(maxNumFilters);
				if (saveOldFilters){
					obf.save();
				}
			}			
		}
	}
	
	public boolean add(String key) throws Exception {
		return check(key, true);
	}
	
	public boolean check (String key) throws Exception {
		return check(key, false);
	}
	
	public boolean check (String key, boolean add) throws Exception {
		boolean res = false;
		// FIXME: this is a bottlnece
		synchronized (chainedFilters) {
			res = checkFilters(key);
			// no need to check length, exception happens if no filters exist
			if (add) {
				res = chainedFilters.get(0).add(key);
			}
		}
		return res;
	}
		
	private boolean checkFilters (String key) throws Exception {
		if (chainedFilters.size() == 0) {
			throw new Exception("No BloomFilters in the chain");
		}
		boolean result = false;
		for (NameedStringBloomFilter bf : chainedFilters) {
			result = bf.check(key);
			if (result) break;
		}
		return result;
	}
	
	public String getFirstFilterName (String key) throws Exception {
		if (chainedFilters.size() == 0) {
			throw new Exception("No BloomFilters in the chain");
		}
		String bfname = "";
		for (NameedStringBloomFilter bf : chainedFilters) {
			boolean result = bf.check(key);
			if (result) 
				bfname = bf.getName();
		}
		return bfname;
	}
	
	public ArrayList<String> getAllFilterName (String key) throws Exception {
		if (chainedFilters.size() == 0) {
			throw new Exception("No BloomFilters in the chain");
		}
		ArrayList<String> bfnames = new ArrayList<String>();
		for (NameedStringBloomFilter bf : chainedFilters) {
			boolean result = bf.check(key);
			if (result) 
				bfnames.add(bf.getName());
		}
		return bfnames;
	}
	
	
	
	public static void main(String[] args) {
		NameedStringBloomFilter nbf = new NameedStringBloomFilter();	
	}
}
