package com.example.david_000.cartshare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.List;

/**
 * Created by david_000 on 9/1/2015.
 */
public class SignInActivity extends AppCompatActivity {

    private EditText user;
    private EditText pw;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.signin);

        Drawable myIcon = getResources().getDrawable(R.drawable.icon_tik);
        myIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        ((ImageView)findViewById(R.id.cartIcon)).setImageDrawable(myIcon);

        user = (EditText) findViewById(R.id.username);
        pw = (EditText) findViewById(R.id.password);

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the sign up activity
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });

        findViewById(R.id.forgotpassword).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText input = new EditText(SignInActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                builder.setTitle("Enter your email address: ");
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }  //end onClick
                });  //end button

                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String str = input.getText().toString();
                        boolean flag=true;

                        if (str.isEmpty())
                            Toast.makeText(SignInActivity.this, "Your input is empty!", Toast.LENGTH_LONG).show();
                        else
                        {
                            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                            userQuery.whereEqualTo("email", str);

                            try {
                                List<ParseUser> result = userQuery.find();
                                if (result.size() == 0) {
                                    flag=false;
                                }  //end if
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }  //end try-catch
                        }  //end if-else

                        if(flag==true) {
                            ParseUser.requestPasswordResetInBackground(str,
                                    new RequestPasswordResetCallback() {
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(SignInActivity.this, "A reset link has sent to this email address.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(SignInActivity.this, "Error in recovering your password.", Toast.LENGTH_LONG).show();
                                            }
                                        }  //end done
                                    });
                        } else
                            Toast.makeText(SignInActivity.this, "This email address does not exist.", Toast.LENGTH_LONG).show();
                    }  //end onClick
                });  //end button

                builder.show();
            } //end onClick
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Validate the log in data
                boolean validationError = false;
                StringBuilder validationErrorMessage =
                        new StringBuilder("Please ");
                if (isEmpty(user)) {
                    validationError = true;
                    validationErrorMessage.append("enter email address");
                }
                if (isEmpty(pw)) {
                    if (validationError) {
                        validationErrorMessage.append(", and ");
                    }
                    validationError = true;
                    validationErrorMessage.append("enter a password");
                }
                validationErrorMessage.append(".");

                // If there is a validation error, display the error
                if (validationError) {
                    Toast.makeText(SignInActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                // Set up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(SignInActivity.this);
                dlg.setTitle("Please wait!");
                dlg.setMessage("Logging in, one moment!");
                dlg.show();

                // Call the Parse login method
                ParseUser.logInInBackground(user.getText().toString(), pw.getText().toString(), new LogInCallback() {

                    @Override
                    public void done(ParseUser user, ParseException e) {
                        dlg.dismiss();
                        if (e != null) {
                            // Show the error message
                            Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            // Start an intent for the dispatch activity
                            Intent intent = new Intent(SignInActivity.this, CheckingStatus.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }  //end isEmpty
}
