package com.gorgexec.mvvmcore.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

import com.gorgexec.mvvmcore.activity.ActivityCore;
import com.gorgexec.mvvmcore.activity.ViewModelNotificationDispatcher;
import com.gorgexec.mvvmcore.annotations.ViewModelOwner;
import com.gorgexec.mvvmcore.notification.ILocalNotificationHandler;
import com.gorgexec.mvvmcore.viewModel.ViewModelCore;

public abstract class FragmentCore<TModel extends ViewModelCore> extends Fragment {

    protected ViewModelNotificationDispatcher modelNotificationDispatcher;

    private TModel model;
    protected int layoutRes;
    protected int destinationId;
    protected Class<TModel> modelClass;

    public FragmentCore(@LayoutRes int layoutRes, @IdRes int destinationId, @NonNull Class<TModel> modelClass) {
        this.layoutRes = layoutRes;
        this.destinationId = destinationId;
        this.modelClass = modelClass;
    }

    public FragmentCore(@LayoutRes int layoutRes, @NonNull Class<TModel> modelClass) {
        this.layoutRes = layoutRes;
        this.modelClass = modelClass;
    }

    public FragmentCore(@LayoutRes int layoutRes) {
        this.layoutRes = layoutRes;
    }

    public FragmentCore(@LayoutRes int layoutRes, @IdRes int destinationId) {
        this.layoutRes = layoutRes;
        this.destinationId = destinationId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(layoutRes, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareModel(modelClass);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(model != null) {
            modelNotificationDispatcher.subscribe();
            model.onResume();
        }

    }

    @Override
    public void onPause() {
        if(model != null) {
            model.onPause();
            modelNotificationDispatcher.unsubscribe();
        }
        super.onPause();
    }

    @Override
    public void onDetach() {
        if(model != null) {
            modelNotificationDispatcher.dispose();
        }
        super.onDetach();
    }

    public TModel model(){
        return model;
    }

    protected ActivityCore activityCore(){
        return (ActivityCore)requireActivity();
    }

    protected void prepareModel(Class<TModel> modelClass) {
        if(modelClass!=null) {
            boolean isModelOwner = getClass().getAnnotation(ViewModelOwner.class) != null;
            model = new ViewModelProvider(isModelOwner ? this : activityCore(), activityCore().getActivityComponent().viewModels()).get(modelClass);
            model.setAppCoreConfig(activityCore().appConfig());
            modelNotificationDispatcher = new ViewModelNotificationDispatcher(activityCore().getActivityComponent().notificationHandlers(), this, activityCore(), model);
        }
    }

    protected NavController nav(){
        return NavHostFragment.findNavController(this);
    }

    protected void navigate(NavDirections navDirections) {
        navigate(navDirections, null);
    }

    protected void navigate(NavDirections navDirections, FragmentNavigator.Extras extras) {
        NavController navController = nav();
        if (navController != null
                && destinationId != 0
                && navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() == destinationId) {
            if(extras!=null) {
                navController.navigate(navDirections, extras);
            }
            else{
                navController.navigate(navDirections);
            }
        }
    }

    protected <T> void subscribeNotification(Class<T> notificationClass, ILocalNotificationHandler<T> handler) {
        if(modelNotificationDispatcher!=null) {
            modelNotificationDispatcher.addHandler(notificationClass, handler);
        }
    }
}
