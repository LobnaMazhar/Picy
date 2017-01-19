package com.example.lobna.flickr;

import android.app.Application;

/**
 * Created by Lobna on 17-Jan-17.
 */

public class PiccyApplication extends Application {

    public static PiccyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static PiccyApplication getPiccyApp(){
        return instance;
    }
}
