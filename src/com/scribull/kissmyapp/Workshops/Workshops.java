package com.scribull.kissmyapp.Workshops;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.scribull.kissmyapp.R;
import com.scribull.kissmyapp.Util.MyJson;

public class Workshops extends Activity{
	
	static ArrayList<Workshop> workshopList = new ArrayList<Workshop>();
	
	public class Workshop{
		String name, phone, date, time, address;
		public Workshop(String name, String phone, String date,String time, String address){
			this.name = name;
			this.phone = phone;
			this.date = date;
			this.time = time;
			this.address = address;
		}
		public String getName() {
			return name;
		}
		public String getPhone() {
			return phone;
		}
		public String getDate() {
			return date;
		}
		public String getTime() {
			return time;
		}
		public String getAddress() {
			return address;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workshops);
        getActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFCC33));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        workshopList = new ArrayList<Workshop>();
        Workshop workshop1 = new Workshop("Central West Self Management Program", "905-494-6752", "Nov 26, 2014", "12:30 PM to 4:30 PM", "2250 Bovaird Drive East, Brampton, L6R 0W3");
        Workshop workshop2 = new Workshop("Central West Self Management Program", "905-494-6752", "Dec 11, 2014", "8:00 AM to 12:30 PM", "2250 Bovaird Drive East, Brampton, L6R 0W3");
        Workshop workshop3 = new Workshop("Central West Self Management Program", "905-494-6752", "Jan 14, 2015", "8:00 AM to 12:30 PM", "2250 Bovaird Drive East, Brampton, L6R 0W3");
        Workshop workshop4 = new Workshop("Central West Self Management Program", "905-494-6752", "Feb 25, 2015", "8:00 AM to 12:30 PM", "2250 Bovaird Drive East, Brampton, L6R 0W3");
        workshopList.add(workshop1);
        workshopList.add(workshop2);
        workshopList.add(workshop3);
        workshopList.add(workshop4);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new CategoriesView()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.workshops, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class CategoriesView extends Fragment{
		
		public CategoriesView() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_workshops,container, false);

			//TODO REMOVE LOADING SCREEN GIF
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.listLL);
			TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,
		            LayoutParams.WRAP_CONTENT, 1f);

			for (final Workshop workshop : workshopList) {
				View row = inflater.inflate(R.layout.categoryrow, ((ViewGroup)rootView.getParent()), false);
		        TextView labelView = (TextView) row.findViewById(R.id.label);
		        TextView valueView = (TextView) row.findViewById(R.id.value);
		        labelView.setText(workshop.getName());
		        labelView.setTextSize(28);
		        valueView.setText(""+workshop.getDate());
			    Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/DIN Black.ttf"); 
			    labelView.setTypeface(type);
			    params = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);
			    row.setLayoutParams(params);
			    //Set last divider in list invisible
	            if(workshop == workshopList.get(workshopList.size()-1)){
	            	//row.setBackground(null);
	            }
	            row.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						getFragmentManager().beginTransaction().replace(R.id.container, new DetailsView(workshop)).addToBackStack( "categoriesView" ).commit();
					}
				});
			    // Adding it to the linear layout, they will all have a weight of 1, which will make them spread out evenly.
			    layout.addView(row);
			}
		
			return rootView;
		}
		
	}
	
	public static class DetailsView extends Fragment{

		final Workshop workshop;
		
		public DetailsView(){
			this(null);
		}
		
		public DetailsView(Workshop workshop){
			this.workshop = workshop;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_workshopsoptiondetails,container, false);
			//TODO REMOVE LOADING SCREEN GIF
        	String name = workshop.getName();
        	final String address = workshop.getAddress();
        	final String phone = workshop.getPhone();
        	RelativeLayout fdo = (RelativeLayout) rootView.findViewById(R.id.FDO);
			TextView nameView = (TextView) fdo.findViewById(R.id.optionName);
			TextView distanceView = (TextView) fdo.findViewById(R.id.optionDistance);
			TextView addressView = (TextView) fdo.findViewById(R.id.optionAddress);
			TextView phoneView = (TextView) fdo.findViewById(R.id.optionPhone);
			nameView.setText(name);
			addressView.setText(address);
			phoneView.setText(phone);
			ImageView iv = (ImageView) fdo.findViewById(R.id.googleImage);
	         Toast.makeText(getActivity(), 
	         "Click number to call, and map for directions.", Toast.LENGTH_LONG).show();
			iv.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent navigation = new Intent(Intent.ACTION_VIEW, Uri
					        .parse("http://maps.google.com/maps?saddr="
					                +workshop.getAddress() + "&daddr="
					                + address));
					startActivity(navigation);
				}
			});
			phoneView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent phoneIntent = new Intent(Intent.ACTION_CALL);
				      phoneIntent.setData(Uri.parse("tel:"+phone));
				      try {
				    	  startActivity(phoneIntent);
				         Log.i("Finished making a call...", "");
				      } catch (android.content.ActivityNotFoundException ex) {
				         Toast.makeText(getActivity(), 
				         "Call failed, please try again later.", Toast.LENGTH_SHORT).show();
				      }
				}
			});
			return rootView;
		}
	}
	
}
