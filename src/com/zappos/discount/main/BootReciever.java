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
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		long interval = Long.parseLong("300000"); // 5 minute to call notification service
		PendingIntent operation = PendingIntent.getService(context, -1, new Intent(NotificationService.N_SERVICE ), PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alaramManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		alaramManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(),interval, operation);
		
		Log.d("BootReciever", "On recieve: delay "+interval);
		
	}

}
