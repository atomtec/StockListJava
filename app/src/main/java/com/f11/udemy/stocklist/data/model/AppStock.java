package com.f11.udemy.stocklist.data.model;


import androidx.annotation.NonNull;



public class AppStock {

    @NonNull
    private String mSymbol;
    private float mPrice;
    private float mChange;
    private float mAbsolutechange;
    private String mStockname;



    public AppStock(@NonNull String symbol, @NonNull float price ,
                    @NonNull float change , @NonNull float absolutechange,
                    @NonNull String stockname) {
        this.mSymbol = symbol;
        this.mPrice = price;
        this.mChange = change;
        this.mAbsolutechange = absolutechange;
        this.mStockname = stockname;
    }

    public AppStock() {

    }



    @NonNull
    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(@NonNull String symbol) {
        this.mSymbol = symbol;
    }

    @NonNull
    public float getPrice() {
        return mPrice;
    }

    public void setPrice(@NonNull float price) {
        this.mPrice = price;
    }

    @NonNull
    public float getChange() {
        return mChange;
    }

    public void setChange(@NonNull  float change) {
        this.mChange = change;
    }

    @NonNull
    public float getAbsolutechange() {
        return mAbsolutechange;
    }

    public void setAbsolutechange(@NonNull float absolutechange) {
        this.mAbsolutechange = absolutechange;
    }

    @NonNull
    public String getStockname() {
        return mStockname;
    }

    public void setStockname(@NonNull String stockname) {
        this.mStockname = stockname;
    }

}
