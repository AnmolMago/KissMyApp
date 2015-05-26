package com.scribull.kissmyapp.TabletManagement;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.scribull.kissmyapp.R;
import com.scribull.kissmyapp.database.TabletDatabaseUtil;
import com.scribull.kissmyapp.database.TabletDatabaseUtil.TabletEntry;

public class TabletManagement extends Activity {

    long recentInsert=-1;
    public static ArrayList<Tablet> tablets = new ArrayList<Tablet>();
    
    public static ArrayList<Tablet> getTablets(){
    	return tablets;
    }
    
    private static final int[] DAY_ORDER = new int[] {
            Calendar.SUNDAY,
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		tablets = new ArrayList<Tablet>();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tablet_management);
        getActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFCC33));
        TabletDatabaseUtil TDHelper = new TabletDatabaseUtil(this);
        SQLiteDatabase db = TDHelper.getWritableDatabase();
        //read value
        String[] projection = {
        		TabletEntry.TABLET_ENTRY_ID,
        		TabletEntry.TABLET_NAME,
        		TabletEntry.TABLET_COLOR,
        		TabletEntry.TABLET_START,
        		TabletEntry.TABLET_REPEAT_TIMES
        };
        String sortOrder =
        	    TabletEntry.TABLET_ENTRY_ID + " DESC";
        if(!getApplicationContext().getDatabasePath("WHOSTablets.db").exists())
        	TDHelper.onCreate(db);
        Cursor c =null;
        try{
        	c= db.query(
       			TabletEntry.TABLE,  // The table to query
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
        	Toast.makeText(getApplicationContext(), "Ran out of time! Exception in database so database deleted!", Toast.LENGTH_SHORT).show();
        }
        c.moveToFirst();
        while (c.isAfterLast() == false) {
	        try{
	            String id = c.getString(c.getColumnIndexOrThrow(TabletEntry.TABLET_ENTRY_ID));
	            String name = c.getString(c.getColumnIndexOrThrow(TabletEntry.TABLET_NAME));
	            String color = c.getString(c.getColumnIndexOrThrow(TabletEntry.TABLET_COLOR));
	            String start = c.getString(c.getColumnIndexOrThrow(TabletEntry.TABLET_START));
	            String repeatTimes = c.getString(c.getColumnIndexOrThrow(TabletEntry.TABLET_REPEAT_TIMES));
	            Log.w("has it really happend?", repeatTimes);
	            Tablet tablet = new Tablet(id, name, color, start, start, repeatTimes);
	            tablet.setNotif(this);
	            tablets.add(tablet);
	            c.moveToNext();
	        }catch(Exception ex){
	        	ex.printStackTrace();
	        }
        }
		getFragmentManager().beginTransaction().replace(R.id.container, new TableView(tablets)).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tablet_management, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_add) {
			getFragmentManager().beginTransaction().replace(R.id.container, new InputView()).commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	public static class DetailTabletView extends Fragment{

		Tablet tablet;
        DateFormatSymbols dfs = new DateFormatSymbols();
        final String[] longWeekdayString = dfs.getWeekdays();
		
		public DetailTabletView(Tablet tablet){
			this.tablet = tablet;
		}
		
		@Override
		public View onCreateView(final LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tablet_detailstabletview,container, false);
			TextView tabletName = (TextView) rootView.findViewById(R.id.tabletName);
			tabletName.setText(tablet.getName());
			ImageView iv = (ImageView) rootView.findViewById(R.id.tabletImage);
			iv.setImageDrawable(tablet.getDrawable(rootView.getContext()));
            iv.setScaleType(ScaleType.CENTER_CROP);
            ListView lv = (ListView) rootView.findViewById(R.id.tabletTimeList);
            ArrayList<Assoc> hugeList = new ArrayList<Assoc>();
            for(Integer[] intArr : tablet.getRepeatTimes().keySet()){
            	ArrayList<Integer> dayList = tablet.getRepeatTimes().get(intArr);
            	String AP = "AM";
            	if(intArr[0] > 12){
            		AP = "PM";
            		intArr[0] -=12;
            	}
            	String time = intArr[0] +":"+intArr[1]+AP;
            	for(Integer i :dayList){
            		hugeList.add(new Assoc(time, longWeekdayString[i]));
            	}
            }
            lv.setAdapter(new MyAdapter2(rootView.getContext(), hugeList));
            Button delete = (Button) rootView.findViewById(R.id.deleteTablet);
            delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					TabletDatabaseUtil TDHelper = new TabletDatabaseUtil(v.getContext());
					SQLiteDatabase db = TDHelper.getWritableDatabase();
					db.delete(TabletEntry.TABLE, TabletEntry.TABLET_ENTRY_ID + "=" + tablet.getId(), null);
					tablets.remove(tablet);
					if(tablet.getAlarm() != null){
						AlarmManager alarmManager= (AlarmManager)v.getContext().getSystemService(Context.ALARM_SERVICE);
						alarmManager.cancel(tablet.getAlarm());
					}
					getFragmentManager().beginTransaction().replace(R.id.container, new TableView(tablets)).commit();
				}
			});
			return rootView;
		}
        
        public static class Assoc{
        	String time, day;
			public Assoc(String time, String day){
        		this.time = time;
        		this.day = day;
        	}
        	public String getTime() {
				return time;
			}
			public String getDay() {
				return day;
			}
        }
		
		public static class MyAdapter2 extends ArrayAdapter<Assoc> {
			 
	        private final Context context;
	        private final ArrayList<Assoc> hugeList;
	 
	        public MyAdapter2(Context context, ArrayList<Assoc> hugeList) {
	            super(context, R.layout.fragment_tablet_detailstabletview, hugeList);
	            this.context = context;
	            this.hugeList = hugeList;
	        }
		 
		        @Override
		        public View getView(int position, View convertView, ViewGroup parent) {
		        	Log.w("postition", position+"");

		            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		            View rowView = inflater.inflate(R.layout.tablet_list, parent, false);

		            Assoc ass = hugeList.get(position);
		            
		            TextView listTime = (TextView) rowView.findViewById(R.id.listTime);		            
		            TextView listDate = (TextView) rowView.findViewById(R.id.listDate);

		            listTime.setText(ass.getTime());
		            listDate.setText(ass.getDay());
		 
		            // 5. retrn rowView
		            return rowView;
		        }
			}
	}

	public static class DetailDayView extends Fragment{

		ArrayList<Tablet> tablets;
		String day;
		
		public DetailDayView(ArrayList<Tablet> tablets, String day){
			this.tablets = tablets;
			this.day = day;
		}
		
		@Override
		public View onCreateView(final LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tablet_detailsdayview,container, false);
			TextView tv = (TextView) rootView.findViewById(R.id.dayName);
			tv.setText(day);
			GridView gv = (GridView) rootView.findViewById(R.id.tabletGrid);
			gv.setAdapter(new MyAdapter(getActivity(), tablets));
			Log.w("size", tablets.size() + "");
			return rootView;
		}
	
	
	public class MyAdapter extends ArrayAdapter<Tablet> {
		 
        private final Context context;
        private final ArrayList<Tablet> tablets;
 
        public MyAdapter(Context context, ArrayList<Tablet> tablets) {
 
            super(context, R.layout.activity_tablet_management, tablets);
            this.context = context;
            this.tablets = tablets;
            }
	 
	        @Override
	        public View getView(final int position, View convertView, ViewGroup parent) {

	            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	            View rowView = inflater.inflate(R.layout.details_day, parent, false);
	            Tablet t = tablets.get(position);
	            ImageView iv = (ImageView) rowView.findViewById(R.id.tabletImage);
	            iv.setImageDrawable(t.getDrawable(getActivity()));
	            iv.setScaleType(ScaleType.FIT_XY);
	            TextView tabletName = (TextView) rowView.findViewById(R.id.tabletName);
	            tabletName.setText(t.getName());

	            //TODO placeDistance.setText(itemsArrayList.get(position).getDistance());
	            rowView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
			            TextView placeName = (TextView) v.findViewById(R.id.placeName);
						getFragmentManager().beginTransaction().replace(R.id.container, new DetailTabletView(tablets.get(position))).addToBackStack( "optionsView" ).commit();
					}
				});
	 
	            // 5. retrn rowView
	            return rowView;
	        }
		}

		
	}
	
	public static class TableView extends Fragment{

        DateFormatSymbols dfs = new DateFormatSymbols();
        final String[] shortWeekdayString = dfs.getShortWeekdays();
        final String[] longWeekdayString = dfs.getWeekdays();
        final String[] shortMonthString = dfs.getShortMonths();
        ArrayList<Tablet> tablets = new ArrayList<Tablet>(), sundayTablets = new ArrayList<Tablet>(), 
        		mondayTablets = new ArrayList<Tablet>(), tuesdayTablets = new ArrayList<Tablet>(), 
        		wednesdayTablets = new ArrayList<Tablet>(), thursdayTablets = new ArrayList<Tablet>(), 
        		fridayTablets = new ArrayList<Tablet>(), saturdayTablets = new ArrayList<Tablet>();
        ArrayList<ArrayList<Tablet>> dailyTablets = new ArrayList<ArrayList<Tablet>>();; 
		public TableView(ArrayList<Tablet> tablets){
			sundayTablets = new ArrayList<Tablet>();
			mondayTablets = new ArrayList<Tablet>();
			tuesdayTablets = new ArrayList<Tablet>();
			wednesdayTablets = new ArrayList<Tablet>();
			thursdayTablets = new ArrayList<Tablet>();
			fridayTablets = new ArrayList<Tablet>();
			saturdayTablets = new ArrayList<Tablet>();
			for(Tablet tablet : tablets){
				ArrayList<Integer> days = new ArrayList<Integer>();
				for(ArrayList<Integer> tDays : tablet.getRepeatTimes().values()){
					days.addAll(tDays);
				}
				if(days.contains(Calendar.SUNDAY) && !sundayTablets.contains(tablet)){
					sundayTablets.add(tablet);
				}
				if(days.contains(Calendar.MONDAY) && !mondayTablets.contains(tablet)){
					mondayTablets.add(tablet);
				}
				if(days.contains(Calendar.TUESDAY) && !tuesdayTablets.contains(tablet)){
					tuesdayTablets.add(tablet);
				}
				if(days.contains(Calendar.WEDNESDAY) && !wednesdayTablets.contains(tablet)){
					wednesdayTablets.add(tablet);
				}
				if(days.contains(Calendar.THURSDAY) && !thursdayTablets.contains(tablet)){
					thursdayTablets.add(tablet);
				}
				if(days.contains(Calendar.FRIDAY) && !fridayTablets.contains(tablet)){
					fridayTablets.add(tablet);
				}
				if(days.contains(Calendar.SATURDAY) && !saturdayTablets.contains(tablet)){
					saturdayTablets.add(tablet);
				}
			}
			dailyTablets = new ArrayList<ArrayList<Tablet>>(Arrays.asList(sundayTablets,mondayTablets,tuesdayTablets,wednesdayTablets, thursdayTablets, fridayTablets, saturdayTablets));
			for(int i = 0; i < 7; i++){
				Log.w("size of " + i + ":", dailyTablets.get(i).size()+"");
			}
		}
		
		@Override
		public View onCreateView(final LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tablet_table,container, false);
			LinearLayout column = (LinearLayout) rootView.findViewById(R.id.tableHolder);
			for(int i = 1;i<=7;i++){
				final int fi=i;
				View cell = inflater.inflate(R.layout.tablet_box,container, false);
				TextView date = (TextView)cell.findViewById(R.id.DayText);
				date.setText(shortWeekdayString[i].toUpperCase(Locale.US));
				ArrayList<Tablet> tablets = new ArrayList<Tablet>();
				LinearLayout ll = (LinearLayout) ((RelativeLayout)cell).getChildAt(0); 
				ArrayList<Tablet> tList = dailyTablets.get(i-1);
				for(int pti = 0; pti < 4; pti++){
					int ti = pti;
					if(ti > 1){
						ti += 1;
					}
					try{
						Tablet t = tList.get(pti);
						ImageView iv = (ImageView)ll.getChildAt(ti);
						if(iv != null)
							iv.setImageDrawable(t.getDrawable(getActivity()));
					}catch(IndexOutOfBoundsException ex){
						break;
					}
				}
				cell.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						getFragmentManager().beginTransaction().replace(R.id.container, new DetailDayView(dailyTablets.get(fi-1), longWeekdayString[fi])).addToBackStack( "tableView" ).commit();
					}
				});
				column.addView(cell);
			}
			return rootView;
		}
	}


	public static class InputView extends Fragment{
        public static String tabletColor, name;
        public static Calendar startTime, endTime;
		static Toast t;
        
		public static void setStartTime(Calendar startTime) {
			InputView.startTime = startTime;
		}

		public static void setEndTime(Calendar endTime) {
			InputView.endTime = endTime;
		}

		public InputView(){
		}
		
		@Override
		public View onCreateView(final LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tablet_input,container, false);
	        final LinearLayout timeSlots = (LinearLayout) rootView.findViewById(R.id.timeSlots);
	        timeSlots.addView(inflater.inflate(R.layout.time_slot,timeSlots, false));
	        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.inputRepeatButtonLayout);
            DateFormatSymbols dfs = new DateFormatSymbols();
            final String[] shortWeekdayString = dfs.getShortWeekdays();
            final String[] longWeekdayString = dfs.getWeekdays();
            final String[] shortMonthString = dfs.getShortMonths();
	        final OnClickListener repeatListener = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					LinearLayout repeatButtons = ((LinearLayout)((ViewGroup)v.getParent()).getChildAt(2));
					if(repeatButtons.getChildCount() > 0){
						repeatButtons.removeAllViews();
					}else{
				        for (int i = 0; i < 7; i++) {
				            final ViewGroup viewgroup = (ViewGroup) inflater.inflate(R.layout.togglebutton,
				            		repeatButtons, false);
				            final ToggleButton button = (ToggleButton) viewgroup.getChildAt(0);
				            button.setWidth(repeatButtons.getWidth()/7);
				            final int dayToShowIndex = DAY_ORDER[i];
				            button.setText(shortWeekdayString[dayToShowIndex]);
				            button.setTextOn(shortWeekdayString[dayToShowIndex]);
				            button.setTextOff(shortWeekdayString[dayToShowIndex]);
				            //button.setContentDescription(mLongWeekDayStrings[dayToShowIndex]);
				            repeatButtons.addView(viewgroup);
				        }
					}
				}
			};
	        final OnClickListener timePickListener = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showTimePickerDialog((TextView)v);
				}
			};
			((RelativeLayout)timeSlots.getChildAt(0)).getChildAt(1).setOnClickListener(repeatListener);
			((RelativeLayout)timeSlots.getChildAt(0)).getChildAt(0).setOnClickListener(timePickListener);
	        final TextView addSlotView = (TextView) rootView.findViewById(R.id.inputAddTimeSlot);
	        final TextView removeSlotView = (TextView) rootView.findViewById(R.id.inputRemoveTimeSlot);
	        removeSlotView.setVisibility(View.INVISIBLE);
	        addSlotView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
			        Log.w("children: ", timeSlots.getChildCount()+"");
			       final  RelativeLayout slot = (RelativeLayout) inflater.inflate(R.layout.time_slot,
		                    timeSlots, false);
			        slot.getChildAt(1).setOnClickListener(repeatListener);
			        slot.getChildAt(0).setOnClickListener(timePickListener);
			        timeSlots.addView(slot);
			        if(timeSlots.getChildCount() >= 5){
			        	addSlotView.setVisibility(View.INVISIBLE);
			        }
			        if(timeSlots.getChildCount() > 1){
			        	removeSlotView.setVisibility(View.VISIBLE);
			        }
				}
			});
	        removeSlotView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
			        Log.w("children: ", timeSlots.getChildCount()+"");
			        if(timeSlots.getChildCount() > 1){
			        	timeSlots.removeViewAt(timeSlots.getChildCount()-1);
			        }
			        if(timeSlots.getChildCount() <= 1){
			        	removeSlotView.setVisibility(View.INVISIBLE);
			        }
			        if(timeSlots.getChildCount() < 5){
			        	addSlotView.setVisibility(View.VISIBLE);
			        }
				}
			});
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			final TextView startDate = (TextView) rootView.findViewById(R.id.startDate);
			final TextView endDate = (TextView) rootView.findViewById(R.id.endDate);
			startDate.setText(day + " " + shortMonthString[month] + " " + year);
			endDate.setText((day+1) + " " + shortMonthString[month] + " " + year);
			startDate.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showDatePickerDialog(startDate);
				}
			});
			endDate.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showDatePickerDialog(endDate);
				}
			});
			String[] tabletColors = new String[]{"blue", "green", "orange", "purple", "red", "white", "yellow"};
			LinearLayout tabletChoices = (LinearLayout) rootView.findViewById(R.id.tabletChoices);
			for(final String color : tabletColors){
				ImageView iv = new ImageView(rootView.getContext());
				Resources resources = rootView.getContext().getResources();
				final int resourceId = resources.getIdentifier("tablet_"+color, "drawable", 
						rootView.getContext().getPackageName());
				Drawable d = resources.getDrawable(resourceId);
				iv.setBackgroundDrawable(d);
				iv.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						InputView.tabletColor = color;
						if(t != null)
							t.cancel();
						t = Toast.makeText(getActivity(), "Your tablet will now be: " + color, Toast.LENGTH_SHORT);
						t.show();
					}
				});
				Log.w("width", timeSlots.getWidth()+"");
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,100, 1f);
				iv.setLayoutParams(layoutParams);
				tabletChoices.addView(iv);
			}
			Button submitInput = (Button) rootView.findViewById(R.id.submitTabletInput);
			submitInput.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try{
						Log.d("where?", "1");
						//public Tablet(String name, String color, long startTime, HashMap<String, ArrayList<Integer>> repeatTimes){
						HashMap<Integer[], ArrayList<Integer>> repeatTimes = new HashMap<Integer[], ArrayList<Integer>>();
						for(int i = 0; i < timeSlots.getChildCount(); i++){
							ArrayList<Integer> days = new ArrayList<Integer>();
							TextView time = (TextView)((RelativeLayout)timeSlots.getChildAt(i)).getChildAt(0);
							String timeText = time.getText().toString();
							String[] data = timeText.split(" ");
							String[] hour_minute = data[0].split(":");
							String am_pm = data[1].equalsIgnoreCase("am") ? "AM" : "PM";
							int hour = Integer.parseInt(hour_minute[0]);
							int minute = Integer.parseInt(hour_minute[1]);
							if(am_pm.equalsIgnoreCase("pm")){
								hour +=12;
							}
							LinearLayout daysHolder = (LinearLayout) ((RelativeLayout)time.getParent()).getChildAt(2);
							if(daysHolder.getChildCount() > 0){
								for(int day = 0; day < daysHolder.getChildCount();day++){
									ToggleButton toggle = (ToggleButton) ((RelativeLayout)daysHolder.getChildAt(day)).getChildAt(0);
									if(toggle.isChecked()){
										days.add(day+1);
									}
								}
							}
							repeatTimes.put(new Integer[]{hour, minute}, days);
							name = ((TextView) getView().findViewById(R.id.inputNameEntry)).getText().toString();
							Log.w("name", name);
							if(tabletColor == null){
								tabletColor = "red";
							}
						}
						try{
							Tablet tablet = new Tablet(name, tabletColor, startTime, endTime, repeatTimes);
							tablet.save(getActivity());
							tablets.add(tablet);
							getFragmentManager().beginTransaction().replace(R.id.container, new TableView(tablets)).commit();
						}catch(Exception ex){
							Toast.makeText(getActivity(), "No Time for Error Handling ... Exception", Toast.LENGTH_SHORT).show();
							ex.printStackTrace();
						}
						Log.d("where?", "2");
					}catch(NullPointerException ex){
						Toast.makeText(getActivity(), "No Time for Error Handling ... NullPointerException ... did you enter the dates?", Toast.LENGTH_SHORT).show();
						ex.printStackTrace();
					}catch(NumberFormatException ex){
						Toast.makeText(getActivity(), "No Time for Error Handling ... NumberFormatException", Toast.LENGTH_SHORT).show();
						ex.printStackTrace();
					}catch(StringIndexOutOfBoundsException ex){
						Toast.makeText(getActivity(), "No Time for Error Handling ... StringIndexOutOfBoundsException", Toast.LENGTH_SHORT).show();
						ex.printStackTrace();
					}catch(Exception ex){
						Toast.makeText(getActivity(), "No Time for Error Handling ... General Exception", Toast.LENGTH_SHORT).show();
						ex.printStackTrace();
					}
				}
			});
			Toast.makeText(getActivity(), "PLEASE TAP ALL DATES/TIMES AND SELECT THEM!", Toast.LENGTH_SHORT).show();
			return rootView;
		}
		
		public void showTimePickerDialog(TextView v) {
		    DialogFragment newFragment = new TimePickerFragment(v);
		    newFragment.show(getFragmentManager(), "datePicker");
		}
		
		public void showDatePickerDialog(TextView v) {
		    DialogFragment newFragment = new DatePickerFragment(v);
		    newFragment.show(getFragmentManager(), "datePicker");
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
			if(v.getTag().toString().equals("startDate")){
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, month);
				calendar.set(Calendar.DAY_OF_MONTH, day);
				InputView.setStartTime(calendar);
			}else{
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, month);
				calendar.set(Calendar.DAY_OF_MONTH, day);
				InputView.setEndTime(calendar);
			}
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
		}
	}
}
