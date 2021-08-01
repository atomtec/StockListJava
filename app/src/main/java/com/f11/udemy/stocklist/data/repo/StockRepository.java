package com.f11.udemy.stocklist.data.repo;

import android.content.Context;

import com.f11.udemy.stocklist.data.local.LocalDataSource;
import com.f11.udemy.stocklist.data.model.AppStock;
import com.f11.udemy.stocklist.data.model.FetchStatus;
import com.f11.udemy.stocklist.data.remote.RemoteDataSource;
import com.f11.udemy.stocklist.data.remote.RemoteStockProviderSDK;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class StockRepository implements DataRepository{

    private static DataRepository INSTANCE = null;

    private LocalDataSource mLocalDataSource = null;

    private RemoteDataSource mRemoteDataSource = null;

    private LiveData<List<AppStock>> mAppStockObservable;

    private MutableLiveData<FetchStatus> mFetchStatus = new MutableLiveData<FetchStatus>();



    private StockRepository(@NonNull LocalDataSource localDataSource, @NonNull RemoteDataSource remoteDataSource){
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
        mAppStockObservable = mLocalDataSource.observeStocks();
    }

    public static DataRepository getInstance(LocalDataSource localDataSource,
                                              RemoteDataSource remoteDataSource) {
        if (INSTANCE == null) {
            synchronized (StockRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE =new StockRepository(localDataSource ,
                            remoteDataSource);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void searchAndAddStock(String symbol) {
        AppStock stock = null;
        mFetchStatus.postValue(FetchStatus.FETCHING);
        try {
            stock = mRemoteDataSource.getStockBySymbol(symbol);
        } catch (IOException e) {
            mFetchStatus.postValue(FetchStatus.STOCK_NOT_FOUND);
            e.printStackTrace();
        }
        if(stock != null){
            mLocalDataSource.insertorUpdate(stock);
            mFetchStatus.postValue(FetchStatus.STOCK_FOUND);
        }

    }

    @Override
    public void deleteBySymbol(String symbol) {

    }

    @Override
    public void refreshStocks() {
        List<AppStock> appStocks = null;

        try {
            appStocks = mLocalDataSource.getAllStocks();
            mRemoteDataSource.getRemoteStocks(appStocks);
            for(AppStock stock:appStocks){
                mLocalDataSource.insertorUpdate(stock);
            }

        } catch (IOException e) {
            e.printStackTrace();
            //Ignore
        }
    }

    @Override
    public LiveData<List<AppStock>> observeStocks() {
        return mAppStockObservable;
    }

    @Override
    public LiveData<FetchStatus> observeFetchStatus() {
        return mFetchStatus;
    }
}
