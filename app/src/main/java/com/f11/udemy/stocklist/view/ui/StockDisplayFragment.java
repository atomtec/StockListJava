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

import com.f11.udemy.stocklist.R;
import com.f11.udemy.stocklist.data.local.db.room.LocalDataSourceImpl;
import com.f11.udemy.stocklist.data.model.AppStock;
import com.f11.udemy.stocklist.data.model.FetchStatus;
import com.f11.udemy.stocklist.data.remote.RemoteStockProviderSDK;
import com.f11.udemy.stocklist.data.repo.DataRepository;
import com.f11.udemy.stocklist.data.repo.StockRepository;
import com.f11.udemy.stocklist.view.adapter.StockListAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class StockDisplayFragment extends Fragment {

    RecyclerView mStockRecyclerView;
    StockListAdapter mAdapter;
    View mProgressBar ;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private DataRepository mRepo = null;
    ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    Runnable mStockUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mRepo.refreshStocks();
        }
    };

    private static final String TAG = StockDisplayFragment.class.getSimpleName();


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        mRepo = StockRepository.getInstance(LocalDataSourceImpl.getInstance(getActivity().getApplication()),
                RemoteStockProviderSDK.getInstance());


        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStockRecyclerView = view.findViewById(R.id.recycler_view);
        mProgressBar = view.findViewById(R.id.llProgressBar);
        mAdapter = new StockListAdapter(getContext());
        mStockRecyclerView.setAdapter(mAdapter);
        mStockRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRepo.observeStocks().observe(getViewLifecycleOwner(), new Observer<List<AppStock>>() {
            @Override
            public void onChanged(List<AppStock> appStocks) {
                mAdapter.setStocks(appStocks);
            }
        });
        mRepo.observeFetchStatus().observe(getViewLifecycleOwner(), new Observer<FetchStatus>() {
            @Override
            public void onChanged(FetchStatus fetchStatus) {
                switch (fetchStatus){
                    case FETCHING:
                        mProgressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(),"Fetching Stocks",Toast.LENGTH_LONG).show();
                        break;
                    case FETCH_ERROR:
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(),"Error Fetching Stocks",Toast.LENGTH_LONG).show();
                        break;
                    case STOCK_FOUND:
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(),"StockFound",Toast.LENGTH_LONG).show();
                        break;
                    case STOCK_NOT_FOUND:
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(),"Stock Not Found",Toast.LENGTH_LONG).show();
                        break;
                    default:
                        mProgressBar.setVisibility(View.GONE);
                        break;

                }
            }
        });
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



    public void addStock(final String symbol){

        mProgressBar.setVisibility(View.VISIBLE);
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mRepo.searchAndAddStock(symbol);
            }
        });
    }

}