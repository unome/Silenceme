package com.unome.silenceme.app;


import java.util.ArrayList;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Calendars;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity{
	static boolean settingsChangesMade =false;
	ArrayList<Integer> checked = new ArrayList<Integer>();
	ArrayList<Integer> calIds = new ArrayList<Integer>();
	DBhelper dbhelper;
	final String TAG="Silenceme";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		dbhelper = new DBhelper(this);
		super.onCreate(savedInstanceState);
		final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.settingsmenu);
		final RadioButton rBVibrate=(RadioButton)findViewById(R.id.rButtonVibrate);
		final RadioButton rBSilent=(RadioButton)findViewById(R.id.rButtonSilent);
		final RadioButton rbAllCal=(RadioButton)findViewById(R.id.rB_allCal);
		final RadioButton rbSelectedCal=(RadioButton)findViewById(R.id.rB_selectedCal);
		final RadioButton rbBusyYes=(RadioButton)findViewById(R.id.rbBusyYes);
		final RadioButton rbBusyNo=(RadioButton)findViewById(R.id.rbBusyNo);	
		final Button reviewButton = (Button)findViewById(R.id.buttonWriteReview);
		if(wmbPreference.getInt("reviewWritten", -1)==1)
			reviewButton.setVisibility(View.INVISIBLE);
		if(wmbPreference.getInt("desiredPhState", -1)==0)
		{
			rBSilent.setChecked(true);
			rBVibrate.setChecked(false);
		}
		else{
			rBVibrate.setChecked(true);
			rBSilent.setChecked(false);

		}
		if(wmbPreference.getInt("monitorCal", -1)==0)
		{
			rbAllCal.setChecked(true);
			rbSelectedCal.setChecked(false);
		}
		else{
			rbAllCal.setChecked(false);
			rbSelectedCal.setChecked(true);
			displayCalendars();

		}
		if(wmbPreference.getInt("monitorBusy", -1)==0)
		{
			rbBusyNo.setChecked(true);
			rbBusyYes.setChecked(false);
		}
		else{
			rbBusyNo.setChecked(false);
			rbBusyYes.setChecked(true);
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
					settingsChangesMade=true;

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
					settingsChangesMade=true;
				}

			}
		});
		rbAllCal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rbSelectedCal.setChecked(false);
				TableLayout accountTable = (TableLayout)findViewById(R.id.tL_accTable);
				accountTable.removeAllViews();
				if(wmbPreference.getInt("monitorCal",-1)==1)
				{
					SharedPreferences.Editor editor = wmbPreference.edit();
					editor.putInt("monitorCal",0);
					editor.commit();
					//Toast.makeText(v.getContext(), "All the Calendars will be monitored",
						//	Toast.LENGTH_LONG).show();
					SQLiteDatabase db = dbhelper.getReadableDatabase();
					/* delete all the calids in the allCalTable */
					String deleteQuery = "delete from "+DBhelper.allCalendarsTable;
					db.execSQL(deleteQuery);
					db.close();
					checked.clear();
					settingsChangesMade = true;
				}

			}
		});
		rbSelectedCal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rbAllCal.setChecked(false);	
				if(wmbPreference.getInt("monitorCal",-1)==0)
				{
					SharedPreferences.Editor editor = wmbPreference.edit();
					editor.putInt("monitorCal",1);
					editor.commit();
					settingsChangesMade=true;
					//Toast.makeText(v.getContext(), "Selected calendars will be monitored",
					//		Toast.LENGTH_LONG).show();
					displayCalendars();
				}

			}
		});
		rbBusyYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rbBusyNo.setChecked(false);
				if(wmbPreference.getInt("monitorBusy", -1)==0)
				{
					SharedPreferences.Editor editor = wmbPreference.edit();
					editor.putInt("monitorBusy",1);
					editor.commit();
					settingsChangesMade=true;
				}
			}
		});
		rbBusyNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rbBusyYes.setChecked(false);
				if(wmbPreference.getInt("monitorBusy", -1)==1)
				{
					SharedPreferences.Editor editor = wmbPreference.edit();
					editor.putInt("monitorBusy",0);
					editor.commit();
					settingsChangesMade=true;
				}
			}
		});
	}


	private void displayCalendars() 
	{
		//Read the database containing calids.
		TableLayout accountTable = (TableLayout)findViewById(R.id.tL_accTable);
		accountTable.removeAllViews();
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String SqlQuery = "Select * from "+DBhelper.allCalendarsTable;
		Cursor allCalIt = db.rawQuery(SqlQuery, null);
		while(allCalIt.moveToNext())
		{
			calIds.add(allCalIt.getInt(1));
		}
		ContentResolver cr = getContentResolver();
		String[] INSTANCE_PROJECTION = new String[] { // Set all the columns you
				// need to extract
				Calendars._ID, // 0
				Calendars.NAME // 1
		};
		Cursor calCursor = cr.query(Calendars.CONTENT_URI, INSTANCE_PROJECTION,
				null, null,null);
		while(calCursor.moveToNext())
		{
			TableRow tbrow = new TableRow(this);
			String calName=calCursor.getString(1);
			Integer calId=calCursor.getInt(0);
			CheckBox cBox = new CheckBox(this);
			cBox.setTag("cB" + calName);
			if((calIds.size()!=0)&&calIds.contains(calId))
			{
				cBox.setChecked(true);
				checked.add(calId);
			}
			else
				cBox.setChecked(false);
			cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton v,boolean isChecked) {
					if(isChecked){
						TableRow currRow = (TableRow) v.getParent(); // getParent returns the parent
						ArrayList<View> allViews = new ArrayList<View>();
						allViews = currRow.getTouchables();
						TextView tName = (TextView) (allViews.get(1));
						//(TAG,"tname.gettag is :"+tName.getTag());
						checked.add(Integer.parseInt((String) tName.getTag()));
					}
					if(!isChecked)
					{
						TableRow currRow = (TableRow) v.getParent(); // getParent returns the parent
						ArrayList<View> allViews = new ArrayList<View>();
						allViews = currRow.getTouchables();
						TextView tName = (TextView) (allViews.get(1));
						//Log.d(TAG,"tname.gettext is :"+tName.getTag());
						checked.remove(checked.indexOf(Integer.parseInt((String) tName.getTag())));					}
				}
			});
			tbrow.addView(cBox);
			TextView calNameView = new TextView(this);
			calNameView.setText(calName);
			calNameView.setPadding(0, 5, 0, 5);
			calNameView.setClickable(true);
			calNameView.setTag(calId.toString());
			tbrow.addView(calNameView);
			accountTable.addView(tbrow);
		}
		calCursor.close();
		db.close();
	}
	@Override
	public void onBackPressed() {
		//Delete the calendar database and rewrite it
		//Compile the keywords again
		if(checked.size()!=0)
		{
			SQLiteDatabase db = dbhelper.getReadableDatabase();
			String deleteQuery = "delete from "+DBhelper.allCalendarsTable;
			db.execSQL(deleteQuery);
			//Log.d(TAG,"In back pressed");
			for(int i=0;i<checked.size();++i)
			{
				String sqlQuery = "Insert or Replace into "+DBhelper.allCalendarsTable+"(\""+DBhelper.calId+"\")"+
						" values (\""+checked.get(i)+"\")";
				//Log.d(TAG,sqlQuery);
				db.execSQL(sqlQuery);
			}
			db.close();
		}
		if(settingsChangesMade)
		{
			settingsChangesMade=false;
			Intent calChangesMade=new Intent(this,CalendarObserverService.class);
			this.startService(calChangesMade);
		}
		super.onBackPressed();
	}
	public void onClickHandler(View view)
	{
		if(view.getId()==R.id.buttonWriteReview)
		{
			final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putInt("reviewWritten",1);
			editor.commit();
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.unome.silenceme.app")));

		}
	}
}
