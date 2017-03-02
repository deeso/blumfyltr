package com.thecoverofnight.blumfyltr.frontend;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.json.JSONArray;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thecoverofnight.blumfyltr.bloomfilters.BloomFilterInterface;
import com.thecoverofnight.blumfyltr.bloomfilters.NamedStringBloomFilter;
import com.thecoverofnight.blumfyltr.service.BlumfyltrCreateNewRequest;
import com.thecoverofnight.blumfyltr.service.BlumfyltrCreationResult;
import com.thecoverofnight.blumfyltr.service.BlumfyltrResult;

public class BasicServer {
	
	static final String BF_USER_ADD = "/blumfyltr/user/add/";
	static final String BF_USER_CHECK = "/blumfyltr/user/check/";
	static final String BF_ADMIN_NEW = "/blumfyltr/admin/new/";
	static final String BF_ADMIN_SAVE = "/blumfyltr/admin/save";
	static final String BF_ADMIN_RETIRE = "/blumfyltr/admin/retire";
	
	static final String BF_SERVER_PARAMET_KEYS = "keys";
	
	
	static final String BF_SERVER_PARAMET_NEW = "new";
	static final String BF_SERVER_PARAMET_NEW_FPP = "fpp";
	static final String BF_SERVER_PARAMET_NEW_INS = "insertions";
	static final String BF_SERVER_PARAMET_NEW_name = "name";

	
	static final String REASON_FAILED_JSON_PARSE = "Failed to parse request.";
	static final String REASON_FAILED_KEY_CHECK = "Failed to check for keys in bloomfilter.";
	
	static String defaultName = "default";
	static int defaultInsertions = 1000000;
	static double defaultFpp = .00001;
	
	static final String PARSER_HELP_ME = "help";
	static final String PARSER_BF_FALSE_POSITIVE_PROBABILITY = "fpp";
	static final String PARSER_BF_INSERTIONS = "insertions";
	static final String PARSER_BF_NAME = "name";
	
	static final String PARSER_PORT = "port";
	static final String PARSER_HOST = "host";
	
	Options options = new Options();
	static Option myHelpOption = new Option(PARSER_HELP_ME, "print the help message");
	
	static Option myInsertions = Option.builder()
			.longOpt(PARSER_BF_INSERTIONS)
			.hasArg()
			.required(false)
			.type(Integer.class)
			.desc("number of expected insertions")
			.build();
	
	static Option myFpp = Option.builder()
			.longOpt(PARSER_BF_FALSE_POSITIVE_PROBABILITY)
			.hasArg()
			.required(false)
			.desc("False positive rate")
			.type(Double.class)
			.build();

	static Option myName= Option.builder()
			.longOpt(PARSER_BF_NAME)
			.hasArg()
			.required(false)
			.desc("name for the filter (for future case of saving)")
			.type(String.class)
			.build();


	static Option myPort = Option.builder()
			.longOpt(PARSER_PORT)
			.hasArg()
			.required(false)
			.type(Integer.class)
			.desc("port to listen on")
			.build();
	
	static Option myHost = Option.builder()
			.longOpt(PARSER_HOST)
			.hasArg()
			.required(false)
			.desc("IP Address to listen on")
			.type(String.class)
			.build();
	
	public static Options myOptions = new Options()
			.addOption(myName).addOption(myInsertions).addOption(myFpp)
			.addOption(myHost).addOption(myPort).addOption(myHelpOption);
	
	static BloomFilterInterface bloomFilter = getNewInstance();
	private static Integer defaultPort = 12000;
	private static String defaultHost = "0.0.0.0";
			
	static private BloomFilterInterface getNewInstance() {
		return getNewInstance(defaultName, defaultInsertions, defaultFpp);
	}
	
	static private BloomFilterInterface getNewInstance(String name, Integer insertions, Double fpp) {
		return new NamedStringBloomFilter(name, insertions, fpp);
	}

	private static BlumfyltrCreationResult handleNewBloomFilter(BlumfyltrCreateNewRequest nbfr) {
		bloomFilter = getNewInstance(nbfr.getName(), nbfr.getInsertions(), nbfr.getFpp());
		return new BlumfyltrCreationResult(nbfr.getName(), nbfr.getInsertions(), nbfr.getFpp(), false);
	}
	
	public static BlumfyltrCreationResult adminNew(spark.Request request, spark.Response response, boolean add) {
		try {
			BlumfyltrCreateNewRequest nbfr = (new Gson()).fromJson(request.body(), BlumfyltrCreateNewRequest.class);
	      	if (nbfr.getName() == null){
	      		nbfr.setName(defaultName);
	      	}
	      	if (nbfr.getFpp() == null){
	      		nbfr.setFpp(defaultFpp);
	      	}
	      	if (nbfr.getInsertions() == null){
	      		nbfr.setInsertions(defaultInsertions);
	      	}
	      	BlumfyltrCreationResult x = handleNewBloomFilter(nbfr);
	      	if (x.getFailed()) response.status(400);
	      	else response.status(200);
	      	return x;
		} catch (Exception e) {
			response.status(400);
			e.printStackTrace();
			return new BlumfyltrCreationResult(REASON_FAILED_JSON_PARSE);
		}      	
	}

	public static BlumfyltrResult userCheck(spark.Request request, spark.Response response, boolean add) {
      	ArrayList<String> keys = null;
      	JsonObject jobj = null;
		try {
	      	jobj = new JsonParser().parse(request.body()).getAsJsonObject();
	      	JsonArray jsa = jobj.get(BF_SERVER_PARAMET_KEYS).getAsJsonArray();
	      	keys = new ArrayList<String>();
	      	for (JsonElement jso : jsa) {
	      		keys.add(jso.getAsString());
	      	}
		} catch (Exception e) {
			response.status(400);
			return new BlumfyltrResult(REASON_FAILED_JSON_PARSE);
		}
		
		if (!add){
			try {
				HashMap<String, Boolean> results = bloomFilter.checkKeys(keys);
				BlumfyltrResult x = new BlumfyltrResult(results);
				response.status(200);
				return x;
			} catch (Exception o) {
				response.status(400);
				o.printStackTrace();
				return new BlumfyltrResult(REASON_FAILED_KEY_CHECK);
			}			
		} else {
			try {
				HashMap<String, Boolean> results = bloomFilter.addKeys(keys);
				BlumfyltrResult x = new BlumfyltrResult(results);
				response.status(200);
				return x;
			} catch (Exception o) {
				response.status(400);
				o.printStackTrace();
				return new BlumfyltrResult(REASON_FAILED_KEY_CHECK);
			}
		}
      	
	}
	
	
	public static void setup_bf_user_check() {
      post(BF_USER_CHECK, "application/json", (request, response) ->
      {
      	return userCheck(request, response, false);
      }, new JsonTransformer());		
	}
	
	public static void setup_bf_user_add() {
      post(BF_USER_ADD, "application/json", (request, response) ->
      {
      	return userCheck(request, response, true);
      }, new JsonTransformer());		
	}
	
	public static void setup_bf_filter_management() {
	      post(BF_ADMIN_NEW, "application/json", (request, response) ->
	      {
	    	// TODO: authenticatoin
	      	return adminNew(request, response, false);
	      }, new JsonTransformer());		
		}

	public static void main(String[] args) {
		CommandLineParser parser = new DefaultParser();
		CommandLine cli;
		
		try {
			cli = parser.parse(BasicServer.getOptions(), args);
			defaultInsertions = cli.hasOption(PARSER_BF_INSERTIONS) ? Integer.valueOf(cli.getOptionValue(PARSER_BF_INSERTIONS)) : defaultInsertions;
			defaultFpp = cli.hasOption(PARSER_BF_FALSE_POSITIVE_PROBABILITY) ? Double.valueOf(cli.getOptionValue(PARSER_BF_FALSE_POSITIVE_PROBABILITY)) : defaultFpp;
			defaultName = cli.hasOption(PARSER_BF_NAME) ? cli.getOptionValue(PARSER_BF_NAME) : defaultName;
			
			defaultHost = cli.hasOption(PARSER_HOST) ? cli.getOptionValue(PARSER_HOST) : defaultHost;
			defaultPort = cli.hasOption(PARSER_PORT) ? Integer.valueOf(cli.getOptionValue(PARSER_PORT)) : defaultPort;
			
		} catch (ParseException e) {

			e.printStackTrace();
			return;
		}
		
		port(defaultPort);
		
    	setup_bf_user_check();
    	setup_bf_user_add();
    	setup_bf_filter_management();

    }

	private static Options getOptions() {
		return myOptions;
	}
	
}
