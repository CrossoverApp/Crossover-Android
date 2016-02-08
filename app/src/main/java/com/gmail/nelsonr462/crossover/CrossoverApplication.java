package com.gmail.nelsonr462.crossover;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseConfig;
import com.parse.ParseInstallation;

public class CrossoverApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        // [Optional] Power your app with Local Datastore. For more info, go to
        // https://parse.com/docs/android/guide#local-datastore
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }
}
