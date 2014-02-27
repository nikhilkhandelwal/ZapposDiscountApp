package com.zappos.discount.main;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ZapposDiscountApp extends Application  {
	
	static final String TAG = "Zappos Discount App";
	
	static  int count=0;
	
	private List<Product> myProducts = new ArrayList<Product>();
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
	
	
	}
	
	
	public void pullAndInsert()
	{
		try {
			new LongRunningGetIO().execute();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG,"Failed to pull and insert"+e);
		}
		
	}
	
	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

		

		private String readToEnd(InputStream input) throws IOException {
			DataInputStream dis = new DataInputStream(input);
			byte[] stuff = new byte[1024];
			ByteArrayOutputStream buff = new ByteArrayOutputStream();
			int read = 0;
			while ((read = dis.read(stuff)) != -1) {
				buff.write(stuff, 0, read);
			}

			return new String(buff.toByteArray());
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(
					"http://api.zappos.com/Search?limit=100&page=1&key=a73121520492f88dc3d33daf2103d7574f1a3166");

			try {
				HttpResponse response = httpClient.execute(httpGet,
						localContext);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				JSONObject jsonObject = new JSONObject(readToEnd(is));

				JSONArray tsmresponse = (JSONArray) jsonObject.get("results");
				/*System.out.println(tsmresponse.getJSONObject(1)
									.getString("price"));
									
*/
				for (int i = 0; i < tsmresponse.length(); i++) {
					myProducts
							.add(new Product(tsmresponse.getJSONObject(i)
									.getString("price"), tsmresponse
									.getJSONObject(i).getString("productUrl"),
									tsmresponse.getJSONObject(i).getString(
											"productName"), tsmresponse
											.getJSONObject(i).getString(
													"thumbnailImageUrl"),
									tsmresponse.getJSONObject(i).getString(
											"percentOff"), tsmresponse
											.getJSONObject(i).getString(
													"productId"),0));
					//Log.d(TAG,"inserting first time in DB");
				}

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}

			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			for (Product tempProduct : myProducts) {
				//statusData.insert(status);
				
				getContentResolver().insert(ProductListProvider.CONTENT_URI, ProductListProvider.productToValues(tempProduct));
				Log.d(TAG,"pull and insert " +tempProduct.getPrice());				
				
			}
		}

		
	}

	
	
}
