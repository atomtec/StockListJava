package com.f11.udemy.stocklist;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.f11.udemy.stocklist.data.local.db.room.LocalDataSourceImpl;
import com.f11.udemy.stocklist.data.remote.RemoteStockProviderSDK;
import com.f11.udemy.stocklist.data.repo.DataRepository;
import com.f11.udemy.stocklist.data.repo.StockRepository;

public class StockListAppLication extends Application {

    public DataRepository repository = null;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = StockRepository.getInstance(LocalDataSourceImpl.getInstance(this),
                RemoteStockProviderSDK.getInstance(),new Handler(Looper.getMainLooper()));
    }
}
