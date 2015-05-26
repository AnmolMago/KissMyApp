package com.scribull.kissmyapp.Directions;

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
import android.os.SystemClock;
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

public class Directions extends Activity implements LocationListener{

	public static HashMap<String, ArrayList<MyLocation>> places = new HashMap<String, ArrayList<MyLocation>>();
	public static HashMap<String, String> closest = new HashMap<String, String>();
	public static LocationManager locationManager;
	public static Location recentLocation;
	
	public static String getRecentLocationString(){
		if(recentLocation == null)
			return "Not available!";
		return recentLocation.getLatitude() + "," + recentLocation.getLongitude();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		recentLocation = location;
		Log.d("MY LOCATION IS:", getRecentLocationString());
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d("LocationListener","disable");
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		Log.d("LocationListener","enable");
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("LocationListener","status: "+status);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directions);
		Toast.makeText(this, "Please Wait...", Toast.LENGTH_LONG).show();
        getActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFCC33));
        getActionBar().setDisplayHomeAsUpEnabled(true);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			recentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}else{
			android.location.Location newLocation = new android.location.Location(LocationManager.NETWORK_PROVIDER);
	        newLocation.setLatitude(43.7908174);
	        newLocation.setLongitude(-79.6783477);
	        newLocation.setAccuracy(40);
	        newLocation.setTime(System.currentTimeMillis());
	        recentLocation = newLocation;
		}
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new CategoriesView()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.directions, menu);
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

		private final String[] categories = new String[]{"HOSPITAL", "DOCTOR", "PHARMACHY", "PHYSIO", "FITNESS"};
		
		public CategoriesView() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_directions,container, false);
			//TODO ADD LOADING SCREEN GIF
			try {
				//Location mCurrentLocation = mLocationClient.getLastLocation();
				new getWebResponse().execute(new URL("http://scribull.com/?location="+getRecentLocationString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return rootView;
		}
		public class getWebResponse extends AsyncTask<URL, Integer, String> {

			long beforetime;
			
			@Override
			protected String doInBackground(URL... params) {
				try{
					beforetime = System.currentTimeMillis();
					HttpClient httpclient = new DefaultHttpClient();
				    HttpResponse response = httpclient.execute(new HttpGet(params[0].toString()));
				    StatusLine statusLine = response.getStatusLine();
				    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				        ByteArrayOutputStream out = new ByteArrayOutputStream();
				        response.getEntity().writeTo(out);
				        out.close();
				        String responseString = out.toString();
			        	final JSONObject obj = (JSONObject)JSONValue.parse(responseString);
			        	if(obj == null){
			        		Log.d("obj", responseString);
			        	}
				        for(String category : categories){
				        	final MyJson optionsList = new MyJson(obj).next(category);
				        	if(category.equalsIgnoreCase("hospital")){
					        	ArrayList<MyLocation> locations = new ArrayList<MyLocation>();
					        	MyLocation civicLoc = new MyLocation("Brampton Civic", category, "ChIJf7FSGCsWK4gRyOiyfz4esjo");
					        	MyLocation etobGLoc = new MyLocation("Etobicoke General", category, "ChIJJz7w7FM6K4gR9_MiTWv52ec");
					        	if(new MyJson(optionsList).next("whichOne").toString() == "civic"){
					        		locations.add(civicLoc);
					        		locations.add(etobGLoc);
					        	}else{
					        		locations.add(etobGLoc);
					        		locations.add(civicLoc);
					        	}
					        	places.put(category, locations);
				        		closest.put(category, new MyJson(optionsList).next("closestEntry").toString());
				        		continue;
				        	}
				        	if(category.equalsIgnoreCase("fitness"))
				        		Log.d("response", obj.keySet().toString());
				        	if(obj == null){
				        		Log.d("nullll", responseString);
				        	}
			        		closest.put(category, new MyJson(optionsList).next("closestEntry").toString());
				        	ArrayList<MyLocation> locations = new ArrayList<MyLocation>();
				        	for(int i = 0;i <=9;i++){
				        		final MyJson option = new MyJson(optionsList).next(i+"");
				        		String name = new MyJson(option).next("name").toString();
				        		String id = new MyJson(option).next("id").toString();
				        		locations.add(new MyLocation(name, category, id));
				        	}
				        	places.put(category, locations);
				        }
				    } else{
				        //Closes the connection.
				        response.getEntity().getContent().close();
				        throw new IOException(statusLine.getReasonPhrase());
				    }
				}catch(Exception e){
					Log.e("ErrorResponse", e.getMessage(),e);
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(String result) {
				//TODO REMOVE LOADING SCREEN GIF
				if(getView() == null)
					return;
				LinearLayout layout = (LinearLayout) getView().findViewById(R.id.listLL);
				TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,
			            LayoutParams.WRAP_CONTENT, 1f);
	
				for (final String category : categories) {
					LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService
						      (Context.LAYOUT_INFLATER_SERVICE);
					View row = inflater.inflate(R.layout.categoryrow, ((ViewGroup)getView().getParent()), false);
			        TextView labelView = (TextView) row.findViewById(R.id.label);
			        TextView valueView = (TextView) row.findViewById(R.id.value);
			        labelView.setText(category);
			        valueView.setText("Nearest: "+closest.get(category));
				    Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/DIN Black.ttf"); 
				    labelView.setTypeface(type);
				    params = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);
				    row.setLayoutParams(params);
				    //Set last divider in list invisible
		            if(category == categories[categories.length-1]) {
		            	//row.setBackground(null);
		            }
		            row.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							getFragmentManager().beginTransaction().replace(R.id.container, new OptionsView(category, places.get(category))).addToBackStack( "categoriesView" ).commit();
						}
					});
				    // Adding it to the linear layout, they will all have a weight of 1, which will make them spread out evenly.
				    layout.addView(row);
				}
			}
		}
	}

	public static class OptionsView extends Fragment{

		final String parent;
		final ArrayList<MyLocation> options;
		
		public OptionsView(){
			parent = "ERROR";
			options = null;
		}
		
		public OptionsView(String parent, ArrayList<MyLocation> options){
			this.parent = parent;
			this.options = options;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_directionsoptions,container, false);
			TextView parentLabel = (TextView) rootView.findViewById(R.id.parentLabel);
			parentLabel.setText(parent);
		    Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/DIN Black.ttf"); 
		    parentLabel.setTypeface(type);
			MyAdapter adapter = new MyAdapter(getActivity(), options);
			ListView listView = (ListView) rootView.findViewById(R.id.directionsOptionsList);
			listView.setAdapter(adapter);
			return rootView;
		}
		
		public class MyAdapter extends ArrayAdapter<MyLocation> {
			 
	        private final Context context;
	        private final ArrayList<String> names;
	        private final HashMap<String, MyLocation> itemsArrayList;
	 
	        public MyAdapter(Context context, ArrayList<MyLocation> locList) {
	 
	            super(context, R.layout.directionoption, locList);
	 
	            this.context = context;
	            this.itemsArrayList = new HashMap<String, MyLocation>();
	            this.names = new ArrayList<String>();
	            if(locList == null){
		        	MyLocation civicLoc = new MyLocation("Brampton Civic", "HOSPITAL", "ChIJf7FSGCsWK4gRyOiyfz4esjo");
		        	MyLocation etobGLoc = new MyLocation("Etobicoke General","HOSPITAL", "ChIJJz7w7FM6K4gR9_MiTWv52ec");
		        	locList = new ArrayList<MyLocation>();
		        	locList.add(civicLoc);
		        	locList.add(etobGLoc);
	            }
	            for(MyLocation loc : locList){
	            	names.add(loc.getName());
	            	itemsArrayList.put(loc.getName(), loc);
	            }
	        }
	 
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {

	            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	            View rowView = inflater.inflate(R.layout.directionoption, parent, false);

	            TextView placeName = (TextView) rowView.findViewById(R.id.placeName);
	            TextView placeDistance = (TextView) rowView.findViewById(R.id.placeDistance);

	            placeName.setText(itemsArrayList.get(names.get(position)).getName());
	            //TODO placeDistance.setText(itemsArrayList.get(position).getDistance());
	            rowView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
			            TextView placeName = (TextView) v.findViewById(R.id.placeName);
						getFragmentManager().beginTransaction().replace(R.id.container, new DetailsView(itemsArrayList.get(placeName.getText()))).addToBackStack( "optionsView" ).commit();
					}
				});
	 
	            // 5. retrn rowView
	            return rowView;
	        }
		}
		
	}
	
	public static class DetailsView extends Fragment{

		final MyLocation location;
		
		public DetailsView(){
			this(null);
		}
		
		public DetailsView(MyLocation location){
			this.location = location;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_directionsoptiondetails,container, false);
			//TODO ADD LOADING SCREEN GIF
			try {
				//Location mCurrentLocation = mLocationClient.getLastLocation();
				new getWebResponse(location).execute(new URL("http://scribull.com/?details="+location.getId()+"&location="+getRecentLocationString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return rootView;
		}

		public class getWebResponse extends AsyncTask<URL, Integer, String> {
	
			long beforetime;
			MyLocation location;
			MyJson data;
			Drawable d;
			String coord;
			
			public getWebResponse(MyLocation location){
				super();
				this.location = location;
				data = null;
				d = null;
				coord = null;
			}
			
			@Override
			protected String doInBackground(URL... params) {
				try{
					beforetime = System.currentTimeMillis();
					HttpClient httpclient = new DefaultHttpClient();
				    HttpResponse response = httpclient.execute(new HttpGet(params[0].toString()));
				    StatusLine statusLine = response.getStatusLine();
				    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				        ByteArrayOutputStream out = new ByteArrayOutputStream();
				        response.getEntity().writeTo(out);
				        out.close();
				        String responseString = out.toString();
			        	final JSONObject obj = (JSONObject)JSONValue.parse(responseString);
			        	if(obj == null){
			        		Log.d("obj", responseString);
			        	}
			        	data = new MyJson(obj);
			        	coord = new MyJson(data).next("coord").toString();
			        	URL url = new URL("https://maps.googleapis.com/maps/api/staticmap?center="+coord+"&zoom=14&size=300x300&maptype=roadmap&markers=color:red%7C"+coord);
			        	InputStream content = (InputStream)url.getContent();
			        	d = Drawable.createFromStream(content , "src"); 
				    } else{
				        //Closes the connection.
				        response.getEntity().getContent().close();
				        throw new IOException(statusLine.getReasonPhrase());
				    }
				}catch(Exception e){
					Log.e("ErrorResponse", e.getMessage(),e);
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(String result) {
				//TODO REMOVE LOADING SCREEN GIF
				if(getView() == null)
					return;
	        	String name = new MyJson(data).next("name").toString();
	        	final String address = new MyJson(data).next("address").toString();
	        	String distance = new MyJson(data).next("distance").toString();
	        	String[] distanceParts = distance.split(" ");
	        	distance = distanceParts[0] + " KM";
	        	final String phone = new MyJson(data).next("phone").toString();
	        	RelativeLayout fdo = (RelativeLayout) getView().findViewById(R.id.FDO);
				TextView nameView = (TextView) fdo.findViewById(R.id.optionName);
				TextView distanceView = (TextView) fdo.findViewById(R.id.optionDistance);
				TextView addressView = (TextView) fdo.findViewById(R.id.optionAddress);
				TextView phoneView = (TextView) fdo.findViewById(R.id.optionPhone);
				nameView.setText(name);
				distanceView.setText(distance);
				addressView.setText(address);
				phoneView.setText(phone);
				ImageView iv = (ImageView) fdo.findViewById(R.id.googleImage);
		         Toast.makeText(getActivity(), 
		         "Click number to call, and map for directions.", Toast.LENGTH_LONG).show();
				iv.setImageDrawable(d);
				iv.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent navigation = new Intent(Intent.ACTION_VIEW, Uri
						        .parse("http://maps.google.com/maps?saddr="
						                +getRecentLocationString() + "&daddr="
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
			}
		}
	}
	
}
