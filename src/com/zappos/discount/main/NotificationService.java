package com.zappos.discount.main;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationService extends IntentService {

	static final String TAG = "Notification Service";
	public static String N_SERVICE = "com.zappos.discount.main.N_SERVICE";

	public NotificationService() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String ns = Context.NOTIFICATION_SERVICE;
		int NOTIFICATION_ID = 1;
		Log.d(TAG, "inside notification service");
		int icon = R.drawable.bug;
		CharSequence contentTitle = "Zappos Sale";
		CharSequence contentText = "Some of your favorite items have gone on sale!";
		if(checkDiscountForFavorites())
		{
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(icon)
		        .setContentTitle(contentTitle)
		        .setContentText(contentText);
		Intent notificationIntent = new Intent(this, ProductDisplayActivity.class);
		
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ProductDisplayActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(notificationIntent);
		PendingIntent notificationPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(notificationPendingIntent);
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		
		}
		stopService(intent);
	}

	private boolean checkDiscountForFavorites() {
		// TODO Auto-generated method stub
		
		((ZapposDiscountApp)getApplication()).pullAndInsert();
		Cursor cursor = getContentResolver().query(ProductListProvider.CONTENT_URI, null, null, null, null );
		boolean toNotify=false;
		 int productDiscountIndex = cursor.getColumnIndex(ProductListProvider.PRODUCT_DISCOUNT);
		 int isFavorite = cursor.getColumnIndex(ProductListProvider.IS_FAVORITE);
		 
	    try {
	        if (cursor != null && cursor.moveToFirst()) {
	        	for (int i = 0; i < cursor.getCount(); i++){ 
	        		String discount = cursor.getString(productDiscountIndex).replaceAll("%", "");
	        		Log.d(TAG,discount);
	        		if(Integer.parseInt(cursor.getString(isFavorite))==1&& Integer.parseInt(discount)>=0 )
	        			toNotify = true;
	                cursor.moveToNext();
	        }
	    }} catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (cursor != null && !cursor.isClosed()) {
	            cursor.deactivate();
	            cursor.close();
	            cursor = null;
	        }
	    }
		
		
		return toNotify;
	}

}
