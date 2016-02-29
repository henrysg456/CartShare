package com.example.david_000.cartshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by david_000 on 9/19/2015.
 */
public class SignUpActivity extends AppCompatActivity {

    private EditText usernameView;
    private EditText passwordView;
    private EditText firstnameView;
    private EditText lastnameView;
    private EditText emailView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        //Set up the sign up fields
        passwordView = (EditText) findViewById(R.id.password);
        firstnameView = (EditText) findViewById(R.id.firstname);
        lastnameView = (EditText) findViewById(R.id.lastname);
        emailView = (EditText) findViewById(R.id.email);

        findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the log in activity
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });

        findViewById(R.id.signupnow).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Validate the sign up data
                boolean validationError = false;
                StringBuilder validationErrorMessage =
                        new StringBuilder("Please ");
                if (isEmpty(passwordView)) {
                    validationError = true;
                    validationErrorMessage.append("enter password");
                }

                if(isEmpty(firstnameView)) {
                    if (validationError) {
                        validationErrorMessage.append(", and ");
                    }
                    validationError = true;
                    validationErrorMessage.append("enter first name");
                }

                if(isEmpty(lastnameView)) {
                    if (validationError) {
                        validationErrorMessage.append(", and ");
                    }
                    validationError = true;
                    validationErrorMessage.append("enter last name");
                }

                if(isEmpty(emailView)) {
                    if (validationError) {
                        validationErrorMessage.append(", and ");
                    }
                    validationError = true;
                    validationErrorMessage.append("enter email address");
                }
               /*
                if (!isMatching(passwordView, passwordAgainView)) {
                    if (validationError) {
                        validationErrorMessage.append(", and");
                    }
                    validationError = true;
                    validationErrorMessage.append("enter the same password twice");
                } */
                validationErrorMessage.append(".");

                // If there is a validation error, display the error
                if (validationError) {
                    Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                // Set up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(SignUpActivity.this);
                dlg.setTitle("Please wait.");
                dlg.setMessage("Signing up, one moment!");
                dlg.show();

                // Set up a new Parse user
                ParseUser user = new ParseUser();
                user.setPassword(passwordView.getText().toString());
                user.setEmail(emailView.getText().toString());
                user.put("firstName", firstnameView.getText().toString());
                user.put("lastName", lastnameView.getText().toString());
                user.setUsername(emailView.getText().toString());

                //Call the Parse signup method
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        dlg.dismiss();
                        if (e != null) {
                            // Show the error message
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            // Start an intent for the dispatch activity
                            Intent intent = new Intent(SignUpActivity.this, CheckingStatus.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }  //end else
                    }  //end done
                });
            }  //end onClick
        });
    }  //end onCreate

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else
            return true;
        }  //end isEmpty

    private boolean isMatching(EditText etText1, EditText etText2) {
        if (etText1.getText().toString().equals(etText2.getText().toString())) {
            return true;
        } else
            return false;
    }
}  //end SignUpActivity
