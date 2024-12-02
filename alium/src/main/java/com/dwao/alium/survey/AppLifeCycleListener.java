package com.dwao.alium.survey;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

interface AppLifeCycleListener extends DefaultLifecycleObserver {
    @Override
    public void onCreate(@NonNull LifecycleOwner owner) ;
    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) ;
    @Override
    public void onResume(@NonNull LifecycleOwner owner) ;
    @Override
    public void onPause(@NonNull LifecycleOwner owner) ;
}
