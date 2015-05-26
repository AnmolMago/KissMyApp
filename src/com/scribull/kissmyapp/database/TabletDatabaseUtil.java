package com.scribull.kissmyapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class TabletDatabaseUtil extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WHOSTablets.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + TabletEntry.TABLE + " (" +
        		TabletEntry.TABLET_ENTRY_ID + " INTEGER PRIMARY KEY," +
        		TabletEntry.TABLET_NAME + TEXT_TYPE + COMMA_SEP +
        		TabletEntry.TABLET_COLOR + TEXT_TYPE + COMMA_SEP +
        		TabletEntry.TABLET_START + TEXT_TYPE + COMMA_SEP +
        		TabletEntry.TABLET_END + TEXT_TYPE + COMMA_SEP +
        		TabletEntry.TABLET_REPEAT_TIMES + TEXT_TYPE +" )";

    private static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + TabletEntry.TABLE;

	public TabletDatabaseUtil(Context context){
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
    
	public static abstract class TabletEntry implements BaseColumns {
        public static final String TABLE = "WOHS_Tablets";
        public static final String TABLET_ENTRY_ID = "id";
        public static final String TABLET_NAME = "title";
        public static final String TABLET_COLOR = "color";
        public static final String TABLET_START = "time_start";
        public static final String TABLET_END = "time_end";
        public static final String TABLET_REPEAT_TIMES = "repeat_times";
    }
	
}
