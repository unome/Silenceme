package com.unome.silenceme.app;

import java.util.Calendar;
import java.util.Date;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.util.Log;

public class SetSilentAll extends IntentService{
	final String TAG="Silenceme";
	DBhelper dbhelper;
	public SetSilentAll()
	{
		super("SetSilentAll");
	}
	@Override
	public void onStart(Intent intent, int startId) 
	{
		super.onStart(intent, startId);
	}
	@Override
	protected void onHandleIntent(Intent inIntent) 
	{
		dbhelper = new DBhelper(this);
		ContentResolver cr = getContentResolver();
		Calendar beginTime = Calendar.getInstance();
		long startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.set(2090, 10, 24, 8, 0);
		long endMillis = endTime.getTimeInMillis();
		Uri.Builder builder = Events.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, startMillis);
		ContentUris.appendId(builder, endMillis);
		String[] INSTANCE_PROJECTION = new String[] { // SEt all the columns you
				// need to extract
				Events._ID, // 0
				Events.TITLE, // 1
				Events.DTSTART, // 2
				Events.DTEND,// 3
				Events.CALENDAR_ID
		};
		Cursor eventCursor = cr.query(builder.build(), INSTANCE_PROJECTION,
				null, null, null);
		if (eventCursor.getCount()!=0) {
			while (eventCursor.moveToNext()) { // iterate on all calendar
				// records
				String event_id = eventCursor.getString(0);
				Date begin = new Date(eventCursor.getLong(2));
				Date end = new Date(eventCursor.getLong(3));
				String calId=eventCursor.getString(4);
				addToEventTable(event_id,"**null**");
				Intent intent = new Intent(this, SilencePhone.class);
				intent.putExtra("keyword","**null**");
				intent.putExtra("endTime",end.getTime());
				// In reality, you would want to have a static variable for the request code instead of 192837
				PendingIntent sender = PendingIntent.getBroadcast(this,Integer.parseInt(event_id), intent, PendingIntent.FLAG_UPDATE_CURRENT);
				// Get the AlarmManager service
				AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, begin.getTime(), sender);

			}
		}
		//Log.d(TAG,"All events have been to set to silent");
	}
	private void addToEventTable(String event_id,String keyWord) 
	{
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		String sqlQuery = "Insert or Replace into "+DBhelper.calTName + " (\""+DBhelper.calEventId+"\""+",\""
				+DBhelper.C_KName+"\")"+ " values (\""+event_id+"\",\""+keyWord+"\")";
		db.execSQL(sqlQuery);
		db.close();
	}



}
