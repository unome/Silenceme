package com.unome.silenceme.app;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class SilenceMeActivity extends Activity {
	/** Called when the activity is first created. **/
	DBhelper dbhelper;
	String TAG = "Silenceme";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final RadioButton rBSilenceAll=(RadioButton)findViewById(R.id.rB_SilenceAll);
		final RadioButton rBNoSilenceAll=(RadioButton)findViewById(R.id.rB_NoSilenceAll);
		dbhelper = new DBhelper(this);
		final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		boolean oldVersionExists = wmbPreference.getBoolean("oldVersionExists", false);
		boolean v4FirstRun=wmbPreference.getBoolean("v4FirstRun", true);
		if(v4FirstRun)
		{
			SharedPreferences.Editor editor = wmbPreference.edit();
			if (!oldVersionExists) //if no old version exists put new values.
			{
				//start a service.
				editor.putBoolean("v4FirstRun",false);
				editor.putInt("CurrPhState",-1);
				editor.putInt("allEvents",0);//To maintain the current Status
				editor.putInt("currentEventId", -1);
				editor.putInt("desiredPhState", 1);
				editor.putInt("monitorCal", 0);
				editor.putInt("monitorBusy",0);
				editor.putInt("reviewDone", 0);
				editor.putBoolean("oldVersionExists",true);
				editor.commit();
				final Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.helpfile);
				TextView helpText = (TextView)dialog.findViewById(R.id.textViewHelp);
				helpText.setText(Html.fromHtml(getString(R.string.introText)));
				dialog.setTitle("Introduction");
				dialog.show();
			}
			else //if old version exists do nothing in v3
			{
				editor.putBoolean("oldVersionExists",true);
				editor.putBoolean("v4FirstRun",false);
				editor.commit();
			}
			this.startService(new Intent(this,CalendarChangeService.class));
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.helpfile);
			TextView helpText = (TextView)dialog.findViewById(R.id.textViewHelp);
			helpText.setText(Html.fromHtml(getString(R.string.introText)));
			dialog.setTitle("Introduction");
			dialog.show();
		}
		if(wmbPreference.getInt("allEvents", -1)==0)
			rBNoSilenceAll.setChecked(true);
		else
			rBSilenceAll.setChecked(true);
		rBSilenceAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rBNoSilenceAll.setChecked(false);
				if(wmbPreference.getInt("allEvents",-1)==0)
				{
					SharedPreferences.Editor editor = wmbPreference.edit();
					editor.putInt("allEvents",1);	
					editor.commit();
					Toast.makeText(v.getContext(), "All events will be set to silent",
							Toast.LENGTH_LONG).show();
					Intent setAllIntent=new Intent(v.getContext(),CalendarObserverService.class);
					v.getContext().startService(setAllIntent);
				}
			}
		});

		rBNoSilenceAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rBSilenceAll.setChecked(false);	
				if(wmbPreference.getInt("allEvents",-1)==1)
				{
					SharedPreferences.Editor editor = wmbPreference.edit();
					editor.putInt("allEvents",0);
					editor.commit();
					Toast.makeText(v.getContext(), "Events with keywords will be set to silent",
							Toast.LENGTH_LONG).show();
					Intent unSetAllIntent=new Intent(v.getContext(),CalendarObserverService.class);
					v.getContext().startService(unSetAllIntent);
				}

			}
		});

	}

	public void onClickHandler(View view) {
		if (view.getId() == R.id.enterKeyword) {
			EditText kName = (EditText) findViewById(R.id.keywordText);
			if (kName.getText().toString().equals("")) {
				Toast.makeText(this, "Please Enter a Key word",
						Toast.LENGTH_LONG).show();
				return;
			}
			SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
			if(dbhelper.add(this,kName.getText())==1&&wmbPreference.getInt("allEvents",-1)==0)
			{
				String keyword = kName.getText().toString();
				Intent compileKwIntent=new Intent(this,CompileKeyWord.class);
				compileKwIntent.putExtra("keyword", keyword);
				this.startService(compileKwIntent);
			}
			kName.setText("");
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(kName.getWindowToken(), 0); // This part of code is to hide the
			// keyboard;
		}
		if (view.getId() == R.id.displayKeywords) {
			Intent showKeywords = new Intent(SilenceMeActivity.this,
					DisplayKeywords.class);
			startActivityForResult(showKeywords, 0);
		}
		if (view.getId()==R.id.settingsButton){
			Intent settings = new Intent(this,SettingsActivity.class);
			startActivityForResult(settings,0);
		}
		if(view.getId()==R.id.helpDialog){
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.helpfile);
			dialog.setTitle("Help");
			TextView helpText = (TextView)dialog.findViewById(R.id.textViewHelp);
			helpText.setText(Html.fromHtml(getString(R.string.helpText)));
			dialog.show();
		}

	}


}