package com.example.david_000.cartshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david_000 on 2/16/2016.
 */
public class ViewListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private List<item> iList;
    private MyListAdapter myListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        final ListView listView = (ListView) findViewById(R.id.listview);
        iList = new ArrayList<item>();
        myListAdapter = new MyListAdapter(this, R.layout.list_item, iList);
        listView.setAdapter(myListAdapter);

        Intent intent = this.getIntent();
        String groupId = "";
        String groupName = "";

        if (intent.getExtras() != null) {
            groupId = intent.getStringExtra("list_id");
            groupName = intent.getStringExtra("list_name");
        }  //end if

        refreshPostList(groupId, groupName);
        listView.setOnItemClickListener(this);
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
            case R.id.action_new: {
                //get data(groupID) passed from main menu
                Intent intent = this.getIntent();
                String groupId = "";
                String groupName = "";

                if (intent.getExtras() != null) {
                    groupId = intent.getStringExtra("list_id");
                    groupName = intent.getStringExtra("list_name");
                }  //end if

                //pass data to class where we add new item
                intent = new Intent(this, EditItemActivity.class);
                intent.putExtra("group_id", groupId);
                intent.putExtra("group_name", groupName);
                startActivityForResult(intent, 1);

                break;
            }
        }  //end switch case
        return super.onOptionsItemSelected(item);
    }  //end onOptionsItemSelected

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String stredittext=data.getStringExtra("group_id");
                refreshPostList(stredittext, "");
            }  //end if
        }  //end if
    }  //end onActivityResult

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        item aItem = iList.get(position);
        Log.d("TestID", aItem.getId());
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("noteId", aItem.getId());
        intent.putExtra("noteTitle", aItem.getTitle());
        startActivity(intent);
    }

    //@Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        item aItem = iList.get(position);
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("noteId", aItem.getId());
        intent.putExtra("noteTitle", aItem.getTitle());
        startActivity(intent);
    }  //end onListItemClick

    private void refreshPostList(String groupId, String groupName)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("item");
        setProgressBarIndeterminateVisibility(true);
        query.whereEqualTo("groupId", groupId);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> postList, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    // If there are results, update the list of posts
                    // and notify the adapter
                    iList.clear();
                    for (ParseObject post : postList) {
                        item note = new item(post.getObjectId(), post.getString("title"));
                        iList.add(note);
                    }  //end for loop
                    myListAdapter.notifyDataSetChanged();
                } else
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
            }
        });  //end query
    }  //end refreshPostList
}  //end class
