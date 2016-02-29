package com.example.david_000.cartshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by david_000 on 2/25/2016.
 */
public class EditItemActivity extends AppCompatActivity
{
    private item anItem;
    private EditText titleEditText;
    private String postTitle;
    static String groupId = "";
    static String groupName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Button saveItemButton;

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_edit_item);

        titleEditText = (EditText) findViewById(R.id.noteTitle);
        Intent intent = this.getIntent();

        if (intent.getExtras() != null) {
            groupId = intent.getStringExtra("group_id");
            groupName = intent.getStringExtra("group_name");
        }  //end if

        saveItemButton = (Button)findViewById(R.id.saveNote);
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();

            }
        });
    }  //end onCreate

    private void saveNote()
    {
        postTitle = titleEditText.getText().toString();
        postTitle = postTitle.trim();

        // If user doesn't enter a title or content, do nothing
        // If user enters title, but no content, save
        // If user enters content with no title, give warning *work in progress
        // If user enters both title and content, save

        if (!postTitle.isEmpty()) {
            // Check if post is being created or edited
            if (anItem == null) {
                // create new post
                ParseObject post = new ParseObject("item");
                post.put("title", postTitle);
                post.put("author", ParseUser.getCurrentUser().getObjectId());
                post.put("groupId", groupId);
                setProgressBarIndeterminateVisibility(true);
                post.saveInBackground(new SaveCallback() {
                    public void done( ParseException e) {
                        setProgressBarIndeterminateVisibility(false);
                        if (e == null) {
                            // Saved successfully.
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("group_id",groupId);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            // The save failed.
                            Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                            Log.d(getClass().getSimpleName(), "User update error: " + e);
                        }  //end else
                    }
                });  //end query
            } //end if
            else {
                // update post
                ParseQuery<ParseObject> query = ParseQuery.getQuery("item");

                // Retrieve the object by id
                setProgressBarIndeterminateVisibility(true);
                query.getInBackground(anItem.getId(), new GetCallback<ParseObject>() {
                    public void done(ParseObject post, ParseException e) {
                        setProgressBarIndeterminateVisibility(false);
                        if (e == null) {
                            // Now let's update it with some new data.
                            post.put("title", postTitle);
                            post.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // Saved successfully.
                                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // The save failed.
                                        Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                                        Log.d(getClass().getSimpleName(), "User update error: " + e);
                                    }  //end else
                                }
                            });  //end query
                        }  //end if
                    }
                });  //end query
            }  //end else
        }  //end if
    }  //end saveNote
}  //end class EditItemActivity


