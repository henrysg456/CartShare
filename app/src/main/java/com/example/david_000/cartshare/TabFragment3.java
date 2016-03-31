package com.example.david_000.cartshare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david_000 on 1/20/2016.
 */
public class TabFragment3 extends Fragment
{
    //the images to display
    private ArrayList<Bitmap> imageIDs = new ArrayList<Bitmap>();
    private ArrayList<String> array = new ArrayList<String>();
    private String groupID ="", groupName="";
    private ParseFile cp;
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.tab3_layout, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.image2);

        groupID = getArguments().getString("id");
        groupName = getArguments().getString("name");
        getImages();

        Gallery gallery = (Gallery) rootView.findViewById(R.id.gallery2);
        gallery.setAdapter(new ImageAdapter(getActivity()));
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(ctx, "pic" + (position + 1) + " selected", Toast.LENGTH_SHORT).show();
                imageView.setImageBitmap(imageIDs.get(position));
                final int pos = position;

                // Delete coupon button click handler
                ((Button) rootView.findViewById(R.id.del2)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Do you want to delete this receipt?");

                        // Set up the buttons
                        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }  //end onClick
                        });  //end button

                        builder.setNegativeButton("Yes          ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseQuery<ParseObject> pQueryCoupon = ParseQuery.getQuery("receipt");
                                pQueryCoupon.whereEqualTo("objectId", array.get(pos));

                                try {
                                    List<ParseObject> result = pQueryCoupon.find();

                                    //make sure result is not empty before accessing it
                                    if (result.size() > 0) {
                                        ParseObject.createWithoutData("receipt", result.get(0).getObjectId()).deleteEventually();
                                        SystemClock.sleep(250);

                                        Intent intent = new Intent(getActivity(), ViewListActivity.class);
                                        intent.putExtra("id", groupID);
                                        intent.putExtra("name", groupName);
                                        startActivity(intent);
                                    }  //end if
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }  //end try-catch
                            }  //end onClick
                        });  //end button

                        builder.show();
                    }  //end onClick
                });  //end button
            }  //end onItemClick
        });

        if(imageIDs.size() != 0)
            imageView.setImageBitmap(imageIDs.get(0));

        return rootView;
    }  //end onCreateView

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getImages()
    {
        imageIDs.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("receipt");
        query.whereEqualTo("groupId", groupID);

        try {
            List<ParseObject> results = query.find();

            //Make sure results is not empty before accessing it
            if(results.size() > 0)
                for(int i=0; i < results.size(); i++)
                {
                    cp = results.get(i).getParseFile("receipts");

                    try {
                        byte[] bitmapdata = cp.getData();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        imageIDs.add(bitmap);
                        array.add(results.get(i).getObjectId());
                    } catch (ParseException e2) {
                        e2.printStackTrace();
                    }  //end try-catch
                }  //end for loop
        } catch( ParseException e) {
            e.printStackTrace();
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }  //end try-catch
    }  //end getImages

    public class ImageAdapter extends BaseAdapter
    {
        private Context context;
        private int itemBackground;
        public ImageAdapter(Context c)
        {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a = c.obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();
        }  //end ImageAdapter constructor
        // returns the number of images
        public int getCount() {
            return imageIDs.size();
        }
        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }
        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }
        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(imageIDs.get(position));
            imageView.setLayoutParams(new Gallery.LayoutParams(100, 100));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }  //end getView
    }  //end ImageAdapter
}  //end TabFragment3
