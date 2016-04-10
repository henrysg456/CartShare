package com.example.david_000.cartshare;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
 * Created by david_000 on 11/17/2015.
 */
public class MyCoupon extends AppCompatActivity
{
    //the images to display
    private ArrayList<Bitmap> imageIDs = new ArrayList<Bitmap>();
    private ArrayList<String> array = new ArrayList<String>();
    private ParseFile cp;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.managecoupons);

        array = getIntent().getStringArrayListExtra("array");
        getImages();

        imageView = (ImageView) findViewById(R.id.image3);
        Gallery gallery = (Gallery) findViewById(R.id.gallery3);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(ctx, "pic" + (position + 1) + " selected", Toast.LENGTH_SHORT).show();
                imageView.setImageBitmap(imageIDs.get(position));
            }  //end onItemClick
        });

        if(imageIDs.size() != 0)
            imageView.setImageBitmap(imageIDs.get(0));
    }  //end onCreate

    private void getImages()
    {
        imageIDs.clear();

        for(String str : array) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("coupon");
            query.whereEqualTo("groupId", str);

            try {
                List<ParseObject> results = query.find();

                //Make sure results is not empty before accessing it
                if(results.size() > 0)
                    for(int i=0; i < results.size(); i++)
                    {
                        cp = results.get(i).getParseFile("coupons");

                        try {
                            byte[] bitmapdata = cp.getData();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            imageIDs.add(bitmap);
                        } catch (ParseException e2) {
                            e2.printStackTrace();
                        }  //end try-catch
                    }  //end for loop
            } catch( ParseException e) {
                e.printStackTrace();
                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
            }  //end try-catch
        }  //end for loop
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

}  //end MyCoupon

