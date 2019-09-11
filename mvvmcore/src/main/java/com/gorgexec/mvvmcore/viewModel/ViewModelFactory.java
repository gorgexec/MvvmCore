package com.gorgexec.mvvmcore.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.gorgexec.mvvmcore.common.MapValuesFactory;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

public class ViewModelFactory extends MapValuesFactory<Class<? extends ViewModel>, ViewModel> implements ViewModelProvider.Factory {

    @Inject
    public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
       super(creators);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) super.create(modelClass);
    }

}
