package com.gorgexec.mvvmcore.activity;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ActivityProvider {

    private final Object monitor = new Object();
    private WeakReference<Activity> activity;

    @Inject
    public ActivityProvider() {
    }

    @Nullable
    public Activity currentActivity() {
        return activity.get();
    }

    @NonNull
    public Activity requireActivity() {
        try {
            synchronized (monitor) {
                while (activity.get() == null) {
                    monitor.wait();
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return activity.get();
    }

    public void setActivity(@NonNull Activity activity) {
        synchronized (monitor) {
            this.activity = new WeakReference<>(activity);
            monitor.notify();
        }
    }

    public synchronized void removeActivity(Activity activity){
        Activity a = this.activity.get();
        if(a == activity){
            this.activity.clear();
        }
    }


}
