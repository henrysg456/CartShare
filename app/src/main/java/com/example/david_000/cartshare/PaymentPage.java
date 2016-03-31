package com.example.david_000.cartshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by david_000 on 3/29/2016.
 */
public class PaymentPage extends AppCompatActivity
{
    private String groupId="", groupName="";
    private List<item> iList;  //holds list of items
    private MyListAdapter myListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        Intent intent = this.getIntent();

        if (intent.getExtras() != null) {
            groupId = intent.getStringExtra("id");
            groupName = intent.getStringExtra("name");
        }  //end if

        ParseQuery<ParseObject> query = ParseQuery.getQuery("item");
        query.whereEqualTo("groupId", groupId);

        try {
            List<ParseObject> postList = query.find();

            if (postList.size() > 0)
            {
                iList.clear();
                for (ParseObject post : postList) {
                    item note = new item(post.getObjectId(), post.getString("title"));
                    iList.add(note);
                }  //end for loop
                myListAdapter.notifyDataSetChanged();
            }  //end if
        } catch (Exception e) {
            e.printStackTrace();
        }  //end catch
    }  //end onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment, menu);
        return true;
    }  //end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_back: {
                Intent intent = new Intent(PaymentPage.this, ViewListActivity.class);
                intent.putExtra("id", groupId);
                intent.putExtra("name", groupName);
                startActivity(intent);
            }
        }  //end switch case

        return super.onOptionsItemSelected(item);
    }  //end onOptionsItemSelected
}  //end PaymentPage class
