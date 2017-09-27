package com.example.priya.socialnetworkingdemo.ui.init;

import android.app.Application;

import com.twitter.sdk.android.core.Twitter;

/**
 * Created by priya on 26/9/17.
 */

public class TwitterInit  extends Application{
    public void onCreate() {

        Twitter.initialize(this);
    }
}
