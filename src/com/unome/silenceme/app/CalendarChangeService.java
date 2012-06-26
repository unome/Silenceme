/* This class is to make sure the service starts on the rebooting of the phone */

package com.unome.silenceme.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.CalendarContract.Instances;

public class CalendarChangeService extends Service{

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.getContentResolver().registerContentObserver(Instances.CONTENT_URI,true,new CalendarObserver(this));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		this.getContentResolver().registerContentObserver(Instances.CONTENT_URI,true,new CalendarObserver(this));

	}

}
