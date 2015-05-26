package com.scribull.kissmyapp.Util;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.util.Log;

public class MyJson {

	JSONObject obj;
	String result = null;
	
	public MyJson(MyJson obj){
		this.obj = obj.getJSONObject();
	}
	
	public MyJson(JSONObject obj){
		this.obj = obj;
	}
	
	public MyJson next(String child){
		if(obj.get(child) == null)
			return this;
		JSONObject test = (JSONObject)JSONValue.parse(obj.get(child).toString());
		if(test == null){
			result = obj.get(child).toString();
			return this;
		}
		obj = test;
		return this;
	}
	
	public JSONObject getJSONObject(){
		return obj;
	}
	
	@Override
	public String toString(){
		if(result != null)
			return result;
		return obj.toString();
	}
	
}
