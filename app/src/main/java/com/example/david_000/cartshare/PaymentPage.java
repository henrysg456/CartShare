package com.example.david_000.cartshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david_000 on 3/29/2016.
 */
public class PaymentPage extends AppCompatActivity
{
    private String groupId="", groupName="";
    private List<item> iList;  //holds list of items
    private MyListAdapter myListAdapter;
    private ArrayList<String> members = new ArrayList<String>();
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> paypals = new ArrayList<String>();
    private TextView tv;
    private Button btn;
    private EditText et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        tv = (TextView) findViewById(R.id.paymentText);
        btn = (Button) findViewById(R.id.pay);
        et = (EditText) findViewById(R.id.amount);

        Intent intent = this.getIntent();

        if (intent.getExtras() != null) {
            groupId = intent.getStringExtra("id");
            groupName = intent.getStringExtra("name");
            members = intent.getStringArrayListExtra("members");
        }  //end if

        for(String one : members) {
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereEqualTo("objectId", one.toString());
            String str="";

            try {
                List<ParseUser> results = userQuery.find();
                if (results.size() > 0) {
                    str = results.get(0).get("firstName").toString();
                    str += " ";
                    str += results.get(0).get("lastName").toString();
                    names.add(str);

                    if(results.get(0).get("paypal") != null)
                        paypals.add(results.get(0).get("paypal").toString());
                    else
                        paypals.add("empty");
                }  //end if
            } catch (ParseException e) {
                e.printStackTrace();
            }  //end try-catch
        }  //end for loop

        Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, names);
        dropdown.setAdapter(adapter);

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

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // An spinnerItem was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                final int position = pos;

                tv.setText("Click the button below to pay " + names.get(pos).toString() + " via Paypal.");

                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String str="";

                        if(paypals.get(position).matches("empty"))
                            Toast.makeText(getApplicationContext(), names.get(position) + " has not set up Paypal.me URL yet.", Toast.LENGTH_SHORT).show();
                        else {
                            str = paypals.get(position).toString();

                            if(et.getText() != null)
                                str+= "\\" + et.getText();

                            Uri uriUrl = Uri.parse(str);
                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                            startActivity(launchBrowser);
                        }  //end if-else
                    }  //end onClick
                });  //end setOnClickListener
            }  //end onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }  //end onNothingSelected
        });  //end setOnItemSelectedListener
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
