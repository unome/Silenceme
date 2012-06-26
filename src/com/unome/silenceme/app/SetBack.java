package com.unome.silenceme.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SetBack extends BroadcastReceiver{
	final String TAG="Silenceme";
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intentSer=new Intent(context, SetBackService.class);
		context.startService(intentSer);
	}	
}
