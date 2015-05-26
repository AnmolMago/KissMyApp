package com.scribull.kissmyapp.TabletManagement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.scribull.kissmyapp.database.TabletDatabaseUtil;
import com.scribull.kissmyapp.database.TabletDatabaseUtil.TabletEntry;

public class Tablet {

	private String id, name, color, time;
	Calendar startTime, endTime;     
	PendingIntent alarmIntent;
	Context context;
	private HashMap<Integer[], ArrayList<Integer>> repeatTimes = new HashMap<Integer[], ArrayList<Integer>>();

	public Tablet(String id, String name, String color, String startTime, String endTime, String repeatTimes){
		this.id = id;
		this.name = name;
		this.color = color;
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(Long.parseLong(startTime));
		this.startTime = c;
		c.setTimeInMillis(Long.parseLong(endTime));
		this.endTime = c;
		HashMap<Integer[], ArrayList<Integer>> repeat = new HashMap<Integer[], ArrayList<Integer>>();
		String[] tablets = repeatTimes.split("\\|");
		for(String tablet:tablets){
			String[] data = tablet.split(";");
			String[] hour_min = data[0].split(":");
			if(data.length < 2)
				return;
			String[] days = data[1].split(",");
			Integer[] intarr = new Integer[]{Integer.parseInt(hour_min[0]), Integer.parseInt(hour_min[1])};
			ArrayList<Integer> daysList = new ArrayList<Integer>();
			for(String day : days)
				daysList.add(Integer.parseInt(day));
			repeat.put(intarr, daysList);
		}
		this.repeatTimes = repeat;
	}
	public Tablet(String name, String color, Calendar startTime, Calendar endTime, HashMap<Integer[], ArrayList<Integer>> repeatTimes){
		this.name = name;
		this.color = color;
		this.startTime = startTime;
		this.endTime = endTime;
		this.repeatTimes = repeatTimes;
		//Log.w("construct", name + color + startTime.toString() + endTime + repeatTimes.get(new ArrayList<Integer[]>(repeatTimes.keySet()).get(0)).size());
	}
	
	public void save(Context context){
		this.context = context; 
		Log.w("doshe?", "shedo");
		try{
			TabletDatabaseUtil TDHelper = new TabletDatabaseUtil(context);
	        SQLiteDatabase db = TDHelper.getWritableDatabase();
	        
	        ContentValues values = new ContentValues();
	        if(id != null)
	        	values.put(TabletEntry.TABLET_ENTRY_ID, id);
	        values.put(TabletEntry.TABLET_NAME, name);
	        values.put(TabletEntry.TABLET_COLOR, color);
	        values.put(TabletEntry.TABLET_START, startTime.getTimeInMillis()+"");
	        values.put(TabletEntry.TABLET_END, endTime.getTimeInMillis()+"");
	        StringBuilder sb = new StringBuilder();
	        StringBuilder sb2 = new StringBuilder();
	        for(Integer[] num : repeatTimes.keySet()){
	        	int hour = num[0];
	        	int minute = num[1];
	        	sb.append("|"+hour + ":"+minute+";");
	        	ArrayList<Integer> days = repeatTimes.get(num);
	        	for(Integer day:days){
	        		sb2.append(","+day);
	        	}
	        	sb.append(sb2.toString().replaceFirst(",", ""));
	        	sb2 = new StringBuilder();
	        }
	        Log.w("sql", sb.toString().replaceFirst("\\|", ""));
	        values.put(TabletEntry.TABLET_REPEAT_TIMES, sb.toString().replaceFirst("\\|", ""));
	        id = db.insert(TabletEntry.TABLE, "null", values)+"";
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
		setNotif(context);
	}
	
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getColor() {
		return color;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public HashMap<Integer[], ArrayList<Integer>> getRepeatTimes() {
		return repeatTimes;
	}
	
	public Drawable getDrawable(Context context){
		Resources resources = context.getResources();
		final int resourceId = resources.getIdentifier("drawable/tablet_"+color, "drawable", 
				context.getPackageName());
		return resources.getDrawable(resourceId);
	}
	
	public PendingIntent getAlarm(){
		return alarmIntent;
	}
	
	public void setNotif(Context context){
		this.context = context;
		for(Integer[] i : repeatTimes.keySet()){
			ArrayList<Integer> days = repeatTimes.get(i);
			for(int day : days){
				Intent myIntent = new Intent(getContext(), TabletManagement.class);
				AlarmManager alarmManager= (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
				alarmIntent = PendingIntent.getService(getContext(), 0, myIntent, 0);
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_WEEK, day);
				calendar.set(Calendar.HOUR_OF_DAY, i[0]);
				calendar.set(Calendar.MINUTE, i[1]);
				calendar.set(Calendar.SECOND, 00);
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000 , alarmIntent);
				break;
			}
			break;
		}
	}
	
}
