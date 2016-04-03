package com.example.david_000.cartshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by david_000 on 3/30/2016.
 */
public class EditPaypal extends AppCompatActivity
{
    private EditText editpp;
    private TextView viewpp;
    private String paypal="", str="";
    ParseUser user = ParseUser.getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.editpaypal);

        final ProgressDialog d = new ProgressDialog(EditPaypal.this);

        editpp = (EditText) findViewById(R.id.editTextPaypal);
        viewpp = (TextView) findViewById(R.id.textViewPaypal);

        if(user.get("paypal") == null)
        {        }
        else
        {
            paypal = user.get("paypal").toString();
            viewpp.setText("Your Paypal.me URL address is " + paypal);
        }

        findViewById(R.id.savePaypal).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isEmpty(editpp))
                    Toast.makeText(getApplicationContext(), "Your input is empty!", Toast.LENGTH_SHORT).show();
                else
                {
                    user.put("paypal", editpp.getText().toString());

                    user.saveInBackground(new SaveCallback() {
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                // Save was successful!
                                d.setTitle("Succesfully Updated!");
                                d.show();
                                startActivity(new Intent(EditPaypal.this, HomePageActivity.class));
                            } else {
                                // Save failed. Inspect e for details.
                                d.setTitle("Error in updating the information.");
                                d.show();
                            }  //end else
                        }  //end done
                    });  //end saveInBackground
                }  //end if-else
            }  //end onClick
        });  //end clickListener
    }  //end onCreate

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        else
            return true;
    }  //end isEmpty
}  //end EditPaypal class
