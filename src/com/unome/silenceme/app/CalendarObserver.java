package com.unome.silenceme.app;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

public class CalendarObserver extends ContentObserver{
	@Override
	public boolean deliverSelfNotifications() {
		return super.deliverSelfNotifications();
	}
	final String TAG="Silenceme";
	Context context;
	public CalendarObserver()
	{
		super(null);
	}
	public CalendarObserver(Handler handler) {
		super(handler);
	}
	public CalendarObserver(Context inContext)
	{
		super(null);
		context=inContext;
	}
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Intent serviceIntent=new Intent(context,CalendarObserverService.class);
        context.startService(serviceIntent);
    }


}
