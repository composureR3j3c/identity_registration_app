package com.boabeta.idregtes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

public class MyApp extends Application implements ViewModelStoreOwner {

    private ViewModelStore appViewModelStore;

    @Override
    public void onCreate() {
        super.onCreate();
        appViewModelStore = new ViewModelStore();
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return appViewModelStore;
    }
}
