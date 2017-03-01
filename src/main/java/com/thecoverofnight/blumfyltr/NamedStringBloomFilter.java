package com.thecoverofnight.blumfyltr;

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

public class NamedStringBloomFilter {
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
	
	
	public static NamedStringBloomFilter load(String name, String baseDir)  throws IOException{
		return new NamedStringBloomFilter(name, baseDir);
	}
	private NamedStringBloomFilter(String name, String baseDir) throws IOException {
		this.name = name;
		this.baseDir = baseDir;
		try {
			Path filePath = Paths.get(baseDir, name);
			@SuppressWarnings("resource")
			FileInputStream stream = new FileInputStream(filePath.toString());
			bloomFilter = 
				    BloomFilter.readFrom(stream, Funnels.stringFunnel(Charset.defaultCharset()));

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
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
}
