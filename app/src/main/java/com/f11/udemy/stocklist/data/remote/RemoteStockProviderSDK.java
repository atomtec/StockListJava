package com.f11.udemy.stocklist.data.remote;



import com.f11.udemy.stocklist.data.model.AppStock;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

/**
 * This provider is backed by SDK , we can have many other
 * providers backed by direct API for example
 */
public class RemoteStockProviderSDK  {


    public static List<AppStock> getRemoteStocks(List<AppStock> stocks) throws IOException {

        String[] symbolArray = new String[stocks.size()];
        Map<String, AppStock> appStockMap = new HashMap<>();
        int i = 0;
        for (AppStock stock : stocks) {
            symbolArray[i++] = stock.getSymbol();
            appStockMap.put(stock.getSymbol(), stock);// for sending back
        }
        Map<String, Stock> quotes = YahooFinance.get(symbolArray);
        for (String symbol : symbolArray) {
            Stock stock = quotes.get(symbol);
            if(stock != null) {
                StockQuote quote = stock.getQuote();
                if (quote != null && quote.getPrice() != null) {
                    float price = quote.getPrice().floatValue();
                    float change = quote.getChange().floatValue();
                    float percentChange = quote.getChangeInPercent().floatValue();
                    AppStock appStock = appStockMap.get(symbol);
                    appStock.setChange(percentChange);
                    appStock.setPrice(price);
                    appStock.setAbsolutechange(change);
                    appStock.setStockname(stock.getName());
                }
            }
            else
            {
                //clear the invalid stocks
                AppStock invalid = appStockMap.get(symbol);
                stocks.remove(invalid);
            }

        }
        return stocks;
    }


    public static AppStock getStockBySymbol(String symbol) throws IOException {
       Stock stock = YahooFinance.get(symbol);
       AppStock appStock = null;
       if(stock != null){
           StockQuote quote = stock.getQuote();
           if (quote != null && quote.getPrice() != null){
               float price = quote.getPrice().floatValue();
               float change = quote.getChange().floatValue();
               float percentChange = quote.getChangeInPercent().floatValue();
               String name = stock.getName();
               appStock = new AppStock(symbol,price,percentChange,change,name);
           }
       }
       return appStock;
    }
}



