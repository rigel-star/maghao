package com.service.maghao.sqlite_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.service.maghao.model_classes.DraftsItems;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    //attr and table info we needed to create table
    private static final String TABLE_NAME = "fav_products";
    private static final int DATABASE_VERSION = 1;
    private static final String KEY_ID = "p_id";
    private static final String KEY_NAME = "p_name";
    private static final String KEY_PRICE = "p_price";
    private static final String KEY_QTY = "p_qty";
    private static final String KEY_IMG = "p_img";
    private static final String KEY_STATE = "p_state";
    private static final String DATABASE_NAME = "offlineProductsManager";

    //create table for fav products in mobile device
    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //query to create table
        //table name specify table name and others are attribute to those
        String query = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " TEXT, " +
                KEY_NAME + " TEXT, " + KEY_QTY + " TEXT, " + KEY_IMG + " TEXT, " + KEY_PRICE + " TEXT" + ")";

        String query2 = "CREATE TABLE user_location(id " + "TEXT, " +
                "latitude TEXT, longitude TEXT)";

        ContentValues values = new ContentValues();
        values.put("id", "1");
        values.put("latitude", "0.0");
        values.put("longitude", "0.0");
        db.insert("user_location", null, values);

        db.execSQL(query2);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //handle if the table already exists in that phone if it exists
        //then drop or delete table
        //query to delete that table if it already exists
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);

        //then create the table after dropping the table (if exist)
        onCreate(db);
    }

    //add contact to sqlite db
    public void addProduct(DraftsItems draftsItem){

        //only to read data not to write
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //set values which should be stored in database
        values.put(KEY_ID, draftsItem.getId());
        values.put(KEY_NAME, draftsItem.getName());
        values.put(KEY_IMG, draftsItem.getImage());
        values.put(KEY_PRICE, draftsItem.getPrice());
        values.put(KEY_QTY, draftsItem.getQty());

        //now finally insert data to offline fav list
        database.insert(TABLE_NAME, null, values);
        //and close the data entry
        database.close();
    }

    public List<DraftsItems> getAllItems(){

        //reference to read all data
        SQLiteDatabase database = this.getWritableDatabase();

        List<DraftsItems> allItems = new ArrayList<>();

        //query to select all produtcs from table
        String query = "SELECT * FROM " + TABLE_NAME;

        //cursor points to top row of table product
        Cursor cursor = database.rawQuery(query, null);

        //loop thorough tablle and increase cursor position
        if(cursor.moveToFirst()){

            do {
                DraftsItems item = new DraftsItems();
                item.setId(cursor.getString(0));
                item.setName(cursor.getString(1));
                item.setQty(cursor.getString(2));
                item.setImage(cursor.getString(3));
                item.setPrice(cursor.getString(4));

                //adding contact to list
                allItems.add(item);
            } while (cursor.moveToNext());
        }

        database.close();
        return allItems;

    }

    //delete product from database
    public void deleteProduct(int id){

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(TABLE_NAME, KEY_ID + " =?", new String[]{String.valueOf(id)});
        database.close();
    }
}
