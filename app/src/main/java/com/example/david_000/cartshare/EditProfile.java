package com.example.david_000.cartshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by david_000 on 11/2/2015.
 */
public class EditProfile extends AppCompatActivity
{
    private EditText pw, confirmPw;
    ParseUser user = ParseUser.getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.editprofile);

        pw = (EditText) findViewById(R.id.editPassword);
        confirmPw = (EditText) findViewById(R.id.confirmPassword);
        final ProgressDialog d = new ProgressDialog(EditProfile.this);

        findViewById(R.id.saveEdit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isEmpty(pw))
                    Toast.makeText(getApplicationContext(), "Your input is empty.", Toast.LENGTH_SHORT).show();
                else if (!pw.getText().toString().matches(confirmPw.getText().toString()))
                    Toast.makeText(getApplicationContext(), "Confirm password does not match.", Toast.LENGTH_SHORT).show();
                else {
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
}  //end EditProfile
