package com.f11.udemy.stocklist.view.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f11.udemy.stocklist.R;
import com.f11.udemy.stocklist.data.local.LocalDataSource;
import com.f11.udemy.stocklist.data.local.db.StockDataBase;
import com.f11.udemy.stocklist.data.model.AppStock;
import com.f11.udemy.stocklist.data.remote.RemoteStockProviderSDK;
import com.f11.udemy.stocklist.view.adapter.StockListAdapter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StockDisplayFragment extends Fragment {

    RecyclerView mStockRecyclerView;
    StockListAdapter mAdapter;
    View mProgressBar ;
    private String mSymbol;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LocalDataSource mLocalDB = null;
    ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    Runnable mStockUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                List<AppStock> remoteStocks = RemoteStockProviderSDK.getRemoteStocks
                        (mLocalDB.getAllStocks());
                for(AppStock stock: remoteStocks) {
                    mLocalDB.insertorUpdate(stock);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setStocks(mLocalDB.getAllStocks());
                }
            });
        }

    };

    Runnable mSearchStockRunnable = new Runnable() {
        AppStock stock = null;
        @Override
        public void run() {
            try {
                stock = RemoteStockProviderSDK.getStockBySymbol(mSymbol);
                if(stock != null)
                    mLocalDB.insertorUpdate(stock);
            } catch (IOException e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(stock != null) {
                        mAdapter.setStocks(mLocalDB.getAllStocks());
                        Toast.makeText(getContext(),"Stock Added",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getContext(),"Stock CouldNot be Found",Toast.LENGTH_LONG).show();
                    }
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
    };


    private static final String TAG = StockDisplayFragment.class.getSimpleName();


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        mLocalDB = StockDataBase.getInstance(getActivity().getApplicationContext());

        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStockRecyclerView = view.findViewById(R.id.recycler_view);
        mProgressBar = view.findViewById(R.id.llProgressBar);
        mAdapter = new StockListAdapter(getContext());
        mStockRecyclerView.setAdapter(mAdapter);
        mStockRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post( new Runnable() {
            public void run() {
                mExecutor.submit(mStockUpdateRunnable);
                mHandler.postDelayed(this, 5000);//run after every 5 seconds
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
        AppCompatToggleButton toggleMenu = menu.findItem(R.id.switchId)
                .getActionView().findViewById(R.id.displaymodeswitch);
        toggleMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAdapter.toggleMode(isChecked);
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }



    public void addStock(String symbol){
        mSymbol = symbol;
        mProgressBar.setVisibility(View.VISIBLE);
        mExecutor.submit(mSearchStockRunnable);
    }

}