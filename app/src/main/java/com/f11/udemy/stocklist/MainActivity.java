package com.f11.udemy.stocklist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.f11.udemy.stocklist.view.ui.AddStockDialogFragment;
import com.f11.udemy.stocklist.view.ui.StockDisplayFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {


    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragment = getSupportFragmentManager()
                .findFragmentById(R.id.stock_list_fragment);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkUp()) {
                    new AddStockDialogFragment().show(getSupportFragmentManager(),
                            "StockDialogFragment");
                }
                //TODO add cache and retry
                else{
                    Snackbar.make(view, R.string.internet_down_message, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public void addStock(String symbol){
        if (mFragment != null){
            StockDisplayFragment.class.cast(mFragment).addStock(symbol);
        }


    }
    private boolean isNetworkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}