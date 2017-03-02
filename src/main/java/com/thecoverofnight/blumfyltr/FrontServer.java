package com.thecoverofnight.blumfyltr;

import static spark.Spark.*;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FrontServer {
	
	
	public class User {
		String name;
		String auth_token;
		String email;
	}
	
	ArrayList<User> users = null;
	public void loadAllUsers(String filename){
		// load config from filename
		// read users in file
		// populate allowed users
		users = new ArrayList<User>();
	}
	
	static final String BF_USER_ADD = "/blumfyltr/user/add/";
	static final String BF_USER_CHECK = "/blumfyltr/user/check/";
	static final String BF_ADMIN_NEW = "/blumfyltr/admin/new";
	static final String BF_ADMIN_SAVE = "/blumfyltr/admin/save";
	static final String BF_ADMIN_RETIRE = "/blumfyltr/admin/retire";
	

	
    public static void main(String[] args) {

        //  port(5678); <- Uncomment this if you want spark to listen to port 5678 instead of the default 4567
    	port(8080);

//        post(BF_USER_ADD, "application/json", (request, response) ->
//        {
//        	// check user auth key
//        	// perform a check and add key
//        	response.status(200);
//        	String key = request.queryParams("key");
//        	
//        	BlumfyltrResult x = new BlumfyltrResult(key, false);
//        	Gson g = new Gson();
//        	System.out.println(g.toJson(x));
//        	return x;
//        }, new JsonTransformer());

        post(BF_USER_CHECK, "application/json", (request, response) ->
        {
        	
        	//String key = request.params("key");
        	
        	response.status(200);
        	JsonObject jobj = new JsonParser().parse(request.body()).getAsJsonObject();
        	String key = jobj.get("key").getAsString();
        	System.out.println(key);
        	BlumfyltrResult x = new BlumfyltrResult(key, false);
        	Gson g = new Gson();
        	System.out.println(g.toJson(x));

        	return x;
        }, new JsonTransformer());

    }
	
}
