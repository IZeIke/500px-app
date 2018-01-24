package com.example.haritmoolphunt.liveat500px;

import android.app.Application;

import com.example.haritmoolphunt.liveat500px.manager.Contextor;


/**
 * Created by Harit Moolphunt on 20/10/2560.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Contextor.getInstance().init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
