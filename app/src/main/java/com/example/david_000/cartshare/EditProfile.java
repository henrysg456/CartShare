package com.example.david_000.cartshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by david_000 on 11/2/2015.
 */
public class EditProfile extends AppCompatActivity
{
    private EditText pw;
    ParseUser user = ParseUser.getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.editprofile);

        pw = (EditText) findViewById(R.id.editPassword);
        final ProgressDialog d = new ProgressDialog(EditProfile.this);

        findViewById(R.id.cancelEdit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the home page activity
                startActivity(new Intent(EditProfile.this, HomePageActivity.class));
            }
        });

        findViewById(R.id.saveEdit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user.setPassword(pw.getText().toString());
                user.saveInBackground(new SaveCallback() {
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            // Save was successful!
                            d.setTitle("Succesfully Updated!");
                            d.show();
                            startActivity(new Intent(EditProfile.this, HomePageActivity.class));
                        } else {
                            // Save failed. Inspect e for details.
                            d.setTitle("Error in updating the information.");
                            d.show();
                        }  //end else
                    }  //end done
                });  //end saveInBackground
            }  //end onClick
        });  //end clickListener
    }  //end onCreate
}  //end EditProfile
