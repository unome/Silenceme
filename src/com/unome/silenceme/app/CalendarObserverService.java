package com.unome.silenceme.app;

import java.util.ArrayList;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

//First all the alarms are cancelled,then the event table is dropped, then the calendar is rescanned for
//all the words and new alarms are set.
public class CalendarObserverService  extends IntentService{
	DBhelper dbHelper;
	final String TAG="Silenceme";
	public CalendarObserverService() {
		super("CalendarObserverService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		dbHelper = new DBhelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = wmbPreference.edit();
		editor.putInt("currentEventId", -1); //Reset everything
		Integer prevState=wmbPreference.getInt("CurrPhState",0);
		AudioManager audiomanage = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		audiomanage.setRingerMode(prevState);

		//Need to remove all the set alarms
		String delAlarms="Select "+DBhelper.calEventId+" from "+DBhelper.calTName;
		Cursor allEventid=db.rawQuery(delAlarms,null);
		if(allEventid.getCount()!=0)
		{
			for(int i=0;i<allEventid.getCount();++i)
			{
				allEventid.moveToNext();
				deleteAlarm(allEventid.getString(0));
			}
		}
		allEventid.close();
		String delTable="delete from "+ DBhelper.calTName;
		db.execSQL(delTable); //removes all the values in the table containing events.
		if(wmbPreference.getInt("allEvents",-1)==0)
		{
			String readKeywords="Select "+ DBhelper.C_KName + " from "+DBhelper.kwTName + " where "+DBhelper.C_KStatus+" = " +
					"\"True\"";
			Cursor keywords=db.rawQuery(readKeywords, null);
			ArrayList<String> kwArray=new ArrayList<String>();
			if(keywords.getCount()!=0)
			{
				for(int i=0;i<keywords.getCount();++i)
				{

					keywords.moveToNext();
					kwArray.add(keywords.getString(0));
				}
				keywords.close();
				db.close();
				for(int i=0;i<kwArray.size();++i)
				{
					Intent compileKwIntent=new Intent(this,CompileKeyWord.class);
					compileKwIntent.putExtra("keyword",kwArray.get(i));
					this.startService(compileKwIntent);
				}
			}
		}
		if(wmbPreference.getInt("allEvents",-1)==1)
		{
			db.close();
			Intent setAllIntent = new Intent(this,SetSilentAll.class);
			this.startService(setAllIntent);
		}
	}

	private void deleteAlarm(String eventId) 
	{
		Intent intent = new Intent(this,SilencePhone.class);
		PendingIntent sender = PendingIntent.getBroadcast(this,Integer.parseInt(eventId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
	}

}
