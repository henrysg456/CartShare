package com.example.david_000.cartshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david_000 on 1/18/2016.
 */
public class TabFragment1 extends Fragment
{
    private List<item> iList;
    private MyListAdapter myListAdapter;
    private String groupID ="", groupName="";
    private String postTitle="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.tab1_layout, container, false);
        final ListView listView = (ListView) rootView.findViewById(R.id.listview);
        iList = new ArrayList<item>();
        myListAdapter = new MyListAdapter(getActivity(), R.layout.list_item, iList);
        listView.setAdapter(myListAdapter);

        groupID = getArguments().getString("id");
        groupName = getArguments().getString("name");

        refreshPostList(groupID, groupName);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                final item anItem = iList.get(arg2);
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Edit/Delete Item!");
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }  //end onClick
                });  //end button

                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseObject.createWithoutData("item", anItem.getId()).deleteEventually();
                        Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_SHORT).show();
                        SystemClock.sleep(250);
                        refreshPostList(groupID, groupName);
                    }  //end onClick
                });  //end button

                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postTitle = input.getText().toString();
                        postTitle = postTitle.trim();

                        if (!postTitle.isEmpty())
                        {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("item");
                            // Retrieve the object by id
                            query.getInBackground(anItem.getId(), new GetCallback<ParseObject>() {
                                public void done(ParseObject post, ParseException e) {
                                    if (e == null) {
                                        // Now let's update it with some new data.
                                        post.put("title", postTitle);
                                        post.saveInBackground(new SaveCallback() {
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    // Saved successfully.
                                                    Toast.makeText(getActivity(), "Updated!", Toast.LENGTH_SHORT).show();
                                                    refreshPostList(groupID, groupName);
                                                } else {
                                                    // The save failed.
                                                    Toast.makeText(getActivity(), "Failed to Save", Toast.LENGTH_SHORT).show();
                                                    Log.d(getClass().getSimpleName(), "User update error: " + e);
                                                }  //end else
                                            }  //end done
                                        });  //end query
                                    }  //end if
                                }  //end done
                            });  //end query
                        } //end if
                        else {
                            Toast.makeText(getActivity(), "Your input is empty!", Toast.LENGTH_LONG).show();
                        }  //end else
                    }  //end onClick
                });  //end button

                builder.show();
            }
        });

        return rootView;
    }  //end onCreateView

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshPostList(String groupId, String groupName)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("item");
        //setProgressBarIndeterminateVisibility(true);
        query.whereEqualTo("groupId", groupId);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> postList, ParseException e) {
                //setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    // If there are results, update the list of posts and notify the adapter
                    iList.clear();
                    for (ParseObject post : postList) {
                        item note = new item(post.getObjectId(), post.getString("title"));
                        iList.add(note);
                    }  //end for loop
                    myListAdapter.notifyDataSetChanged();
                } else
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
            }  //end done
        });  //end query
    }  //end refreshPostList
}  //end TabFragment1 class