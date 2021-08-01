package com.f11.udemy.stocklist.data.local.db.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.f11.udemy.stocklist.data.model.AppStock;

@Database(entities = {AppStock.class}, version = 1,  exportSchema = false)
public abstract class StockRoomDB extends RoomDatabase {

    public abstract StockDao mStockDao();

    private static StockRoomDB INSTANCE;

    public static StockRoomDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StockRoomDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StockRoomDB.class, "stock_database")
                            .fallbackToDestructiveMigration()//not defining migration for demo
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
