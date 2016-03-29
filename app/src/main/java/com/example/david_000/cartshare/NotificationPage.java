package com.example.david_000.cartshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david_000 on 3/28/2016.
 */
public class NotificationPage extends AppCompatActivity
{
    private String uID;
    private ListView listView;
    private ArrayList<String> array = new ArrayList<String>();
    private ArrayList<String> array2 = new ArrayList<String>(); //hold notification IDs

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        ParseUser currentUser = ParseUser.getCurrentUser();
        uID = currentUser.getObjectId().toString();

        listView = (ListView) findViewById(R.id.notif);

        ParseQuery<ParseObject> pQueryNotif = ParseQuery.getQuery("notification");
        pQueryNotif.whereEqualTo("friend", uID);

        try {
            List<ParseObject> result = pQueryNotif.find();

            //make sure result is not empty before accessing it
            if (result.size() > 0)
                for(int i=0; i<result.size(); i++)
                {
                    array.add(result.get(i).get("userName").toString() + " invites you to " + result.get(i).get("groupName") + ".");
                    array2.add(result.get(i).getObjectId().toString());
                }  //end for loop

        } catch (ParseException e) {
            e.printStackTrace();
        }  //end try-catch

        listView.setAdapter(new MyListAdapter(this, R.layout.list_friend, R.id.textView, array));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final int position = arg2;

                AlertDialog.Builder builder = new AlertDialog.Builder(NotificationPage.this);
                builder.setMessage("Do you want to accept this invitation " + "?");

                // Set up the buttons
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }  //end onClick
                });  //end button

                builder.setNeutralButton("Ignore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ParseQuery<ParseObject> pQueryNotification = ParseQuery.getQuery("notification");
                        pQueryNotification.whereEqualTo("objectId", array2.get(position));

                        try {
                            List<ParseObject> result = pQueryNotification.find();

                            //make sure result is not empty before accessing it
                            if (result.size() > 0) {
                                ParseObject.createWithoutData("notification", result.get(0).getObjectId()).deleteEventually();
                                SystemClock.sleep(250);
                                startActivity(new Intent(NotificationPage.this, NotificationPage.class));
                            }  //end if
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }  //end try-catch
                    }  //end onClick
                });  //end button

                builder.setNegativeButton("Accept          ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }  //end onClick
                });  //end button

                builder.show();
            }  //end onItemClick
        });  //end setOnItemClickListener
    }  //end onCreate
}  //end NotificationPage class
