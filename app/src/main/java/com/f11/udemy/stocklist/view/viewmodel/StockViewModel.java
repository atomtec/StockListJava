package com.f11.udemy.stocklist.view.viewmodel;

import com.f11.udemy.stocklist.data.model.AppStock;
import com.f11.udemy.stocklist.data.model.FetchStatus;
import com.f11.udemy.stocklist.data.repo.DataRepository;
import com.f11.udemy.stocklist.data.repo.StockRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class StockViewModel extends ViewModel {

    private DataRepository mRepo;
    private LiveData<List<AppStock>> mAllStocks;
    private LiveData<FetchStatus> mFetchData;
    private static final String TAG = StockViewModel.class.getSimpleName();

    public StockViewModel (DataRepository repo) {
        super();
        mRepo = repo;

        mAllStocks = mRepo.observeStocks();
        mFetchData = mRepo.observeFetchStatus();
        mRepo.refreshStocks();

    }

    public void initFetchStatus(){
        mRepo.initStatus();
    }

    public void startSync(){
        mRepo.startSync();
    }

    public void stopSync(){
        mRepo.stopSync();
    }

    public LiveData<List<AppStock>> getAllStocks() { return mAllStocks; }
    public LiveData<FetchStatus> getFetchData(){ return mFetchData;}

    public void searchAndStock(String symbol){
        mRepo.searchAndAddStock(symbol);
    }

}
