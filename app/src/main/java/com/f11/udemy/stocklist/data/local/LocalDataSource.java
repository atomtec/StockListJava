package com.f11.udemy.stocklist.data.local;

import com.f11.udemy.stocklist.data.model.AppStock;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface LocalDataSource {

    public void insertorUpdate(AppStock stock);
    public List<AppStock> getAllStocks();
    public void deleteBySymbol(String symbol);
    public LiveData<List<AppStock>> observeStocks();

}
