package com.example.david_000.cartshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by david_000 on 2/16/2016.
 */
public class ViewListActivity extends AppCompatActivity
{
    ActionBar.Tab shoppingTab, couponTab , receiptTab;
    Fragment tab1 = new TabFragment1();
    Fragment tab2 = new TabFragment2();
    Fragment tab3 = new TabFragment3();
    String groupId = "";
    String groupName = "";
    private String postTitle="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        Intent intent = this.getIntent();

        if (intent.getExtras() != null) {
            groupId = intent.getStringExtra("id");
            groupName = intent.getStringExtra("name");
        }  //end if

        ActionBar actionBar = getSupportActionBar();
        ActionBar.Tab shoppingTab, couponTab , receiptTab;

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        shoppingTab = actionBar.newTab().setText("Items");
        couponTab = actionBar.newTab().setText("Coupons");
        receiptTab = actionBar.newTab().setText("Receipts");

        Bundle bundle=new Bundle();
        bundle.putString("id", groupId);
        bundle.putString("name", groupName);
        tab1.setArguments(bundle);

        shoppingTab.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.replace(R.id.viewlist, tab1);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.remove(tab1);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        });

        couponTab.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.replace(R.id.viewlist, tab2);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.remove(tab2);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        });

        receiptTab.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.replace(R.id.viewlist, tab3);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.remove(tab3);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        });

        actionBar.addTab(shoppingTab);
        actionBar.addTab(couponTab);
        actionBar.addTab(receiptTab);

    }  //end onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_list, menu);
        return true;
    }  //end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (id) {
            case R.id.action_new:
            {
                //final ProgressDialog d = new ProgressDialog(ViewListActivity.this);
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewListActivity.this);
                builder.setTitle("Enter New Item Name!");
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
                        postTitle = input.getText().toString();
                        postTitle = postTitle.trim();

                        if (!postTitle.isEmpty()) {
                            // create new post
                            ParseObject post = new ParseObject("item");
                            post.put("title", postTitle);
                            post.put("author", ParseUser.getCurrentUser().getObjectId());
                            post.put("groupId", groupId);
                            setProgressBarIndeterminateVisibility(true);
                            post.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    setProgressBarIndeterminateVisibility(false);
                                    if (e == null) {
                                        // Saved successfully.
                                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(ViewListActivity.this, ViewListActivity.class);
                                        intent.putExtra("id", groupId);
                                        intent.putExtra("name", groupName);

                                        //maybe add objectID, we'll see
                                        startActivity(intent);
                                    } else {
                                        // The save failed.
                                        Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                                        Log.d(getClass().getSimpleName(), "User update error: " + e);
                                    }  //end else
                                }  //end done
                            });  //end query
                        } //end if
                        else {
                            Toast.makeText(ViewListActivity.this, "Your input is empty!", Toast.LENGTH_LONG).show();
                        }  //end else
                    }  //end onClick
                });  //end button

                builder.show();

                break;
            }
        }  //end switch case

        return super.onOptionsItemSelected(item);
    }  //end onOptionsItemSelected
}  //end class
