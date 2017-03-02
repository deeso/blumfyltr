package com.thecoverofnight.blumfyltr.bloomfilters;

import java.util.Collection;
import java.util.HashMap;

public interface BloomFilterInterface {	
	public Boolean checkKey(String key);
	public Boolean addKey(String key);
	
	public HashMap<String, Boolean> checkKeys(Collection<String> keys);
	public HashMap<String, Boolean> addKeys(Collection<String> keys);
}
