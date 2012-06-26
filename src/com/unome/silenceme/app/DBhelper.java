package com.unome.silenceme.app;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper; 
import android.provider.BaseColumns;
import android.text.Editable;
import android.util.Log;
import android.widget.Toast;

public class DBhelper extends SQLiteOpenHelper{
	public static final String TAG="Silenceme";
	public static final String DB_NAME = "Silenceme.db";
	public static final int DB_VERSION = 1;
	static final String kwTName="keywords";
	public static final String C_KName="keyword";
	public static final String C_KStatus="status";
	public static final String C_id=BaseColumns._ID;
	public static final String calTName="calendarEventsTable";
	public static final String calEventId="eventId";
	public static final String allCalendarsTable = "allCalTable";
	public static final String calName = "CalendarName";
	Context context;
	public DBhelper(Context context) { //
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context; 
	}




	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + kwTName + " (" + C_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ C_KName + " TEXT NOT NULL, " + C_KStatus + " TEXT NOT NULL)"; 	// Database creation sql statement
		db.execSQL(sql); //Actual sql command to create the table in sqlite
		sql="CREATE TABLE "+calTName+"("+calEventId+" INTEGER PRIMARY KEY, "+C_KName+ " TEXT NOT NULL)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Typically do ALTER TABLE statements, but...we're just in development, // so:
		db.execSQL("drop table if exists " + kwTName); // drops the old database 
		Log.d(TAG, "onUpdated");
		onCreate(db); // run onCreate to get new database
	}
	public int add(Context context,Editable kName){

		SQLiteDatabase db = this.getWritableDatabase();
		//Accessing a dataBase
		Cursor cur=null;
		String noSpacesKw=kName.toString().trim().toLowerCase();
		int whiteSpace = noSpacesKw.indexOf(" ");
		if(whiteSpace>0)
		{
			Toast.makeText(context, "Make sure there are no spaces in the keyword",
					Toast.LENGTH_LONG).show();
			return 0;
		}
		String unqStmt="Select * from "+ kwTName+" where lower("+C_KName+")=lower(\""
				+noSpacesKw+"\")";

		cur=db.rawQuery(unqStmt, null); //check for duplicates in table
		if(cur.getCount()==0)
		{
			ContentValues values=new ContentValues();
			values.clear();
			//values.put(C_id,id++);
			values.put(C_KName, noSpacesKw);
			values.put(C_KStatus, "True");
			db.insertOrThrow(kwTName,null, values);
			db.close();
			//Log.d(TAG,"New record written");
			Toast.makeText(
					context,kName.toString()+" has been added to the list of keywords",
					Toast.LENGTH_LONG).show();
			return 1;
		}
		else
		{
			db.close();
			Toast.makeText(context, "Keyword already exists",
					Toast.LENGTH_LONG).show();
			return 0;
		}
	}


}
