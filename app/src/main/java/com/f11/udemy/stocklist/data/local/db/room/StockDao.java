package com.f11.udemy.stocklist.data.local.db.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.f11.udemy.stocklist.data.model.AppStock;

import java.util.List;

@Dao
public interface StockDao {
    @Query("SELECT * from stock_table ORDER BY symbol ASC")
    List<AppStock> getAllStocks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppStock stock);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AppStock> stocks);

    @Query("DELETE FROM stock_table WHERE symbol = :symbol")
    void delete(String symbol);

}
