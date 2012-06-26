package com.unome.silenceme.app;

// Write to database all the calendar events containing the keyword.
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

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
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.util.Log;

public class CompileKeyWord extends IntentService
{
	final String TAG="Silenceme";
	DBhelper dbhelper;
	public CompileKeyWord()
	{
		super("CompileKeyword");
	}

	@Override
	public void onStart(Intent intent, int startId) 
	{
		super.onStart(intent, startId);
	}
	@Override
	protected void onHandleIntent(Intent inIntent) {
		dbhelper = new DBhelper(this);
		Bundle bundle = inIntent.getExtras();
		String keyword = bundle.getString("keyword");
		ContentResolver cr = getContentResolver();
		Calendar beginTime = Calendar.getInstance();
		long startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.set(2090, 10, 24, 8, 0);
		long endMillis = endTime.getTimeInMillis();
		Uri.Builder builder = Events.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, startMillis);
		ContentUris.appendId(builder, endMillis);
		String[] INSTANCE_PROJECTION = new String[] { // Set all the columns you
				// need to extract
				Events._ID, // 0
				Events.TITLE, // 1
				Events.DTSTART, // 2
				Events.DTEND,// 3
				Events.CALENDAR_ID
		};
		Cursor eventCursor = cr.query(builder.build(), INSTANCE_PROJECTION,
				null, null,null);
		if (eventCursor.getCount()!=0) {
			while (eventCursor.moveToNext()) { // iterate on all calendar
				// records
				String event_id = eventCursor.getString(0);
				String title = eventCursor.getString(1);
				Date begin = new Date(eventCursor.getLong(2));
				Date end = new Date(eventCursor.getLong(3));
				String calId = eventCursor.getString(4);
				StringTokenizer st = new StringTokenizer(title.toLowerCase());
				boolean occured=false;
				while(st.hasMoreTokens()&&!occured)
				{
					if(st.nextToken().equals(keyword.toLowerCase()))
					{
						occured = true;
					}
				}
				if (occured)
				{
					addToEventTable(event_id,keyword);
					Intent intent = new Intent(this, SilencePhone.class);
					intent.putExtra("keyword",keyword );
					intent.putExtra("endTime",end.getTime());
					PendingIntent sender = PendingIntent.getBroadcast(this,Integer.parseInt(event_id), intent, PendingIntent.FLAG_UPDATE_CURRENT);
					// Get the AlarmManager service
					AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					am.set(AlarmManager.RTC_WAKEUP, begin.getTime(), sender);
				}

			}
		}
		//Log.d(TAG,"Events with keywords have been compiled");
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
