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
import android.widget.AdapterView;
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
    private ArrayList<String> array = new ArrayList<String>(); //this array holds friend names
    private ArrayList<String> array2 = new ArrayList<String>(); //this array holds friend IDs
    private ListView listView;
    private String fn = "";
    private String cUser, cUserId;
    private String str="";
    private String groupId, groupName;
    private ArrayList<String> list = new ArrayList<String>();
    private boolean flag=false;
    private boolean already=true, full=true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist);

        list = getIntent().getStringArrayListExtra("key");

        // Locate the current user
        ParseUser currentUser = ParseUser.getCurrentUser();
        cUser = currentUser.getEmail().toString();
        cUserId = currentUser.getObjectId().toString();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Friends List");

        refreshPostList();

        listView = (ListView) findViewById(R.id.friends);
        listView.setAdapter(new MyListAdapter(this, R.layout.list_friend, R.id.textView, array));

        if(getIntent().getStringExtra("groupId") != null)
        {
            groupId = getIntent().getStringExtra("groupId");
            groupName = getIntent().getStringExtra("groupName");

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    final int position = arg2;

                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendsList.this);
                    builder.setMessage("Do you want to invite " + array.get(arg2).toString() + " to " + groupName + "?");

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
                            //str stores friend's id
                            String friend = array2.get(position);

                            ParseQuery<ParseObject> pQuery = ParseQuery.getQuery("lists");
                            pQuery.whereEqualTo("objectId", groupId);

                            try {
                                List<ParseObject> results = pQuery.find();
                                int size = results.get(0).getList("authors").size();

                                //create array to hold group of authors
                                String[] authors = new String[10];

                                //make sure it is not empty
                                if (results.size() > 0) {
                                    //make sure array isn't full (if last one is null)
                                    if (results.get(0).getList("authors").get(9).toString() == "null") {
                                        //save authors in group to local array
                                        for (int j = 0; j < size; j++) {
                                            if (results.get(0).getList("authors").get(j).toString() != "null")
                                                authors[j] = results.get(0).getList("authors").get(j).toString();
                                            else
                                                authors[j] = null;
                                        }  //end for loop

                                        //make sure this friend is not a member of the group
                                        for (int i = 0; i < size; i++) {
                                            if (authors[i] != null) {
                                                if (authors[i].equals(friend))
                                                    already = false;
                                            }  //end if
                                        }  //end for loop

                                        //check if user is already in the group
                                        if (already == false)
                                            Toast.makeText(getApplicationContext(), array.get(position).toString() + " is already a member of this group.", Toast.LENGTH_SHORT).show();
                                    }  //end if
                                    else {
                                        full = false;
                                        Toast.makeText(getApplicationContext(), "Your group is full!", Toast.LENGTH_SHORT).show();
                                    }  //end if-else
                                }  //end if
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }  //end try-catch

                            //if  this user is not already on the list AND the list is not full
                            if (already == true && full == true) {
                                ParseQuery<ParseObject> pQueryInvite = ParseQuery.getQuery("notification");
                                pQueryInvite.whereEqualTo("group", groupId);
                                pQueryInvite.whereEqualTo("user", cUserId);
                                pQueryInvite.whereEqualTo("friend", friend);

                                try {
                                    List<ParseObject> result = pQueryInvite.find();

                                    //check if you already have invited this user to the group
                                    if (result.size() > 0)
                                        Toast.makeText(getApplicationContext(), "You have already sent invitation to " + array.get(position).toString() + "!", Toast.LENGTH_SHORT).show();

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }  //end try-catch
                            }  //end if
                        }  //end onClick
                    });  //end button

                    builder.show();
                }  //end setOnItemClickListener
            });  //end setOnItemClickListener
        }  //end if
    }  //end onCreate

    private void refreshPostList()
    {
        array.clear();
        array2.clear();

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

                    str = results.get(0).getObjectId().toString();
                    array2.add(str);
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
