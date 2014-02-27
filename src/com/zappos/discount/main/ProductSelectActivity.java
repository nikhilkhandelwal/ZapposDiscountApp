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
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Demonstrate how to populate a complex ListView with icon and text. Icon
 * images taken from icon pack by Everaldo Coelho (http://www.everaldo.com)
 */
public class ProductSelectActivity extends Activity {
	

	public static final String AUTHORITY = "content://com.zappos.discount.main.provider";
	public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
	private List<Product> myProducts = new ArrayList<Product>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		populateProductList();
		// populateListView();
		registerClickCallback();

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	

	private void populateProductList() {

		new LongRunningGetIO().execute();
	}

	private void registerClickCallback() {
		ListView list = (ListView) findViewById(R.id.productListView);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {

				Product clickedProduct = myProducts.get(position);
				String message = "You clicked position " + position
						+ " Which is Product make " + clickedProduct.getPrice();
				Toast.makeText(ProductSelectActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	private class MyListAdapter extends ArrayAdapter<Product> {
		boolean[] switchState = new boolean[101];

		public MyListAdapter() {
			super(ProductSelectActivity.this, R.layout.item_view, myProducts);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
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

					int index = (Integer) v.getTag();
					Log.v("tag of switch============", "" +position);
					if (((ToggleButton) v).isChecked()) {
						switchState[index] = true;
						((ToggleButton) v)
								.setBackgroundResource(android.R.drawable.btn_star_big_on);
						if(myProducts.get(position)!=null)
						getContentResolver().insert(CONTENT_URI, ProductListProvider.productToValues(myProducts.get(position) ) );

					} else {
						((ToggleButton) v)
								.setBackgroundResource(android.R.drawable.btn_star_big_off);
						switchState[index] = false;
					}

				}
			});

			// deciding the state of favorite button
			if (switchState[position])
				holder.switchButton
						.setBackgroundResource(android.R.drawable.btn_star_big_on);

			else
				holder.switchButton
						.setBackgroundResource(android.R.drawable.btn_star_big_off);

			holder.switchButton.setTag(Integer.valueOf(position));

			// Find the Product to work with.
			Product currentProduct = myProducts.get(position);

			// Fill the view
			int loader = R.drawable.bug;
			ImageView imageView = (ImageView) itemView
					.findViewById(R.id.item_icon);
			// imageView.setImageResource(currentProduct.getThumbnailImageUrl());

			String uri = currentProduct.getThumbnailImageUrl();
			uri = uri.replaceAll("\\\\", "");

			ImageLoader imgLoader = new ImageLoader(getApplicationContext());

			imgLoader.DisplayImage(uri, loader, imageView);

			// Make:
			TextView makeText = (TextView) itemView
					.findViewById(R.id.item_txtMake);
			makeText.setText("" + currentProduct.getProductName());

			// Year:
			TextView yearText = (TextView) itemView
					.findViewById(R.id.item_txtYear);
			yearText.setText(currentProduct.getPrice());

			// Condition:
			TextView condionText = (TextView) itemView
					.findViewById(R.id.item_txtCondition);
			condionText.setText(currentProduct.getPercentOff());

			return itemView;
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
													"productId")));
				}

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}

			return null;

		}

		protected void onPostExecute(String results) {

			ArrayAdapter<Product> adapter = new MyListAdapter();
			ListView list = (ListView) findViewById(R.id.productListView);
			list.setAdapter(adapter);

		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		Intent intentFavorites = new Intent(this, ProductDisplayActivity.class);
		
		switch(item.getItemId()){
		
		case R.id.item_favorite_activity:
			
			startActivity(intentFavorites);
			return true;
		
			
			default:
				
				return false;
				
		}
}
}
