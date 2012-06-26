package com.unome.silenceme.app;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class SetBackService extends IntentService{
	String TAG="Silenceme";
	public SetBackService()
	{
		super("SilencePhoneService");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = wmbPreference.edit();
		editor.putInt("currentEventId",-1);
		Integer prevState=wmbPreference.getInt("CurrPhState",0);
		AudioManager audiomanage = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		audiomanage.setRingerMode(prevState);
		//Log.d(TAG,"audio status has been set back "+prevState.toString());
	}

}
