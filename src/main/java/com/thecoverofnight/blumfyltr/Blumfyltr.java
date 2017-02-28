package com.thecoverofnight.blumfyltr;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.json.JSONObject;;

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
	
	ArrayList<NamedStringBloomFilter> chainedFilters = new ArrayList<NamedStringBloomFilter>();
	
	public NamedStringBloomFilter newBFTimeName(String bfname) throws IOException{
		return newBFTimeName(bfname, this.defaultInsertions, this.defaultFpp);
	}
	
	public NamedStringBloomFilter newBFTimeName(String bfname, int insertions, double fpp) throws IOException{
		NamedStringBloomFilter bf = new NamedStringBloomFilter(bfname, baseDirectory, insertions, fpp);
		addNewNamedStringBloomFilter(bf);
		return bf;
	}
	
	public NamedStringBloomFilter newBFTimeNameNow(int insertions, double fpp) throws IOException {
		ZonedDateTime now = ZonedDateTime.now( ZoneOffset.UTC ).withSecond(0).withNano(0);
		String bfname = now.format(DateTimeFormatter.ISO_INSTANT);
		return newBFTimeName(bfname, insertions, fpp);
	}
	
	public NamedStringBloomFilter newBFTimeNameNowNoMinute(int insertions, double fpp) throws IOException {
		ZonedDateTime now = ZonedDateTime.now( ZoneOffset.UTC ).withSecond(0).withNano(0).withMinute(0);
		String bfname = now.format(DateTimeFormatter.ISO_INSTANT);
		return newBFTimeName(bfname, insertions, fpp);
	}
	
	public NamedStringBloomFilter newBFTimeNameNowNoHour(int insertions, double fpp) throws IOException {
		ZonedDateTime now = ZonedDateTime.now( ZoneOffset.UTC ).withSecond(0).withNano(0).withMinute(0).withHour(0);
		String bfname = now.format(DateTimeFormatter.ISO_INSTANT);
		return newBFTimeName(bfname, insertions, fpp);
	}
	
	public void addNewNamedStringBloomFilter(NamedStringBloomFilter bf) throws IOException {
		synchronized (chainedFilters) {
			chainedFilters.add(bf);
			if (chainedFilters.size() >= maxNumFilters) {
				NamedStringBloomFilter obf = chainedFilters.remove(maxNumFilters);
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
		for (NamedStringBloomFilter bf : chainedFilters) {
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
		for (NamedStringBloomFilter bf : chainedFilters) {
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
		for (NamedStringBloomFilter bf : chainedFilters) {
			boolean result = bf.check(key);
			if (result) 
				bfnames.add(bf.getName());
		}
		return bfnames;
	}
	
	
	
	public static void main(String[] args) throws JSONException {
		NamedStringBloomFilter nbf = new NamedStringBloomFilter();
        String genreJson = "";
        JSONObject json = new JSONObject(genreJson);
	}
}
