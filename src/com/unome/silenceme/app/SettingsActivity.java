package com.unome.silenceme.app;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class SettingsActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.settingsmenu);
		final RadioButton rBVibrate=(RadioButton)findViewById(R.id.rButtonVibrate);
		final RadioButton rBSilent=(RadioButton)findViewById(R.id.rButtonSilent);
		if(wmbPreference.getInt("desiredPhState", -1)==0)
		{
			rBSilent.setChecked(true);
			rBVibrate.setChecked(false);
		}
		else{
			rBVibrate.setChecked(true);
			rBSilent.setChecked(false);

		}

		rBSilent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rBVibrate.setChecked(false);
				if(wmbPreference.getInt("desiredPhState",-1)==1)
				{
					SharedPreferences.Editor editor = wmbPreference.edit();
					editor.putInt("desiredPhState",0);	
					editor.commit();
					Toast.makeText(v.getContext(), "Phone will be silent during selected events",
							Toast.LENGTH_LONG).show();
			        Intent changeIntent=new Intent(v.getContext(),CalendarObserverService.class);
			        v.getContext().startService(changeIntent);
				}
			}
		});

		rBVibrate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rBSilent.setChecked(false);	
				if(wmbPreference.getInt("desiredPhState",-1)==0)
				{
					SharedPreferences.Editor editor = wmbPreference.edit();
					editor.putInt("desiredPhState",1);
					editor.commit();
					Toast.makeText(v.getContext(), "Phone will vibrate during selected events",
							Toast.LENGTH_LONG).show();
			        Intent changeIntent=new Intent(v.getContext(),CalendarObserverService.class);
			        v.getContext().startService(changeIntent);
				}

			}
		});
}
	public void onClickHandler(View view)
	{
		if(view.getId()==R.id.buttonWriteReview)
		{
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.unome.silenceme.app")));

		}
	}
}
