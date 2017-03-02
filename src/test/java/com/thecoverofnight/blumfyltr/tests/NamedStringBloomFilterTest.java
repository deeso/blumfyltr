package com.thecoverofnight.blumfyltr.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thecoverofnight.blumfyltr.NamedStringBloomFilter;

public class NamedStringBloomFilterTest {
	@Rule
	public TemporaryFolder folder= new TemporaryFolder();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void newNamedBloomFilterAnon(){
		NamedStringBloomFilter nsbf = new NamedStringBloomFilter();
		assertFalse("Found 'test' in empty filter", nsbf.check("test"));
		assertTrue("Failed to add 'test'", nsbf.add("test"));
		assertFalse("Found to 'test1'", nsbf.check("test1"));
		try {
			assertFalse("Created a 0-length filename", nsbf.save());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void newNamedBloomFilterTemp() throws Exception{
		String test_bf_name = "test-name.bf";
		File createdFolder = folder.newFolder("baseDir");
		Path path = Paths.get(createdFolder.getAbsolutePath(), test_bf_name);
		NamedStringBloomFilter nsbf = new NamedStringBloomFilter(test_bf_name, createdFolder.getAbsolutePath(), 100, 0.00001);
		
		assertFalse("Found 'test' in empty filter", nsbf.check("test"));
		assertTrue("Failed to add 'test'", nsbf.add("test"));
		assertTrue("Failed to find'test'", nsbf.check("test"));
		assertFalse("Found to 'test1'", nsbf.check("test1"));
		
		try {
			assertTrue("Failed to Create a BF file", nsbf.save());
			File filePath = path.toFile();
			assertTrue("File did not exist after save.", filePath.exists());
		} catch (IOException e) {
			e.printStackTrace();
		}
		NamedStringBloomFilter nsbf_loaded = null;
		try {
			nsbf_loaded = NamedStringBloomFilter.load(test_bf_name, createdFolder.getAbsolutePath());			
		} catch (IOException e) {
			e.printStackTrace();
		}
		nsbf_loaded.setName(test_bf_name+"-copy");
		assertTrue("Failed to Load a BF file", nsbf_loaded != null);
		assertFalse("Found to 'test1'", nsbf_loaded.check("test1"));
		//assertTrue("Failed to Load a BF file", nsbf_loaded == nsbf);
		boolean x = nsbf_loaded.check("test");
		
		nsbf_loaded.save();
		NamedStringBloomFilter nsbf3 = new NamedStringBloomFilter(test_bf_name+'3', createdFolder.getAbsolutePath(), 100, 0.00001);
		nsbf3.save();
			File filePath = path.toFile();
			assertTrue("File did not exist after save.", filePath.exists());

		assertTrue("Failed to find 'test'", x);

		
	}

}
