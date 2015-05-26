package com.scribull.kissmyapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DietDatabaseUtil extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WHOSTablets.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + DietEntry.TABLE + " (" +
        		DietEntry.DIET_ID + " INTEGER PRIMARY KEY," +
        		DietEntry.DIET_DATE + TEXT_TYPE + COMMA_SEP +
        		DietEntry.DIET_GRAINS + TEXT_TYPE + COMMA_SEP +
        		DietEntry.DIET_FV + TEXT_TYPE + COMMA_SEP +
        		DietEntry.DIET_WATER + TEXT_TYPE + COMMA_SEP +
        		DietEntry.DIET_DAIRY + TEXT_TYPE + COMMA_SEP +
        		DietEntry.DIET_MA + TEXT_TYPE + COMMA_SEP +
        		DietEntry.DIET_FO + TEXT_TYPE +" )";

    private static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + DietEntry.TABLE;

	public DietDatabaseUtil(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void delete(SQLiteDatabase db){
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
	public static abstract class DietEntry implements BaseColumns {
        public static final String TABLE = "WOHS_Diet";
        public static final String DIET_ID = "id";
        public static final String DIET_DATE = "date";
        public static final String DIET_GRAINS = "grains";
        public static final String DIET_FV = "fruits_veggies";
        public static final String DIET_WATER = "water";
        public static final String DIET_DAIRY = "dairy";
        public static final String DIET_MA = "meat_alterna";
        public static final String DIET_FO = "fats_oils";
    }
	
}
