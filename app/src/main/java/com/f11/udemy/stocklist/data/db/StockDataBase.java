package com.f11.udemy.stocklist.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.f11.udemy.stocklist.data.model.AppStock;

import java.util.ArrayList;
import java.util.List;

public class StockDataBase extends SQLiteOpenHelper {

    private static final String COLUMN_ID = "symbol";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CHANGE = "change";
    private static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
    private static final String COLUMN_NAME = "stock_name";
    private static final String TABLE_NAME = "stocks";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "stocks_db";

    private static StockDataBase sInstance = null;

    private StockDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table SQL query
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " TEXT PRIMARY KEY ,"
                    + COLUMN_PRICE + " REAL ,"
                    + COLUMN_CHANGE + " REAL ,"
                    + COLUMN_ABSOLUTE_CHANGE + " REAL ,"
                    + COLUMN_NAME + " TEXT "
                    + ")";

    public static StockDataBase getInstance(Context context) {
        if (sInstance == null){
            sInstance = new StockDataBase(context);
        }
        return sInstance;
    }


    public void insertorUpdate(AppStock stock) {
        // get writable database as we want to write data

        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, stock.getSymbol());
        values.put(COLUMN_PRICE, stock.getPrice());
        values.put(COLUMN_CHANGE, stock.getPrice());
        values.put(COLUMN_ABSOLUTE_CHANGE, stock.getAbsolutechange());
        values.put(COLUMN_NAME, stock.getStockname());

        db.insertWithOnConflict(TABLE_NAME,null,values,
                SQLiteDatabase.CONFLICT_REPLACE);

        // close db connection
        db.close();
        // return newly inserted row id
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public List<AppStock> getAllStocks() {
        List<AppStock> dbStocks= new ArrayList<>();

        // Select All Query

        String selectQuery = "SELECT  * FROM " + TABLE_NAME ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AppStock stock = new AppStock();
                stock.setSymbol(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                stock.setStockname(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                stock.setPrice(cursor.getFloat(cursor.getColumnIndex(COLUMN_PRICE)));
                stock.setChange(cursor.getFloat(cursor.getColumnIndex(COLUMN_CHANGE)));
                stock.setAbsolutechange(cursor.getFloat(cursor.getColumnIndex(COLUMN_ABSOLUTE_CHANGE)));

                dbStocks.add(stock);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return stocks list
        return dbStocks;
    }

    public void deleteBySymbol(String symbol){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{symbol});

    }
}
