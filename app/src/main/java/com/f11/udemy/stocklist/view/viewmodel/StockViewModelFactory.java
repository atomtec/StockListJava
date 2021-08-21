package com.f11.udemy.stocklist.view.viewmodel;



import com.f11.udemy.stocklist.data.repo.DataRepository;
import com.f11.udemy.stocklist.data.repo.StockRepository;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class StockViewModelFactory implements ViewModelProvider.Factory {
    private DataRepository mRepo;

    public StockViewModelFactory(DataRepository repo) {
       mRepo = repo;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new StockViewModel(mRepo);
    }
}

