package com.unome.silenceme.app;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;


public class SilencePhoneService extends IntentService{
	DBhelper dbHelper;
	String TAG="Silenceme";
	public SilencePhoneService()
	{
		super("SilencePhoneService");
		dbHelper = new DBhelper(this);
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) 
	{
		Bundle bundle = intent.getExtras();
		String keyword = bundle.getString("keyword");
		long endTime=bundle.getLong("endTime");
		SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		if(wmbPreference.getInt("allEvents",-1)==0)
		{
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String sqlQuery= "Select "+ dbHelper.C_KStatus +" from "+dbHelper.kwTName+" where "+dbHelper.C_KName+"=\""+keyword+"\"";
			Cursor sqlIt = db.rawQuery(sqlQuery, null);
			if(sqlIt.getCount()!=0)
			{
				sqlIt.moveToNext();
				if(sqlIt.getString(0).equals("True"))
				{
					setSilent(wmbPreference,endTime);

				}
			}
			db.close();
		}
		if(wmbPreference.getInt("allEvents",-1)==1)
		{
			setSilent(wmbPreference,endTime);
		}
	}
	private void setSilent(SharedPreferences wmbPreference,long endTime)
	{
		AudioManager audiomanage = (AudioManager)this.getSystemService(AUDIO_SERVICE);
		if(wmbPreference.getInt("currentEventId",-1)==-1) //implies an event is not running currently
		{
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putInt("currentEventId",1); //set currenteventId to currentEventId
			editor.putInt("CurrPhState",audiomanage.getRingerMode());
			//Log.d(TAG,"Phone status has been saved");
			editor.commit(); 		//To maintains the current Status
		}
		int phoneStatus = wmbPreference.getInt("desiredPhState", -1);
		audiomanage.setRingerMode(phoneStatus);
		//Log.d(TAG,"Phone has been set to silent");
		Intent backintent = new Intent(this,SetBack.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 192837, backintent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am1 = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		am1.set(AlarmManager.RTC_WAKEUP, endTime, sender);
	}
}



