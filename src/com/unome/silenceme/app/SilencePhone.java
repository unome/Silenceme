package com.unome.silenceme.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
public class SilencePhone extends BroadcastReceiver{
	final String TAG="Silenceme";
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String keyword = bundle.getString("keyword");
		long endTime=bundle.getLong("endTime");
		Intent forIntent=new Intent(context, SilencePhoneService.class);
		forIntent.putExtra("keyword",keyword );
		forIntent.putExtra("endTime",endTime);
		context.startService(forIntent);
	}

}


