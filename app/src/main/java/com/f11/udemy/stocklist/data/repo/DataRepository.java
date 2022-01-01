package com.f11.udemy.stocklist.data.repo;

import com.f11.udemy.stocklist.data.model.AppStock;
import com.f11.udemy.stocklist.data.model.FetchStatus;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface DataRepository {

    public void searchAndAddStock(String symbol);
    public void deleteBySymbol(String symbol);
    public void refreshStocks();
    public LiveData<List<AppStock>> observeStocks();
    public LiveData<FetchStatus> observeFetchStatus();
    public void startSync();
    public void stopSync();
    public void initStatus();
    void clearHandlerThread();
}
