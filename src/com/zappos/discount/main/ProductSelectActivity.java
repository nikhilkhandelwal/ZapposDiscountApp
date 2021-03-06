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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

/*The main class which displays the whole product list pulled from Zappos.com. The use can favorite a particular product*/
public class ProductSelectActivity extends Activity {

	public static final String AUTHORITY = "content://com.zappos.discount.main.provider";
	public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
	private List<Product> myProducts = new ArrayList<Product>();

	public static final String TAG = "ProductSelectionActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ((ZapposDiscountApp)getApplication()).pullAndInsert();
		/*
		 * synchronized(this) { try { Thread.sleep(5000);
		 */
		new LongRunningGetIO().execute();

		/*
		 * } catch (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	private void populateListView() {
		ArrayAdapter<Product> adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.productListView);
		list.setAdapter(adapter);
	}

	private void populateProductList() {
		Log.d(TAG, "Inside populate product list");
		Cursor cursor = getContentResolver().query(
				ProductListProvider.CONTENT_URI, null, null, null, null);

		int productIdIndex = cursor
				.getColumnIndex(ProductListProvider.PRODUCT_ID);
		int productNameIndex = cursor
				.getColumnIndex(ProductListProvider.PRODUCT_NAME);
		int productPriceIndex = cursor
				.getColumnIndex(ProductListProvider.PRODUCT_PRICE);
		int productDiscountIndex = cursor
				.getColumnIndex(ProductListProvider.PRODUCT_DISCOUNT);
		int productUrlIndex = cursor
				.getColumnIndex(ProductListProvider.PRODUCT_URL);
		int thumbImageUrlIndex = cursor
				.getColumnIndex(ProductListProvider.THUMB_IMAGE_URL);
		int isFavoriteIndex = cursor
				.getColumnIndex(ProductListProvider.IS_FAVORITE);

		try {
			if (cursor != null && cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					Log.d(TAG, "insisde populate cursor");
					myProducts.add(new Product(cursor
							.getString(productPriceIndex), cursor
							.getString(productUrlIndex), cursor
							.getString(productNameIndex), cursor
							.getString(thumbImageUrlIndex), cursor
							.getString(productDiscountIndex), cursor
							.getString(productIdIndex), Integer.parseInt(cursor
							.getString(isFavoriteIndex))));

					cursor.moveToNext();

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.deactivate();
				cursor.close();
				cursor = null;
			}
		}

	}

	private class MyListAdapter extends ArrayAdapter<Product> {

		public MyListAdapter() {
			super(ProductSelectActivity.this, R.layout.item_view, myProducts);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)

			ViewHolder holder;

			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.item_view,
						parent, false);
				View row = itemView;
				holder = new ViewHolder(row);

				itemView.setTag(holder);
				holder.switchButton = (ToggleButton) itemView
						.findViewById(R.id.favorite_button);

			} else {
				holder = (ViewHolder) itemView.getTag();

			}
			// when favorite toggle button is clicked

			holder.switchButton.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (((ToggleButton) v).isChecked()) {
						((ToggleButton) v)
								.setBackgroundResource(android.R.drawable.btn_star_big_on);
						myProducts.get(position).setIsFavorite(1);

						getContentResolver().update(
								CONTENT_URI,
								ProductListProvider.productToValues(myProducts
										.get(position)),
								ProductListProvider.PRODUCT_ID + "=?",
								new String[] { String.valueOf(myProducts.get(
										position).getProductId()) });

					} else {
						((ToggleButton) v)
								.setBackgroundResource(android.R.drawable.btn_star_big_off);
						myProducts.get(position).setIsFavorite(0);
						getContentResolver().update(
								CONTENT_URI,
								ProductListProvider.productToValues(myProducts
										.get(position)),
								ProductListProvider.PRODUCT_ID + "=?",
								new String[] { String.valueOf(myProducts.get(
										position).getProductId()) });
					}

				}
			});

			// Find the Product to work with.
			Product currentProduct = myProducts.get(position);

			// Fill the view
			int loader = R.drawable.zapposlogo;
			ImageView imageView = (ImageView) itemView
					.findViewById(R.id.item_icon);

			String uri = currentProduct.getThumbnailImageUrl();
			uri = uri.replaceAll("\\\\", "");

			ImageLoader imgLoader = new ImageLoader(getApplicationContext());

			imgLoader.DisplayImage(uri, loader, imageView);

			// Product Name:
			TextView makeText = (TextView) itemView
					.findViewById(R.id.item_txtMake);
			makeText.setText("" + currentProduct.getProductName());

			// Product Price:
			TextView yearText = (TextView) itemView
					.findViewById(R.id.item_txtYear);
			yearText.setText(currentProduct.getPrice());

			// Discount:
			TextView condionText = (TextView) itemView
					.findViewById(R.id.item_txtCondition);
			condionText.setText(currentProduct.getPercentOff());

			// Setting Favorite Button image depending on the value of favorite
			// in Db
			if (currentProduct.getIsFavorite() == 1) {
				Log.d(TAG, "inside favorite on");
				holder.switchButton
						.setBackgroundResource(android.R.drawable.btn_star_big_on);
			} else
				holder.switchButton
						.setBackgroundResource(android.R.drawable.btn_star_big_off);

			return itemView;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		Intent intentFavorites = new Intent(this, ProductDisplayActivity.class);

		switch (item.getItemId()) {

		case R.id.item_favorite_activity:

			startActivity(intentFavorites);
			return true;

		default:

			return false;

		}
	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

		private ProgressDialog progressDialog;
		private List<Product> tempProducts = new ArrayList<Product>();

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
			//http://www.json-generator.com/j/coSmjZAKuW?indent=4 the url used for testing as the above url was generally down

			try {
				HttpResponse response = httpClient.execute(httpGet,
						localContext);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				JSONObject jsonObject = new JSONObject(readToEnd(is));

				JSONArray tsmresponse = (JSONArray) jsonObject.get("results");
				Log.d(TAG, "" + tsmresponse.length());
				for (int i = 0; i < tsmresponse.length(); i++) {
					tempProducts
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

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (progressDialog != null && progressDialog.isShowing())
				progressDialog.dismiss();

			for (Product tempProduct : tempProducts) {

				getContentResolver().insert(ProductListProvider.CONTENT_URI,
						ProductListProvider.productToValues(tempProduct));

			}
			Log.d(TAG, "myProducts has : " + myProducts.size());

			populateProductList();
			populateListView();

		}

		@Override
		protected void onPreExecute() {

			progressDialog = new ProgressDialog(ProductSelectActivity.this);
			progressDialog.setMessage("Fetching Products..");
			progressDialog.setCancelable(false);
			progressDialog.show();

		}

	}
}
