package com.scribull.kissmyapp.Workshops;

import android.util.Log;

public class MyLocation {

	String name, category, id, address, phone, distance, image;
	
	public MyLocation(String name, String category, String id){
		this.name = name;
		this.category = category;
		this.id = id;
		//Log.d("new JSON", name + " | " + category + " | " + id);
	}
	
	public MyLocation(String name, String category, String id, String address, String phone, String distance, String image){
		this.name = name;
		this.category = category;
		this.id = id;
		this.address = address;
		this.phone = phone;
		this.distance = distance;
		this.image = image;
	}
	
	public String getName(){
		return name;
	}
	
	public String getId(){
		return id;
	}
	
	public String getAddress(){
		return address;
	}
	
	public String getPhone(){
		return phone;
	}
	
	public String getDistance(){
		return distance;
	}
	
}
