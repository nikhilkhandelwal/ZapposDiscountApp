package com.zappos.discount.main;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/*This class managed the content provider for the application*/

public class ProductListProvider extends ContentProvider {
	
	public static final String AUTHORITY = "content://com.zappos.discount.main.provider";
	public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
	public static final String DB_NAME="product.db";
	public static final String TABLE_NAME="product";
	public static final int DB_VERSION=6;
	public static final String PRODUCT_ID= "_id";
	public static final String PRODUCT_NAME="product_name";
	public static final String PRODUCT_PRICE="product_price";
	public static final String PRODUCT_DISCOUNT="product_discount";
	public static final String PRODUCT_URL="product_url";
	public static final String THUMB_IMAGE_URL="thumb_image_url";
	public static final String IS_FAVORITE="is_favorite";
	public static final String TAG= "ProductListProvider";
	
	DBHelper dbHelper;
	SQLiteDatabase db;

	@Override
	public boolean onCreate() {

		dbHelper = new DBHelper(getContext());
		return true;
	}
	
	public static ContentValues productToValues(Product product)
	{
		ContentValues values = new ContentValues();
		
		values.put(PRODUCT_ID,product.getProductId());
		values.put(PRODUCT_NAME,product.getProductName());
		values.put(PRODUCT_PRICE,product.getPrice());
		values.put(PRODUCT_DISCOUNT,product.getPercentOff());
		values.put(PRODUCT_URL,product.getPercentOff());
		values.put(THUMB_IMAGE_URL,product.getThumbnailImageUrl());
		values.put(IS_FAVORITE, product.getIsFavorite());
		
		return values;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		
		if(uri.getLastPathSegment()==null)
		{
			return "vnd.android.cursor.item/vnd.example.yamba.status";
		}
		else
		{
			return "vnd.android.cursor.dir/vnd.example.yamba.status";
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		db = dbHelper.getWritableDatabase();
		//Log.d(TAG, values.getAsString(PRODUCT_ID));
		long id= db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		if(id!=-1)
		{
		return Uri.withAppendedPath(uri	, Long.toString(id));
		}
		else
		{
			return uri;
		}
		
	}

	

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		db = dbHelper.getReadableDatabase();
		Cursor cursor=db.query(ProductListProvider.TABLE_NAME, projection, selection, selectionArgs , null, null, sortOrder);
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		
		db = dbHelper.getWritableDatabase();
		   int updateCount = 0;
		      updateCount = db.update(ProductListProvider.TABLE_NAME, 
		            values, 
		            selection,
		            selectionArgs);
		      
		      return updateCount;

	}
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	class DBHelper extends SQLiteOpenHelper{

		static final String TAG= "DBHelper";
		public DBHelper(Context context) {
			super(context, ProductListProvider.DB_NAME, null, ProductListProvider.DB_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
			String sql= String.format("create table %s "+"(%s int primary key, %s text, %s text, %s text,%s text,%s text, %s int)", ProductListProvider.TABLE_NAME, ProductListProvider.PRODUCT_ID,
					ProductListProvider.PRODUCT_NAME,ProductListProvider.PRODUCT_PRICE,ProductListProvider.PRODUCT_DISCOUNT,
					ProductListProvider.PRODUCT_URL,ProductListProvider.THUMB_IMAGE_URL, ProductListProvider.IS_FAVORITE);
			Log.d(TAG,"On create in SQl");
			
			db.execSQL(sql);
			
			
			
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exists "+ProductListProvider.TABLE_NAME);
			onCreate(db);
			}
		
	}
}


