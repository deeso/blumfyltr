package com.thecoverofnight.blumfyltr.bloomfilters;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.PrimitiveSink;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;

public class NamedStringBloomFilter implements BloomFilterInterface{
	String name = null;
	String baseDir = null;
	private BloomFilter<CharSequence> bloomFilter = null;
	
	
	public NamedStringBloomFilter(String name, int insertions, double fpp) {
		this.name = name;
		this.baseDir = System.getProperty("user.dir");
		bloomFilter = BloomFilter.create(new Funnel<CharSequence>() {
			@Override
			public void funnel(CharSequence from, PrimitiveSink into) {
				into.putUnencodedChars(from);
				
			}}, insertions, fpp);
	}
	
	public NamedStringBloomFilter(String name, String base_directory, int insertions, double fpp) {
		this.name = name;
		this.baseDir = base_directory;
		
		bloomFilter = BloomFilter.create(new Funnel<CharSequence>() {
			@Override
			public void funnel(CharSequence from, PrimitiveSink into) {
				into.putUnencodedChars(from);
				
			}}, insertions, fpp);
	}
	
	public NamedStringBloomFilter(int insertions, double fpp) {
		this.name = "";
		this.baseDir = "";
		
		bloomFilter = BloomFilter.create(new Funnel<CharSequence>() {
			@Override
			public void funnel(CharSequence from, PrimitiveSink into) {
				into.putUnencodedChars(from);
				
			}}, insertions, fpp);
	}
	
	
	public static NamedStringBloomFilter load(String name, String baseDir)  throws Exception{
		try {
			return new NamedStringBloomFilter(name, baseDir);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	private NamedStringBloomFilter(String name, String baseDir) throws Exception {
		this.name = name;
		this.baseDir = baseDir;
		try {
			Path filePath = Paths.get(baseDir, name);
			@SuppressWarnings("resource")
			FileInputStream stream = new FileInputStream(filePath.toString());
			bloomFilter = 
				    BloomFilter.readFrom(stream, Funnels.stringFunnel(Charset.defaultCharset()));
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		// FIXME the hash functions between the loaded file and this instance may not match
		throw new Exception("Dont use this yet.");
		
	}
	
	public NamedStringBloomFilter() {
		this.name = "";
		this.baseDir = "";
		int insertions = 10000;
		double fpp = 0.0001;
		bloomFilter = BloomFilter.create(new Funnel<CharSequence>() {
			@Override
			public void funnel(CharSequence from, PrimitiveSink into) {
				into.putUnencodedChars(from);
				
			}}, insertions, fpp);
	}
	
	public boolean check(String key) {
		return bloomFilter.mightContain(key);
	}
	
	public boolean add(String key) {
		return bloomFilter.put(key);
	}
	
	public boolean save() throws IOException {
		if (name.length() == 0)
			return false;
		
		File baseDirPath = new File(baseDir);
		if (!baseDirPath.exists())
			return false;
		
		try {
			Path filePath = Paths.get(baseDir, name);
			@SuppressWarnings("resource")
			FileOutputStream stream = new FileOutputStream(filePath.toString());
			bloomFilter.writeTo(stream);
			stream.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		//return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String string) {
		name = string;
	}

	@Override
	public Boolean checkKey(String key) {
		return check(key);
	}

	@Override
	public Boolean addKey(String key) {
		return add(key);
	}

	@Override
	public HashMap<String, Boolean> checkKeys(Collection<String> keys) {
		HashMap<String, Boolean> results = new HashMap<String, Boolean>();
		for (String key: keys){
			results.put(key, checkKey(key));
		}
		return results;
	}

	@Override
	public HashMap<String, Boolean> addKeys(Collection<String> keys) {
		HashMap<String, Boolean> results = new HashMap<String, Boolean>();
		for (String key: keys){
			results.put(key, addKey(key));
		}
		return results;

	}
}
