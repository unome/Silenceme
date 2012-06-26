package com.unome.silenceme.app;


import java.util.ArrayList;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class DisplayKeywords extends Activity 
{
	DBhelper dbhelper;
	final String TAG="Silenceme";
	ArrayList<String> checked = new ArrayList<String>();
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.displaykeywords);
		dbhelper = new DBhelper(this);
		showKeywords();
	}

	private void showKeywords()
	{
		TableLayout tl = (TableLayout) findViewById(R.id.kwTable);
		final Button delBut=(Button)findViewById(R.id.deleteButton);
		final Button disBut=(Button)findViewById(R.id.button_disable);
		final Button enaBut=(Button)findViewById(R.id.enable_button);
		delBut.setVisibility(View.INVISIBLE);
		disBut.setVisibility(View.INVISIBLE);
		enaBut.setVisibility(View.INVISIBLE);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String TasksSelect = "SELECT * FROM " + DBhelper.kwTName;
		Cursor sqlIt = db.rawQuery(TasksSelect, null);
		tl.removeAllViews();
		while (sqlIt.moveToNext()) {
			TableRow tbrow = new TableRow(this);
			tbrow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			CheckBox cBox = new CheckBox(this);
			cBox.setTag("cB" + sqlIt.getString(1));
			cBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					delBut.setVisibility(View.VISIBLE);
					disBut.setVisibility(View.VISIBLE);
					enaBut.setVisibility(View.VISIBLE);
					TableRow currRow = (TableRow) v.getParent(); // getParent returns the parent
					ArrayList<View> allViews = new ArrayList<View>();
					allViews = currRow.getTouchables();
					TextView tName = (TextView) (allViews.get(1));
					checked.add((String) tName.getText());

				}
			});
			tbrow.addView(cBox);
			TextView kName = new TextView(this);
			kName.setText(sqlIt.getString(1));
			kName.setPadding(0, 5, 0, 5);
			kName.setClickable(true);
			kName.setTag(sqlIt.getString(1));
			kName.setTextSize(18);
			String state = (String) sqlIt.getString(2);
			if (state.equals("False"))
				kName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			tbrow.addView(kName);
			tl.addView(tbrow);
		}
		sqlIt.close();
		db.close();
	}

	public void onClickHandler(View view)
	{
		if(view.getId()==R.id.deleteButton)
		{
			deleteKeywords();
		}
		if(view.getId()==R.id.button_disable)
		{
			disableKeywords();
		}
		if(view.getId()==R.id.enable_button)
			enableKeyWords();
        Intent changeIntent=new Intent(view.getContext(),CalendarObserverService.class);
        view.getContext().startService(changeIntent);
	}

	private void enableKeyWords()
	{
		GridLayout Parent=(GridLayout)findViewById(R.id.tableParent);
		for (int i = 0; i < checked.size(); ++i)
		{
			SQLiteDatabase db = dbhelper.getReadableDatabase();
			TextView kName = (TextView) Parent.findViewWithTag(checked.get(i));
			kName.setPaintFlags(0);
			CheckBox cBox = (CheckBox) Parent.findViewWithTag("cB"
					+ (String) kName.getText());
			cBox.setChecked(false);
			String sqlQuery= "Select "+DBhelper.C_KStatus + " from "+ DBhelper.kwTName+" where "+DBhelper.C_KName+"='"+
					(String)kName.getText()+"'";
			Cursor cur=db.rawQuery(sqlQuery,null);
			cur.moveToNext();
			if(cur.getString(0).equals("False"))
			{
				sqlQuery = "UPDATE " + DBhelper.kwTName + " SET "
						+ DBhelper.C_KStatus + "='True' WHERE "
						+ DBhelper.C_KName + "='" + (String) kName.getText()
						+ "'";
				db.execSQL(sqlQuery);
				db.close();
				Intent compileKwIntent=new Intent(this,CompileKeyWord.class);
				compileKwIntent.putExtra("keyword",kName.getText().toString());
				this.startService(compileKwIntent);
			}
			cur.close();
		}

		final Button delBut=(Button)findViewById(R.id.deleteButton);
		final Button disBut=(Button)findViewById(R.id.button_disable);
		final Button enaBut=(Button)findViewById(R.id.enable_button);
		delBut.setVisibility(View.INVISIBLE);
		disBut.setVisibility(View.INVISIBLE);
		enaBut.setVisibility(View.INVISIBLE);
		checked.clear();
		onCreate(null);

	}

	private void disableKeywords() 
	{
		GridLayout Parent=(GridLayout)findViewById(R.id.tableParent);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		for (int i = 0; i < checked.size(); ++i)
		{
			TextView kName = (TextView) Parent.findViewWithTag(checked.get(i));
			deleteAlarmMananger(kName.getText().toString(),db);
			kName.setPaintFlags(kName.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			CheckBox cBox = (CheckBox) Parent.findViewWithTag("cB"
					+ (String) kName.getText());
			cBox.setChecked(false);
			String sqlQuery = "UPDATE " + DBhelper.kwTName + " SET "
					+ DBhelper.C_KStatus + "='False' WHERE "
					+ DBhelper.C_KName + "='" + (String) kName.getText()
					+ "'";
			db.execSQL(sqlQuery);
			//Log.d(TAG,kName.getText().toString()+" has been disabled");

		}
		db.close();
		final Button delBut=(Button)findViewById(R.id.deleteButton);
		final Button disBut=(Button)findViewById(R.id.button_disable);
		final Button enaBut=(Button)findViewById(R.id.enable_button);
		delBut.setVisibility(View.INVISIBLE);
		disBut.setVisibility(View.INVISIBLE);
		enaBut.setVisibility(View.INVISIBLE);
		checked.clear();
		onCreate(null);

	}


	private void deleteKeywords() 
	{
		GridLayout Parent=(GridLayout)findViewById(R.id.tableParent);
		SQLiteDatabase db=dbhelper.getReadableDatabase();
		for(int i=0;i<checked.size();++i)
		{
			TextView kName=(TextView)Parent.findViewWithTag(checked.get(i));
			deleteAlarmMananger(kName.getText().toString(),db);
			String deleteQuery="DELETE FROM "+DBhelper.kwTName+
					" WHERE "+ DBhelper.C_KName + "='"+(String)kName.getText()+"'";
			db.execSQL(deleteQuery);
			deleteQuery="DELETE FROM "+DBhelper.calTName+" WHERE "+DBhelper.C_KName+ "='"+
					(String)kName.getText()+"'";
			db.execSQL(deleteQuery);
			//Log.d(TAG,kName.getText().toString()+" has been deleted");
		}
		db.close();
		checked.clear();
		onCreate(null);

	}

	private void deleteAlarmMananger(String kwName, SQLiteDatabase db)
	{
		//get the eventid and cancel the alarm manager
		String delAlarms="Select "+DBhelper.calEventId+" from "+DBhelper.calTName+" where "+DBhelper.C_KName+" = "
				+"\""+kwName+"\"";
		Cursor alarmCur=db.rawQuery(delAlarms,null);
		if(alarmCur.getCount()!=0)
		{
			for(int i=0;i<alarmCur.getCount();++i)
			{
				alarmCur.moveToNext();
				Intent intent = new Intent(this,SilencePhone.class);
				PendingIntent sender = PendingIntent.getBroadcast(this,Integer.parseInt(alarmCur.getString(0)), intent, PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				am.cancel(sender);
			}
		}
		alarmCur.close();
		delAlarms="Delete from "+DBhelper.calTName+" where "+DBhelper.C_KName+" = "
				+"\""+kwName+"\"";
		db.execSQL(delAlarms);
	}

}


