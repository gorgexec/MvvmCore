package com.gorgexec.mvvmcore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.gorgexec.mvvmcore.app.AppCore;
import com.gorgexec.mvvmcore.app.AppCoreConfig;
import com.gorgexec.mvvmcore.dagger.ActivityCoreComponent;
import com.gorgexec.mvvmcore.dagger.AppCoreComponent;
import com.gorgexec.mvvmcore.fragment.FragmentCore;
import com.gorgexec.mvvmcore.notification.ILocalNotificationHandler;
import com.gorgexec.mvvmcore.viewModel.ViewModelCore;

import java.util.Map;


public abstract class ActivityCore<TModel extends ViewModelCore> extends AppCompatActivity {

    private TModel model;

    protected int navHostId;

    protected ActivityResultDispatcher activityResultDispatcher;

    protected ViewModelNotificationDispatcher modelNotificationDispatcher;

    public TModel model() {
        return model;
    }

    public AppCoreComponent getAppComponent() {
        return ((AppCore) this.getApplicationContext()).getAppComponent();
    }

    public ActivityCoreComponent getActivityComponent() {
        return ((AppCore) this.getApplicationContext()).getActivityComponent();
    }

    public AppCoreConfig appConfig() {
        return getAppComponent().appConfig();
    }

    protected void prepareModel(Class<TModel> modelClass) {
        model = new ViewModelProvider(this, getActivityComponent().viewModels()).get(modelClass);
        model.setAppCoreConfig(appConfig());
        modelNotificationDispatcher = new ViewModelNotificationDispatcher(getActivityComponent().notificationHandlers(), this, this, model);
    }

    protected void setContentView(int layoutId, Class<TModel> modelClass) {
        prepareModel(modelClass);
        setContentView(layoutId);
    }

    protected void setContentView(@LayoutRes int layoutId, @IdRes int navHostId, Class<TModel> modelClass) {
        this.navHostId = navHostId;
        prepareModel(modelClass);
        setContentView(layoutId);
    }

    protected void setContentView(@LayoutRes int layoutId, @IdRes int navHostId) {
        this.navHostId = navHostId;
        setContentView(layoutId);
    }

    @Override
    protected void onResume() {
        if (model != null) {
            modelNotificationDispatcher.subscribe();
            model.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (model != null) {
            model.onPause();
            modelNotificationDispatcher.unsubscribe();
        }
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        activityResultDispatcher = new ActivityResultDispatcher(getActivityComponent().activityResultHandlers());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (model != null) {
            modelNotificationDispatcher.dispose();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        activityResultDispatcher.dispatch(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected <T> void subscribeNotification(Class<T> notificationClass, ILocalNotificationHandler<T> handler) {
        modelNotificationDispatcher.addHandler(notificationClass, handler);
    }

    @Nullable
    public NavController nav() {
        return navHostId != 0 ? Navigation.findNavController(this, navHostId) : null;
    }

    @Nullable
    public Fragment getCurrentDestinationFragment() {
        Fragment res = null;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(navHostId);
        if (navHostFragment != null) {
            res = navHostFragment.getChildFragmentManager().getFragments().get(0);
        }
        return res;
    }

    public void showActivityAndFinishCurrent(Class activityClass) {
        showActivity(activityClass, null);
        finish();
    }

    public void showActivityAndFinishCurrent(Class activityClass, Map<String, String> params) {
        showActivity(activityClass, params);
        finish();
    }

    public void showActivity(Class activityClass) {
        showActivity(activityClass, null);
    }

    public void showActivity(Class activityClass, Map<String, String> params) {

        Intent intent = new Intent(this, activityClass);

        if (params != null) {

            for (Map.Entry<String, String> entry : params.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        startActivity(intent);
    }

    public void showKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
    }


    @Nullable
    public <T> T findImplementationOf(Class<T> clazz) {
        T res = findImplementationOf(model(), clazz);
        if (res == null) {
            Fragment f = getCurrentDestinationFragment();
            if (f instanceof FragmentCore) {
                FragmentCore fragmentCore = (FragmentCore) f;
                res = findImplementationOf(fragmentCore.model(), clazz);
            }
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <T,R> R findImplementationOf(T obj, Class<R> clazz) {
        try {
            return obj.getClass().asSubclass(clazz) != null ? (R)obj : null;
        } catch (Exception e) {
            return null;
        }
    }
}
