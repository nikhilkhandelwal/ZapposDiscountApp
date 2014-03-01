package com.zappos.discount.main;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
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

import com.zappos.discount.main.ProductListProvider.DBHelper;

import android.app.Application;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class ZapposDiscountApp extends Application {

	/*This class is responsible for pulling the data using the REST api and inserting in the content provider*/
	
	static final String TAG = "ZapposDiscountApp";


	private List<Product> myProducts ;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

	}

	public void pullAndInsert() {
		try {
			new LongRunningGetIO().execute();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Failed to pull and insert" + e);
		}

	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

		private ProgressDialog progressDialog;
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
					"http://www.json-generator.com/j/coSmjZAKuW?indent=4");
			

			try {
				HttpResponse response = httpClient.execute(httpGet,
						localContext);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				JSONObject jsonObject = new JSONObject(readToEnd(is));

				JSONArray tsmresponse = (JSONArray) jsonObject.get("results");
				myProducts = new ArrayList<Product>();
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
													"productId"), 0)); // isFavorite boolean is initialized to 0 the first time
				}
				Iterator<Product> iterator = myProducts.iterator();
				while(iterator.hasNext()){
					
					Product tempProduct = iterator.next();
					String [] temp = {tempProduct.getProductId()};
					Cursor c = getContentResolver().query(ProductListProvider.CONTENT_URI, null, "_id=?", temp, null);
					// to update the state of favorite button from the pre existing state
					//i.i if product_id exists and the isFavorite == 1 then update the db else insert
					try {
						
						int isFavoriteIndex = c.getColumnIndex(ProductListProvider.IS_FAVORITE);
						if(c.moveToFirst() && Integer.parseInt(c.getString(isFavoriteIndex)) ==1)
						{
							tempProduct.setIsFavorite(1);
							
							getContentResolver().update(ProductListProvider.CONTENT_URI,ProductListProvider.productToValues(tempProduct ),ProductListProvider.PRODUCT_ID+"=?"
									,new String[] {String.valueOf(tempProduct.getProductId()) });
						}
						else
						{
							getContentResolver().insert(ProductListProvider.CONTENT_URI,
									ProductListProvider.productToValues(tempProduct));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
					finally{
						if ( c != null && !c.isClosed()) {
				            c.deactivate();
				            c.close();
				            c = null;
				        }
					}
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
			
		}
		
		

	}
	

}
