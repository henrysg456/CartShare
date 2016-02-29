package com.example.david_000.cartshare;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by david_000 on 9/1/2015.
 */
public class ParseInitialize extends Application {

    public void onCreate(){
        super.onCreate();
        Parse.initialize(this, "4sieY0gkfvi8c9iCSYTlgJp5k9Lo8aPC65HplOjZ", "YV1VFrW7haq37gqTYBpdtzr40ZHX8cbXJ5WRQllj");
    }
}
