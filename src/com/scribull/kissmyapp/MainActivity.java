package com.scribull.kissmyapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.scribull.kissmyapp.Directions.Directions;
import com.scribull.kissmyapp.TabletManagement.TabletManagement;
import com.scribull.kissmyapp.Workshops.Workshops;
import com.scribull.kissmyapp.diabetes.Diabetes;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        getActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFCC33));
        //tablet(null);
	}

    public void directions(View view){
    	Intent intent = new Intent(this, Directions.class);
    	startActivity(intent);
    }

    public void workshops(View view){
    	Intent intent = new Intent(this, Workshops.class);
    	startActivity(intent);
    }

    public void tablet(View view){
    	Intent intent = new Intent(this, TabletManagement.class);
    	startActivity(intent);
    }

    public void diabetes(View view){
    	Intent intent = new Intent(this, Diabetes.class);
    	startActivity(intent);
    }

    public void diet(View view){
    	Intent intent = new Intent(this, Diet.class);
    	startActivity(intent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
}
