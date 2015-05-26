package com.scribull.kissmyapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.scribull.kissmyapp.database.DietDatabaseUtil;
import com.scribull.kissmyapp.database.DietDatabaseUtil.DietEntry;

public class Diet extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diet);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new DietHome()).commit();
		}
        getActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFCC33));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.diet, menu);
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

	public static class DietHome extends Fragment {

		//public static String[] categories = new String[]{"dairy", "fruits_and_vegtables", "grains", "meat_and_alternatives", "fats_and_oils"};
		public static Category[] categories = new Category[6];
		public DietLog currentLog;
		public ArrayList<DietLog> oldLogs = new ArrayList<DietLog>(); 
		
		public DietHome() {
			categories[0] = new Category("grains", 0, 8);
			categories[1] = new Category("fruits_and_vegetables", 0, 8);
			categories[2] = new Category("water", 0, 8);
			categories[3] = new Category("dairy", 0, 2);
			categories[4] = new Category("meat_and_alternatives", 0, 2);
			categories[5] = new Category("fats_and_oils", 0, 2);
			currentLog = new DietLog(categories);
		}

		public static class DietLog{
			String date;
			Category[] categories;
			public DietLog(String date, Category[] categories){
				this.date = date;
				this.categories = categories;
			}
			public DietLog(Category[] categories){
				Date dateO = new Date(System.currentTimeMillis());
				date = dateO.getMonth() + "-"+dateO.getDate()+"-"+dateO.getYear();
				this.categories=categories;
			}

			public void Save(Context context){

				DietDatabaseUtil DDU = new DietDatabaseUtil(context);
		        SQLiteDatabase db = DDU.getWritableDatabase();
		        
		        ContentValues values = new ContentValues();
		        values.put(DietEntry.DIET_DATE, date);
		        values.put(DietEntry.DIET_GRAINS, categories[0].getNow());
		        values.put(DietEntry.DIET_FV, categories[1].getNow());
		        values.put(DietEntry.DIET_WATER, categories[2].getNow());
		        values.put(DietEntry.DIET_DAIRY, categories[3].getNow());
		        values.put(DietEntry.DIET_MA, categories[4].getNow());
		        values.put(DietEntry.DIET_FO, categories[5].getNow());
		        db.insert(DietEntry.TABLE, "null", values);
			}
		}
		
		public static class Category{
			String name;
			int now, max;
			public Category(String date, String name, int now, int max){
				this.name = name;
				this.now = now;
				this.max = max;
			}
			public Category(String name, int now, int max){
				this.name = name;
				this.now = now;
				this.max = max;
			}
			public String getName() {
				return name;
			}
			public int getNow() {
				return now;
			}
			public int getMax() {
				return max;
			}
			public Drawable getDrawable(Context context){
				Resources resources = context.getResources();
				final int resourceId = resources.getIdentifier("drawable/"+getName()+"_0"+getNow(), "drawable", 
						context.getPackageName());
				return resources.getDrawable(resourceId);
			}
			public void raiseCount(TextView tv, ImageView iv){
				if(now == max)
					return;
				now++;
				iv.setImageDrawable(getDrawable(iv.getContext()));
				tv.setText(getNow() + "/" + getMax());
				
				//TODO update database
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			TableLayout rootView = (TableLayout)inflater.inflate(R.layout.fragment_diet_home,
					container, false);
			TableRow tr1 = new TableRow(getActivity());
			TableRow tr2 = new TableRow(getActivity());
			tr1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
			tr2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
			int i =0;
			for(final Category category : categories){
				Log.w("category", category.getName());
				LinearLayout dietM = (LinearLayout) inflater.inflate(R.layout.diet_module, container, false);
				android.widget.TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
				lp.setMargins(25, 100, 0, 0);
				dietM.setLayoutParams(lp);
				ImageView iv = (ImageView) dietM.findViewById(R.id.dietImage);
				iv.setLayoutParams(new TableRow.LayoutParams(300, 300));
				TextView nameV = (TextView) dietM.findViewById(R.id.categoryName);
				nameV.setText(category.getName().replace("_", " ").replace("and", "&").toUpperCase(Locale.CANADA));
				nameV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
				TextView amountV = (TextView) dietM.findViewById(R.id.amountOfTotal);
				amountV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
				iv.setImageDrawable(category.getDrawable(getActivity()));
				amountV.setText(category.getNow() + "/" + category.getMax());
				dietM.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ImageView iv = (ImageView) ((LinearLayout)v).getChildAt(0);
						TextView tv = (TextView) ((LinearLayout)v).getChildAt(2);
						category.raiseCount(tv, iv);
					}
				});
				if(i<3){
					tr1.addView(dietM);//, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
				}else{
					tr2.addView(dietM);//, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
				}
				i++;
			}
			Log.w("safs", tr1.getChildCount()+"");
			Log.w("safs", tr2.getChildCount()+"");
			rootView.addView(tr1, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
			rootView.addView(tr2, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
			return rootView;
		}
	}
}
