package com.scribull.kissmyapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DiabetesDatabaseUtil extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WHOSTablets.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + DiabetesEntry.TABLE + " (" +
        		DiabetesEntry.DIABETES_ID + " INTEGER PRIMARY KEY," +
        		DiabetesEntry.DIABETES_DATE + TEXT_TYPE + COMMA_SEP +
        		DiabetesEntry.DIABETES_BG + TEXT_TYPE + COMMA_SEP +
        		DiabetesEntry.DIABETES_SAI + TEXT_TYPE + COMMA_SEP +
        		DiabetesEntry.DIABETES_LAI + TEXT_TYPE +" )";

    private static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + DiabetesEntry.TABLE;

	public DiabetesDatabaseUtil(Context context){
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
    
	public static abstract class DiabetesEntry implements BaseColumns {
        public static final String TABLE = "WOHS_DIABETES";
        public static final String DIABETES_ID = "id";
        public static final String DIABETES_DATE = "date";
        public static final String DIABETES_BG = "glucose";
        public static final String DIABETES_SAI = "shortActingInsulin";
        public static final String DIABETES_LAI = "longActingInsulin";
    }
	
}
