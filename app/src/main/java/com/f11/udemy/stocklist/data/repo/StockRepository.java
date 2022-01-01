package com.f11.udemy.stocklist.data.repo;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.f11.udemy.stocklist.data.local.LocalDataSource;
import com.f11.udemy.stocklist.data.model.AppStock;
import com.f11.udemy.stocklist.data.model.FetchStatus;
import com.f11.udemy.stocklist.data.remote.RemoteDataSource;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class StockRepository implements DataRepository{

    private static final String TAG = "StockRepository" ;
    private static DataRepository INSTANCE = null;

    private LocalDataSource mLocalDataSource = null;

    private RemoteDataSource mRemoteDataSource = null;

    private LiveData<List<AppStock>> mAppStockObservable;

    private MutableLiveData<FetchStatus> mFetchStatus = new MutableLiveData<FetchStatus>();


    private Handler mHandler;

    HandlerThread mHandlerThread ;
    ExecutorService mExecutor = Executors.newCachedThreadPool();



    Runnable mStockUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"RefreshingSTocks");
            List<AppStock> appStocks = null;

            try {
                appStocks = mLocalDataSource.getAllStocks();
                if(appStocks != null && appStocks.size() >0 ) {
                    mRemoteDataSource.getRemoteStocks(appStocks);
                    for (AppStock stock : appStocks) {
                        mLocalDataSource.insertorUpdate(stock);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                //Ignore
            }
        }
    };


    @Override
    public void stopSync(){
        Log.d(TAG,"PauseRefresh");
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void initStatus() {
        mFetchStatus.postValue(FetchStatus.INIT);
    }

    @Override
    public void clearHandlerThread() {
        mHandlerThread.quitSafely();
        mHandlerThread = null;
    }

    private void initHandlerThread(){
        mHandlerThread = new HandlerThread("DBUpdateHandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }


    private StockRepository(LocalDataSource localDataSource,
                             RemoteDataSource remoteDataSource){
        Log.d(TAG,"InitCalled");
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
        mAppStockObservable = mLocalDataSource.observeStocks();
        initHandlerThread();


    }

    @Override
    public void startSync(){
        if(mHandlerThread == null){
            initHandlerThread();
        }
        mHandler.post( new Runnable() {
            public void run() {
                mExecutor.submit(mStockUpdateRunnable);
                mHandler.postDelayed(this, 5000);//run after every 5 seconds
            }
        });
    }

    public static DataRepository getInstance(@NonNull LocalDataSource localDataSource,
                                             @NonNull RemoteDataSource remoteDataSource) {
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
        mFetchStatus.postValue(FetchStatus.FETCHING);
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG,"Searching For Stock " + symbol);
                    AppStock stock = null;
                    stock = mRemoteDataSource.getStockBySymbol(symbol);
                    if(stock != null){
                        mLocalDataSource.insertorUpdate(stock);
                        mFetchStatus.postValue(FetchStatus.STOCK_FOUND);
                    }
                    else{
                        mFetchStatus.postValue(FetchStatus.STOCK_NOT_FOUND);
                    }
                } catch (Exception e) {
                    mFetchStatus.postValue(FetchStatus.FETCH_ERROR);
                    e.printStackTrace();
                }


            }
        });

    }

    @Override
    public void deleteBySymbol(String symbol) {

    }

    @Override
    public void refreshStocks() {
        mExecutor.submit(mStockUpdateRunnable);
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
