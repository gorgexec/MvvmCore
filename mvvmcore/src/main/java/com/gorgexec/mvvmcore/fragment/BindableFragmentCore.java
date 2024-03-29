package com.gorgexec.mvvmcore.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.gorgexec.mvvmcore.viewModel.ViewModelCore;

public abstract class BindableFragmentCore<TModel extends ViewModelCore, TBinding extends ViewDataBinding> extends FragmentCore<TModel> {

    private TBinding binding;

    protected TBinding binding(){
        return binding;
    }

    public BindableFragmentCore(@LayoutRes int layoutRes, @IdRes int destinationId, @NonNull Class<TModel> modelClass) {
        super(layoutRes, destinationId, modelClass);
    }

    public BindableFragmentCore(@LayoutRes int layoutRes, @NonNull Class<TModel> modelClass) {
       super(layoutRes, modelClass);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.setVariable(activityCore().appConfig().getDefaultModelBR(), model());
        onBindingReady();
    }

    protected void onBindingReady(){}

}
