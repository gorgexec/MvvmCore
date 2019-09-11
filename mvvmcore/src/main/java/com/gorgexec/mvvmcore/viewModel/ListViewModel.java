package com.gorgexec.mvvmcore.viewModel;

import android.os.Handler;

import androidx.databinding.Bindable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ListViewModel<T> extends ViewModelCore {

    private final Handler handler = new Handler();

    private List<T> items = new ArrayList<>();

    @Bindable
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
        notifyPropertyChanged(appCoreConfig.getDefaultItemsBR());
    }

    private T itemToAdd;

    @Bindable
    public T getItemToAdd() {
        return itemToAdd;
    }

    public void setItemToAdd(T itemToAdd) {
        this.itemToAdd = itemToAdd;
        notifyPropertyChanged(appCoreConfig.getDefaultItemsToAddBR());
    }

    private T itemToUpdate;

    @Bindable
    public T getItemToUpdate() {
        return itemToUpdate;
    }

    public void setItemToUpdate(T itemToUpdate) {
        this.itemToUpdate = itemToUpdate;
        notifyPropertyChanged(appCoreConfig.getDefaultItemsToUpdateBR());
    }

    private BlockingQueue<T> itemToAddOrUpdate = new LinkedBlockingQueue<>(50);

    @Bindable
    public  List<T> getItemToAddOrUpdate() {
        List<T> res = new ArrayList<>();
        itemToAddOrUpdate.drainTo(res);
        return res;
    }

    public  void setItemToAddOrUpdate(T itemToAddOrUpdate) {
        try {
            this.itemToAddOrUpdate.put(itemToAddOrUpdate);
            notifyPropertyChanged(appCoreConfig.getDefaultItemToAddOrUpdateBR());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private T itemToRemove;

    @Bindable
    public T getItemToRemove() {
        return itemToRemove;
    }

    public void setItemToRemove(T itemToRemove) {
        this.itemToRemove = itemToRemove;
        notifyPropertyChanged(appCoreConfig.getDefaultItemToRemoveBR());
    }

    private String emptyText;

    @Bindable
    public String getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
        notifyPropertyChanged(appCoreConfig.getDefaultEmptyListTextBR());
    }
}
