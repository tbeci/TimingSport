package hu.reloc.timingsport;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class MyDatabase extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "mydatabase.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME_INFO = "info";
	public static final String COL_INFO_ID = "id";
	public static final String COL_INFO_NAME = "nev";
	public static final String COL_INFO_LAT = "lat";
	public static final String COL_INFO_LON = "lon";
	public static final String COL_INFO_IDO = "ido";
	public static final String COL_INFO_ERZEK = "erzek";
	public static final String COL_INFO_SLAT = "slat";
	public static final String COL_INFO_SLON = "slon";
	public static final String COL_INFO_DIS = "did";
	public static final String CRATE_INFO = "create table " + TABLE_NAME_INFO
			+ " (" + COL_INFO_ID + " text," + COL_INFO_NAME + " text,"
			+ COL_INFO_LAT + " text," + COL_INFO_LON + " text," + COL_INFO_IDO
			+ " text," + COL_INFO_ERZEK + " text," + COL_INFO_SLAT + " text,"  + COL_INFO_SLON + " text,"
			+ COL_INFO_DIS + " text)";


	public static final String TABLE_NAME_LOGIN = "login";
	public static final String COL_LOGIN_ID = "id";
	public static final String COL_LOGIN_VNAME = "vname";
	public static final String COL_LOGIN_KNAME = "kname";
	public static final String COL_LOGIN_YOB = "yob";
	public static final String COL_LOGIN_PID = "pid";
	public static final String COL_LOGIN_BIB = "bib";
	public static final String COL_LOGIN_GENDER = "gender";
	public static final String COL_LOGIN_STATUS = "status";
	public static final String COL_LOGIN_AUTO = "auto";
	public static final String CRATE_LOGIN = "create table " + TABLE_NAME_LOGIN
			+ " (" + COL_LOGIN_ID + " text,"+ COL_LOGIN_VNAME + " text," + COL_LOGIN_KNAME + " text,"
			+ COL_LOGIN_YOB + " text,"+ COL_LOGIN_PID + " text,"+ COL_LOGIN_BIB + " text,"
			+ COL_LOGIN_GENDER + " text,"+ COL_LOGIN_STATUS + " text,"+ COL_LOGIN_AUTO + " text)";


	public static final String TABLE_NAME_TIMER = "timer";
	public static final String COL_TIMER_ID = "ido";
	public static final String COL_TIMER_PID = "pid";
	public static final String COL_TIMER_BIB = "bib";
	public static final String COL_TIMER_LAT = "lat";
	public static final String COL_TIMER_LON = "lon";
    public static final String COL_TIMER_CIM = "cim";
    public static final String COL_TIMER_HEAD = "head";
    public static final String COL_TIMER_SPEED = "speed";
	public static final String COL_TIMER_MEGTETT = "megtett";
	public static final String COL_TIMER_ALT = "alt";
	public static final String CRATE_TIMER = "create table " + TABLE_NAME_TIMER
			+ " (" + COL_TIMER_ID + " text,"+ COL_TIMER_PID + " text,"+ COL_TIMER_BIB + " text,"
			+ COL_TIMER_CIM + " text,"+ COL_TIMER_LAT + " text," + COL_TIMER_LON + " text,"
            + COL_TIMER_HEAD + " text,"+ COL_TIMER_SPEED + " text,"+ COL_TIMER_MEGTETT + " text,"
			+ COL_TIMER_ALT + " text)";



	public static final String TABLE_NAME_VERSENY = "verseny";
	public static final String COL_VERSENY_ID = "id";
	public static final String COL_VERSENY_BIB = "bib";
	public static final String COL_VERSENY_VER = "nev";
	public static final String COL_VERSENY_TAV = "tav";
	public static final String COL_VERSENY_DAT = "dat";
	public static final String COL_VERSENY_URL = "url";
	public static final String CRATE_VERSENY = "create table " + TABLE_NAME_VERSENY
			+ " ("+ COL_VERSENY_ID + " text," + COL_VERSENY_BIB + " text," + COL_VERSENY_VER + " text,"
			+ COL_VERSENY_TAV + " text," + COL_VERSENY_DAT + " text," + COL_VERSENY_URL + " text)";


	public MyDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<HashMap<String, String>> getAllBoja() {
		ArrayList<HashMap<String, String>> wordList;
		wordList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM info";
	    SQLiteDatabase database = this.getWritableDatabase();
	    Cursor cursor = database.rawQuery(selectQuery, null);
	    if (cursor.moveToFirst()) {
	        do {
	        	HashMap<String, String> map = new HashMap<String, String>();
				map.put("MemberID", cursor.getString(0));
				map.put("Name", cursor.getString(1));
				map.put("Lat", cursor.getString(2));
				map.put("Lon", cursor.getString(3));
				map.put("Ido", cursor.getString(4));
                wordList.add(map);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return wordList;
	}


    public ArrayList<HashMap<String, String>> getAllBoja2() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM timer";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ido", cursor.getString(0));
				map.put("pid", cursor.getString(1));
				map.put("bib", cursor.getString(2));
				map.put("cim", cursor.getString(3));
                map.put("lat", cursor.getString(4));
                map.put("lon", cursor.getString(5));
                map.put("head", cursor.getString(6));
                map.put("speed", cursor.getString(7));

                wordList.add(map);
            } while (cursor.moveToNext());
        }

        // return contact list
        return wordList;
    }


    @Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CRATE_INFO);
        db.execSQL(CRATE_TIMER);
		db.execSQL(CRATE_LOGIN);
		db.execSQL(CRATE_VERSENY);
		db.execSQL("INSERT INTO login VALUES('1','','','','','','','0','0');");
		db.execSQL("INSERT INTO verseny VALUES('1','','','','','');");
	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists " + TABLE_NAME_INFO);
	}

}
