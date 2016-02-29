package com.example.david_000.cartshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

/**
 * Created by david_000 on 9/1/2015.
 */
public class CheckingStatus extends Activity {

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null)
        {
           startActivity(new Intent(this, HomePageActivity.class));
        }  //end if
        else
            startActivity(new Intent(this, SignInActivity.class));
    }
}  //end CheckingStatus
