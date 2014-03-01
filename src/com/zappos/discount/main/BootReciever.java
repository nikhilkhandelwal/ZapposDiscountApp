package com.zappos.discount.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReciever extends BroadcastReceiver {
	
	/*This class recieves the broadcast at BOOT time to set the alarm to periodically set the alarm service to call notification service 
	at the interval. The interval is har coded to 5 minutes. It can the canged by setting the interval variable
*/
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String state = intent.getExtras().getString("extra");
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		PendingIntent operation = PendingIntent.getService(context, -1, new Intent(NotificationService.N_SERVICE ), PendingIntent.FLAG_UPDATE_CURRENT);
		if(state.equalsIgnoreCase("start"))
		{
		long interval = Long.parseLong("30000"); // 5 minute to call notification service
		
		
		alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(),interval, operation);
		Log.d("BootReciever", "Alaram set with interval : "+interval);
		}
		else
		{
			alarmManager.cancel(operation);
			Log.d("BootReciever", "Alaram cancelled ");
		}
		
		
	}

}
