package com.gorgexec.mvvmcore.viewModel;

import android.os.Handler;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gorgexec.mvvmcore.app.AppCoreConfig;
import com.gorgexec.mvvmcore.liveData.Event;

public abstract class ViewModelCore extends ViewModel implements Observable {

    private Handler handler = new Handler();

    private MutableLiveData<Event<Object>> notifications = new MutableLiveData<>();

    public MutableLiveData<Event<Object>> notifications() {
        return notifications;
    }

    public synchronized void notifyView(Object notification){
        handler.post(() -> notifications.setValue(Event.create(notification)));
    }

    protected AppCoreConfig appCoreConfig;

    public void setAppCoreConfig(AppCoreConfig appCoreConfig){
        if (this.appCoreConfig != null) return;
        this.appCoreConfig = appCoreConfig;
    }

    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    @Override
    public void addOnPropertyChangedCallback(
            OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(
            OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    /**
     * Notifies observers that all properties of this instance have changed.
     */
   protected void notifyChange() {
        callbacks.notifyCallbacks(this, 0, null);
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    protected void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }

    protected boolean inProgress;

    @Bindable
    public synchronized boolean isInProgress() {
        return inProgress;
    }

    public synchronized void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
        notifyPropertyChanged(appCoreConfig.getDefaultProgressBR());
    }


    public void onPause() {
    }

    public void onResume() {
    }
}
