package com.unome.silenceme.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartServiceOnBoot extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent arg1) {
		context.startService(new Intent(context,CalendarChangeService.class));
	}

}
