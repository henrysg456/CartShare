package com.example.david_000.cartshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david_000 on 10/27/2015.
 * Home page after the user has logged in.
 */
public class HomePageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    FlyOutContainer root;
    private ArrayList<String> array = new ArrayList<String>();
    private ArrayList<String> arrayUserGroups = new ArrayList<String>();
    private TextView strName;
    private ParseFile profile;
    private String strTemp;
    private ImageView pic;
    private String mUser, uID;
    Button button;
    private List<item> iList;
    private ListView listView;
    private MyListAdapter myListAdapter;
    private String postTitle;
    RelativeLayout notificationCount;
    private TextView numNotif;
    private int numNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.root = (FlyOutContainer) this.getLayoutInflater().inflate(R.layout.homepage, null);
        this.setContentView(root);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("CartShare");

        // Change password button click handler
        ((Button) findViewById(R.id.editprofile)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HomePageActivity.this, EditProfile.class));
            }  //end onClick
        });

        // Edit profile picture button click handler
        ((Button) findViewById(R.id.editPicture)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HomePageActivity.this, EditPicture.class));
            }  //end onClick
        });

        // Coupons picture button click handler
        ((Button) findViewById(R.id.coupons)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, MyCoupon.class);
                intent.putStringArrayListExtra("array", arrayUserGroups);
                startActivity(intent);
            }  //end onClick
        });

        // Edit paypal email address button click handler
        ((Button) findViewById(R.id.paypalButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HomePageActivity.this, EditPaypal.class));
            }  //end onClick
        });

        // Log out button click handler
        ((Button) findViewById(R.id.logOut)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logOut();
            }  //end onClick
        });

        // Locate the current user, mUser is name of current user.
        ParseUser currentUser = ParseUser.getCurrentUser();
        mUser = currentUser.getEmail().toString();
        uID = currentUser.getObjectId().toString();

        // Locate the button and imageView in homepage.xml
        button = (Button) findViewById(R.id.editPicture);
        pic = (ImageView) findViewById(R.id.profilePic);

        // Locate the textView in homepage.xml
        strName = (TextView) findViewById(R.id.hello);

        if (currentUser != null)
        {
            strTemp = (String) currentUser.get("firstName");
            profile = currentUser.getParseFile("profilePicture");

            if(profile != null) {
                profile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        Bitmap bitpic = BitmapFactory.decodeByteArray(data, 0, data.length);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        if(bitpic != null) {
                            bitpic.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            pic.setImageBitmap(bitpic);
                        }
                    }  //end done
                });  //end getDataInBackground
            }  //end if
        }  //end if

        if(strTemp != null)
            strName.setText("Hello " + strTemp + "!");
        else
            strName.setText("Hello!");

        iList = new ArrayList<item>();
        listView = (ListView) findViewById(R.id.list);
        myListAdapter = new MyListAdapter(this, R.layout.list_item, iList);
        listView.setAdapter(myListAdapter);

        refreshPostList();

        listView.setOnItemClickListener(this);

        //Get the number of notification this current user has
        ParseQuery<ParseObject> pQueryNotif = ParseQuery.getQuery("notification");
        pQueryNotif.whereEqualTo("friend",  uID);

        try {
            List<ParseObject> result = pQueryNotif.find();

            if (result.size() > 0)
                numNotification = result.size();

        } catch (ParseException e) {
            e.printStackTrace();
        }  //end try-catch
    }  //end onCreate

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        item list_name = iList.get(position);
        Intent intent = new Intent(this, ViewListActivity.class);
        intent.putExtra("id", list_name.getId());
        intent.putExtra("name", list_name.getTitle());

        //maybe add objectID, we'll see
        startActivity(intent);
    }  //end onItemClick

    private void findPostList(List<ParseObject> postList)
    {
        String userid = ParseUser.getCurrentUser().getObjectId();
        iList.clear();
        for (ParseObject post : postList)
        {
            for (int j = 0; j < post.getList("authors").size(); j++) {
                if (userid.contains(post.getList("authors").get(j).toString())) {
                    item note = new item(post.getObjectId(), post.getString("listName"));
                    iList.add(note);
                    arrayUserGroups.add(post.getObjectId());
                    j = post.getList("authors").size();
                }  //end if
            }  //end for loop
        }  //end for loop

        myListAdapter.notifyDataSetChanged();
    }  //end findPostList

    private void refreshPostList() {
        setProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseObject> pQuery = ParseQuery.getQuery("lists");

        pQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> postList, ParseException e) {

                setProgressBarIndeterminateVisibility(false);

                if (e == null)
                    findPostList(postList);
                else {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                }  //end else
            }  //end done
        });  //end findInBackground
    }  //end refreshPostList

    public void toggleMenu(View v)
    {
        this.root.toggleMenu();
    }  //end toggleMenu

    public void logOut()
    {
        ParseUser.getCurrentUser().logOut();
        startActivity(new Intent(HomePageActivity.this, CheckingStatus.class));
    }  //end logOut

    public void toggleAddFriend ()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("friends");
        query.whereEqualTo("user", mUser);
        try {
            List<ParseObject> results = query.find();

            if (results.size() > 0) {
                for (ParseObject obj : results) {
                    array.add(obj.get("friend").toString());
                }  //end for loop
            }  //end if
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(HomePageActivity.this, FriendsList.class);
        intent.putStringArrayListExtra("key", array);
        startActivity(intent);
    }  //end toggleAddFriend

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.actionbar_item);

        MenuItemCompat.setActionView(item, R.layout.notification_update_count_layout);

        notificationCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        numNotif = (TextView) notificationCount.findViewById(R.id.badge_notification_1);

        if(numNotification > 0)
            numNotif.setText(Integer.toString(numNotification));

        notificationCount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                startActivity(new Intent(HomePageActivity.this, NotificationPage.class));
                //test();
            }  //end onClick
        });  //end setOnClickListener

        return super.onCreateOptionsMenu(menu);
    }  //end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_slidemenu:
            {
                this.root.toggleMenu();
                break;
            }
            case R.id.action_viewfriendlist:
            {
                toggleAddFriend();
                break;
            }
            case R.id.action_newlist:
            {
                //final ProgressDialog d = new ProgressDialog(HomePageActivity.this);
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);
                builder.setTitle("Enter New List Name!");
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

                        if (postTitle.isEmpty())
                            Toast.makeText(HomePageActivity.this, "Your input is empty!", Toast.LENGTH_LONG).show();
                        else {
                            //create array for authors
                            ArrayList<String> authors = new ArrayList<String>();
                            authors.add(ParseUser.getCurrentUser().getObjectId());

                            ParseObject post = new ParseObject("lists");
                            post.put("listName", postTitle);
                            post.put("author", ParseUser.getCurrentUser().getObjectId());
                            post.put("authors", authors);

                            setProgressBarIndeterminateVisibility(true);
                            post.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    setProgressBarIndeterminateVisibility(false);
                                    if (e == null) {
                                        // Saved successfully.
                                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // The save failed.
                                        Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                                    }  //end else
                                }  //end done
                            });  //end query

                            startActivity(new Intent(HomePageActivity.this, HomePageActivity.class));
                        }  //end else
                    }  //end onClick
                });  //end button

                builder.show();
                break;
            }
        }  //end switch case
        return super.onOptionsItemSelected(item);
    }  //end onOptionsItemSelected

    private void  test()
    {
        int n;
        String str = numNotif.getText().toString();

        if(str.equals(""))
            n = 0;
        else
            n = Integer.parseInt(str) ;

        str = Integer.toString(n+1);

        numNotif.setText(str);
    }
}  //end HomePageActivity class
