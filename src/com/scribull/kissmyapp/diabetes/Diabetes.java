package com.scribull.kissmyapp.diabetes;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.scribull.kissmyapp.R;
import com.scribull.kissmyapp.TabletManagement.TabletManagement.DetailTabletView.Assoc;
import com.scribull.kissmyapp.TabletManagement.TabletManagement.DetailTabletView.MyAdapter2;
import com.scribull.kissmyapp.database.DiabetesDatabaseUtil;
import com.scribull.kissmyapp.database.DiabetesDatabaseUtil.DiabetesEntry;
import com.scribull.kissmyapp.database.TabletDatabaseUtil.TabletEntry;

public class Diabetes extends Activity {

	public Diabetes(){}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diabetes);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().replace(R.id.container, new DiabetesHome()).commit();
		}
        getActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFCC33));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.diabetes, menu);
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

	public static class DiabetesHome extends Fragment {

		public DiabetesHome() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_diabetes_home,
					container, false);
			 ArrayList<DEntry> entries = new ArrayList<DEntry>();
			 DiabetesDatabaseUtil TDHelper = new DiabetesDatabaseUtil(getActivity());
			 SQLiteDatabase db = TDHelper.getWritableDatabase();
		        //read value
		        String[] projection = {
		        		DiabetesEntry.DIABETES_ID,
		                DiabetesEntry.DIABETES_DATE,
		                DiabetesEntry.DIABETES_BG,
		                DiabetesEntry.DIABETES_SAI,
		                DiabetesEntry.DIABETES_LAI
		        };
		        String sortOrder =
		        	    DiabetesEntry.DIABETES_DATE + " DESC";
		        Cursor c =null;
		        try{
		        	c= db.query(
		        		DiabetesEntry.TABLE,  // The table to query
		        		projection,                               // The columns to return
		        		null,                                // The columns for the WHERE clause
		        		null,                            // The values for the WHERE clause
		        		null,                                     // don't group the rows
		        		null,                                     // don't filter by row groups
		        		sortOrder                                 // The sort order
		        		);
		        }catch(Exception ex){
		        	TDHelper.delete(db);
		        	TDHelper.onCreate(db);
		        	Toast.makeText(rootView.getContext(), "Ran out of time! Exception in database so database deleted!", Toast.LENGTH_SHORT).show();
		        }
		        c.moveToFirst();
		        while (c.isAfterLast() == false) {
			        try{
			            String id = c.getString(c.getColumnIndexOrThrow(DiabetesEntry.DIABETES_ID));
			            String date = c.getString(c.getColumnIndexOrThrow(DiabetesEntry.DIABETES_DATE));
			            String bg = c.getString(c.getColumnIndexOrThrow(DiabetesEntry.DIABETES_BG));
			            String sai = c.getString(c.getColumnIndexOrThrow(DiabetesEntry.DIABETES_SAI));
			            String lai = c.getString(c.getColumnIndexOrThrow(DiabetesEntry.DIABETES_LAI));
			            DEntry entry = new DEntry(date, bg, sai, lai);
			            entries.add(entry);
			            c.moveToNext();
			        }catch(Exception ex){
			        	ex.printStackTrace();
			        }
		        }
			TextView addButton = (TextView) rootView.findViewById(R.id.addButton);
			Log.w("fdsf", "sdfsdf" + entries.size());
			addButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.w("fdsf", "adding");
					getFragmentManager().beginTransaction().replace(R.id.container, new AddEntry()).addToBackStack("home").commit();

				}
			});
			 
			TextView dataButton = (TextView) rootView.findViewById(R.id.dataButton);
			final ArrayList<DEntry> Fentries = new ArrayList<DEntry>(entries);
			dataButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getFragmentManager().beginTransaction().replace(R.id.container, new dataView(Fentries)).addToBackStack("home").commit();
				}
			});
				return rootView;
		}
	}

	public static class AddEntry extends Fragment {
		static long longDate=System.currentTimeMillis();
		public static Calendar c = Calendar.getInstance();
		String image;
		public AddEntry() {
			Log.w("fdsf", "added");
			c.setTime(new Date(longDate));
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_diabetes_add,
					container, false);
			Button submitLog = (Button)rootView.findViewById(R.id.submitDlog);
	        TextView date = (TextView) rootView.findViewById(R.id.editdateLabel);
	        date.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				    DialogFragment newFragment = new DatePickerFragment((TextView) v);
				    newFragment.show(getFragmentManager(), "datePicker");
				}
			});
	        TextView time = (TextView) rootView.findViewById(R.id.edittimeLabel);
	        time.setOnClickListener(new View.OnClickListener() { 
				
				@Override
				public void onClick(View v) {
				    DialogFragment newFragment = new TimePickerFragment((TextView) v);
				    newFragment.show(getFragmentManager(), "datePicker");
				}
			});
	        final EditText BGView = ((EditText)rootView.findViewById(R.id.editglucose));
	        final EditText SAIView = ((EditText)rootView.findViewById(R.id.editshortInsulinLabel));
	        final EditText LAIView = ((EditText)rootView.findViewById(R.id.editlongInsulinLabel));
			submitLog.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Context context = v.getContext();
					try{
						DiabetesDatabaseUtil TDHelper = new DiabetesDatabaseUtil(context);
				        SQLiteDatabase db = TDHelper.getWritableDatabase();

				        final String BG = BGView.getText().toString(); 
				        Log.w("fdsdfs", BG + " is all there is");
				        final String SAI = SAIView.getText().toString(); 
				        final String LAI = LAIView.getText().toString(); 
				        ContentValues values = new ContentValues();
				        values.put(DiabetesEntry.DIABETES_DATE,c.getTimeInMillis()+"");
				        values.put(DiabetesEntry.DIABETES_BG, BG);
				        values.put(DiabetesEntry.DIABETES_SAI,SAI);
				        values.put(DiabetesEntry.DIABETES_LAI, LAI);
				        db.insert(DiabetesEntry.TABLE, "null", values);
					}catch(NullPointerException ex){
						Toast.makeText(context, "No Time for Error Handling ... NullPointerException", Toast.LENGTH_SHORT).show();
						ex.printStackTrace();
					}catch(NumberFormatException ex){
						Toast.makeText(context, "No Time for Error Handling ... NumberFormatException", Toast.LENGTH_SHORT).show();
						ex.printStackTrace();
					}catch(StringIndexOutOfBoundsException ex){
						Toast.makeText(context, "No Time for Error Handling ... StringIndexOutOfBoundsException", Toast.LENGTH_SHORT).show();
						ex.printStackTrace();
					}catch(Exception ex){
						Toast.makeText(context, "No Time for Error Handling ... General Exception", Toast.LENGTH_SHORT).show();
						ex.printStackTrace();
					}
				}
				
				private String getTextFromView(View v){
					Log.w("ussnull", v == null ? "YES" : "NO");
					EditText tv = (EditText)v;
					Log.w("ufsdfssnull", tv == null ? "YES" : "NO");
					return tv.getText().toString();
				}
			});
				return rootView;
		}
	}

	public static class DEntry{
		String date, BG, SAI, LAI;
		public DEntry(String date, String BG, String SAI, String LAI){
			this.date = date;
			this.BG = BG;
			this.SAI = SAI;
			this.LAI = LAI;
		}
	}
	
	public static class dataView extends Fragment {
		
		ArrayList<DEntry> data;

		public dataView() {}
		public dataView(ArrayList<DEntry> data) {
			this.data = data;
			Log.w("fdsf", "sdfsdf" + data.size());
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_diabetes_data,container, false);
            ListView lv = (ListView) rootView.findViewById(R.id.pastEntries);
            lv.setAdapter(new MyAdapter2(rootView.getContext(), data));
			return rootView;
		}
		
		public static class MyAdapter2 extends ArrayAdapter<DEntry> {
			 
	        private final Context context;
	        private final ArrayList<DEntry> hugeList;
	 
	        public MyAdapter2(Context context, ArrayList<DEntry> hugeList) {
	            super(context, R.layout.fragment_diabetes_list, hugeList);
	            this.context = context;
	            this.hugeList = hugeList;
	        }
		 
		    @SuppressWarnings("deprecation")
			@Override
		    public View getView(int position, View convertView, ViewGroup parent) {

		            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		            View rowView = inflater.inflate(R.layout.fragment_diabetes_list, parent, false);

		            DEntry entry = hugeList.get(position);
		            
		            TextView dglucose = (TextView) rowView.findViewById(R.id.dglucose);		            
		            TextView date = (TextView) rowView.findViewById(R.id.date);           
		            TextView sai = (TextView) rowView.findViewById(R.id.SAI);           
		            TextView lai = (TextView) rowView.findViewById(R.id.LAI);

		            DateFormatSymbols dfs = new DateFormatSymbols();
		            final String[] shortWeekdayString = dfs.getShortWeekdays();
		            final String[] longWeekdayString = dfs.getWeekdays();
		            final String[] shortMonthString = dfs.getShortMonths();
		            final String[] longMonthString = dfs.getMonths();
		            dglucose.setText(entry.BG);
		            Date reallyDate = new Date();
		            reallyDate.setTime(Long.parseLong(entry.date));
		            date.setText(longMonthString[reallyDate.getMonth()] + " " + reallyDate.getDate() + ", " + (1900+reallyDate.getYear()));
		            sai.setText(entry.SAI);
		            lai.setText(entry.LAI);
		            
		            return rowView;
		        }
			}
		
	}

	public static class ex extends Fragment {

		public ex() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_diabetes_home,container, false);
				return rootView;
		}
	}
	

	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		TextView v;
        DateFormatSymbols dfs = new DateFormatSymbols();
        final String[] shortWeekdayString = dfs.getShortWeekdays();
        final String[] shortMonthString = dfs.getShortMonths();
		
		public DatePickerFragment(TextView v){
			this.v = v;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
		
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
			v.setText(day + " " + shortMonthString[month] + " " + year);
			AddEntry.c = Calendar.getInstance();
			AddEntry.c.set(Calendar.YEAR, year);
			AddEntry.c.set(Calendar.MONTH, month);
			AddEntry.c.set(Calendar.DAY_OF_MONTH, day);
		}
	}
	
	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		
		TextView v;
		
		public TimePickerFragment(TextView v){
			this.v = v;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute, false);
		}
		
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			int textHourOfDay = hourOfDay;
			String am_pm = "AM";
			if(hourOfDay > 12){
				textHourOfDay -= 12;
			}
			if(hourOfDay>11){
				am_pm = "PM";
			}
			v.setText(textHourOfDay+":"+minute + " " + am_pm);
			AddEntry.c = Calendar.getInstance();
			AddEntry.c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			AddEntry.c.set(Calendar.MINUTE, minute);
		}
	}
	
}
