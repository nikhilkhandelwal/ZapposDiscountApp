package com.zappos.discount.main;

/*A class to display the products that are favorited by the user*/

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

public class ProductDisplayActivity extends Activity {
	


	public static final String AUTHORITY = "content://com.zappos.discount.main.provider";
	public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
	
	public static final String TAG= "FavoriteDisplayActivity";
	private List<Product> myProducts = new ArrayList<Product>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		populateProductList();
		populateListView();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.item_product_display, menu);
		return true;
	}
	
	private void populateListView() {
		ArrayAdapter<Product> adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.productListView);
		list.setAdapter(adapter);
	}
	private void populateProductList() {
		
		Cursor cursor = getContentResolver().query(ProductListProvider.CONTENT_URI, null, null, null, null );
		
		 int productIdIndex = cursor.getColumnIndex(ProductListProvider.PRODUCT_ID);
		 
		 int productNameIndex = cursor.getColumnIndex(ProductListProvider.PRODUCT_NAME);
		 int productPriceIndex = cursor.getColumnIndex(ProductListProvider.PRODUCT_PRICE);
		 int productDiscountIndex = cursor.getColumnIndex(ProductListProvider.PRODUCT_DISCOUNT);
		 int productUrlIndex = cursor.getColumnIndex(ProductListProvider.PRODUCT_URL);
		 int thumbImageUrlIndex = cursor.getColumnIndex(ProductListProvider.THUMB_IMAGE_URL);
		 int isFavorite = cursor.getColumnIndex(ProductListProvider.IS_FAVORITE);
		 
	    try {
	        if (cursor != null && cursor.moveToFirst()) {
	        	for (int i = 0; i < cursor.getCount(); i++){ 
	        		if(Integer.parseInt(cursor.getString(isFavorite))==1 ) // only populating the items which have isFavorite flag = 1
	        		myProducts.add(new Product(cursor.getString(productPriceIndex),cursor.getString(productUrlIndex),cursor.getString(productNameIndex),
	        				cursor.getString(thumbImageUrlIndex),cursor.getString(productDiscountIndex),cursor.getString(productIdIndex)
	        				,Integer.parseInt(cursor.getString(isFavorite)) ));
	                 
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
		
		
	}
	

	private class MyListAdapter extends ArrayAdapter<Product> {

		public MyListAdapter() {
			super(ProductDisplayActivity.this, R.layout.item_view, myProducts);
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

					if (((ToggleButton) v).isChecked()) {
						((ToggleButton) v)
								.setBackgroundResource(android.R.drawable.btn_star_big_on);
						myProducts.get(position).setIsFavorite(1);
						
						getContentResolver().update(CONTENT_URI,ProductListProvider.productToValues(myProducts.get(position) ),ProductListProvider.PRODUCT_ID+"=?"
								,new String[] {String.valueOf(myProducts.get(position).getProductId()) });

					} else {
						((ToggleButton) v)
								.setBackgroundResource(android.R.drawable.btn_star_big_off);
						myProducts.get(position).setIsFavorite(0);
						getContentResolver().update(CONTENT_URI,ProductListProvider.productToValues(myProducts.get(position) ),ProductListProvider.PRODUCT_ID+"=?"
								,new String[] {String.valueOf(myProducts.get(position).getProductId()) });
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
			
			// Favorite button
			if (currentProduct.getIsFavorite()==1)
				holder.switchButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
			else
				holder.switchButton.setBackgroundResource(android.R.drawable.btn_star_big_off);

			return itemView;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		Intent intentProductList = new Intent(this, ProductSelectActivity.class);
		
		switch(item.getItemId()){
		
		case R.id.item_product_display :
			
			startActivity(intentProductList);
			return true;
		
			
			default:
				
				return false;
				
		}
}
	
}
