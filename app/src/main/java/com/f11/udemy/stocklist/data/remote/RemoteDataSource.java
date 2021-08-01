package com.f11.udemy.stocklist.data.remote;

import com.f11.udemy.stocklist.data.model.AppStock;

import java.io.IOException;
import java.util.List;

public interface RemoteDataSource {

    public List<AppStock> getRemoteStocks(List<AppStock> stocks) throws IOException;

    public AppStock getStockBySymbol(String symbol) throws IOException;

}
