package com.gorgexec.mvvmcore.activity;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.gorgexec.mvvmcore.viewModel.ViewModelCore;


public abstract class BindableActivityCore<TModel extends ViewModelCore, TBinding extends ViewDataBinding> extends ActivityCore<TModel> {

    private TBinding binding;

    protected TBinding binding(){
        return binding;
    }

    protected TBinding bind(@LayoutRes int layoutId, Class<TModel> modelClass) {
        prepareModel(modelClass);
        binding = DataBindingUtil.setContentView(this, layoutId);
        binding.setLifecycleOwner(this);
        binding.setVariable(appConfig().getDefaultModelBR(), model());
        return binding;
    }

    protected TBinding bind(@LayoutRes int layoutId, @IdRes int navHostId, Class<TModel> modelClass) {
        this.navHostId = navHostId;
        return bind(layoutId,  modelClass);
    }
}
