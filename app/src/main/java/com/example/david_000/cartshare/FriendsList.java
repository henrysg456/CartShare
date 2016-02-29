package com.example.david_000.cartshare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david_000 on 2/12/2016.
 */
public class FriendsList extends AppCompatActivity
{
    private ArrayList<String> array = new ArrayList<String>();
    private ListView listView;
    private String fn = "";
    private String cUser;
    private String str="";
    private ArrayList<String> list = new ArrayList<String>();
    private boolean flag=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist);

        list = getIntent().getStringArrayListExtra("key");

        // Locate the current user
        ParseUser currentUser = ParseUser.getCurrentUser();
        cUser = currentUser.getEmail().toString();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Friends List");

        refreshPostList();

        listView = (ListView) findViewById(R.id.friends);
        listView.setAdapter(new MyListAdapter(this, R.layout.list_friend, R.id.textView, array));

        findViewById(R.id.cancelFriendlist).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the home page activity
                startActivity(new Intent(FriendsList.this, HomePageActivity.class));
            }  //end onClick
        });
    }  //end onCreate

    private void refreshPostList() {
        array.clear();

        for(String one : list) {
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereEqualTo("email", one.toString());

            try {
                List<ParseUser> results = userQuery.find();
                if (results.size() > 0) {
                    str = results.get(0).get("firstName").toString();
                    str += " ";
                    str += results.get(0).get("lastName").toString();
                    array.add(str);
                }  //end if
            } catch (ParseException e) {
                e.printStackTrace();
            }  //end try-catch
        }  //end for loop
    }  //end refreshPostList

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friendlist, menu);
        return true;
    }  //end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_addfriend:
            {
                final ProgressDialog d = new ProgressDialog(FriendsList.this);
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsList.this);
                builder.setTitle("Enter User Email Address!");
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }  //end onClick
                });  //end button

                builder.setNegativeButton("Add          ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fn = input.getText().toString();

                        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                        userQuery.whereEqualTo("email", fn);

                        try {
                            List<ParseUser> results = userQuery.find();
                            if (results.size() == 0) {
                                flag=true;
                            }  //end if
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }  //end try-catch

                        if (fn.isEmpty())
                            Toast.makeText(FriendsList.this, "Your input is empty!", Toast.LENGTH_LONG).show();
                        else if (list.contains(fn))
                            Toast.makeText(FriendsList.this, "User is already your friend!", Toast.LENGTH_LONG).show();
                        else if (flag)
                            Toast.makeText(FriendsList.this, "User does not exist!", Toast.LENGTH_LONG).show();
                        else {
                            final ParseObject obj = new ParseObject("friends");
                            ParseQuery query = new ParseQuery("User");
                            query.whereEqualTo("email", fn);
                            obj.put("user", cUser);
                            obj.put("friend", fn);
                            obj.saveInBackground(new SaveCallback() {
                                public void done(com.parse.ParseException e) {
                                    if (e == null) {
                                        // Adding was successful!
                                        d.setTitle("Succesfully Added!");
                                        d.show();
                                        list.add(fn);
                                        Intent intent = new Intent(FriendsList.this, FriendsList.class);
                                        intent.putStringArrayListExtra("key", list);
                                        startActivity(intent);
                                    } else {
                                        // Add failed. Inspect e for details.
                                        d.setTitle("Error in adding the user.");
                                        d.show();
                                    }  //end else
                                }  //end done
                            });  //end saveInBackGroup
                        }  //end else
                    }  //end onClick
                });  //end button

                builder.show();
                break;
            }  //end case
        }  //end switch case
        return super.onOptionsItemSelected(item);
    }  //end onOptionsItemSelected
}  //end FriendList class
