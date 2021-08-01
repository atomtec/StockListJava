package com.f11.udemy.stocklist.data.local.db.room;

import android.app.Application;

import com.f11.udemy.stocklist.data.local.LocalDataSource;
import com.f11.udemy.stocklist.data.model.AppStock;

import java.util.List;

import androidx.lifecycle.LiveData;


public class LocalDataSourceImpl implements LocalDataSource {

    private static LocalDataSourceImpl sInstance = null;

    private StockDao mStockDao;

    private LiveData<List<AppStock>> mStockObservable;

    private LocalDataSourceImpl(Application context) {
        mStockDao = StockRoomDB.getDatabase(context.getApplicationContext()).mStockDao();
        mStockObservable = mStockDao.observeStocks();
    }


    public static LocalDataSourceImpl getInstance(Application context) {
        if (sInstance == null){
            sInstance = new LocalDataSourceImpl(context);
        }
        return sInstance;
    }

    @Override
    public void insertorUpdate(AppStock stock) {
        mStockDao.insert(stock);
    }

    @Override
    public List<AppStock> getAllStocks() {
        return mStockDao.getAllStocks();
    }

    @Override
    public void deleteBySymbol(String symbol) {
        mStockDao.delete(symbol);
    }

    @Override
    public LiveData<List<AppStock>> observeStocks() {
        return mStockObservable;
    }
}
