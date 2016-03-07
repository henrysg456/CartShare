package com.example.david_000.cartshare;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by david_000 on 2/12/2016.
 */
public class MyListAdapter extends ArrayAdapter
{
    // Get the item view here
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    public MyListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    // Constructor of ArrayAdapter
    public MyListAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


}  //end MyListAdapter class
