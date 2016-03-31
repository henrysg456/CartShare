package com.example.david_000.cartshare;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david_000 on 2/16/2016.
 */
public class ViewListActivity extends AppCompatActivity
{
    ActionBar.Tab shoppingTab, couponTab , receiptTab;
    Fragment tab1 = new TabFragment1();
    Fragment tab2 = new TabFragment2();
    Fragment tab3 = new TabFragment3();
    private ArrayList<String> array = new ArrayList<String>();
    String groupId = "";
    String groupName = "";
    private String postTitle="";
    private Bitmap thumbnail;
    private String mUser;
    private String strCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        // Locate the current user, mUser is name of current user.
        ParseUser currentUser = ParseUser.getCurrentUser();
        mUser = currentUser.getEmail().toString();

        Intent intent = this.getIntent();

        if (intent.getExtras() != null) {
            groupId = intent.getStringExtra("id");
            groupName = intent.getStringExtra("name");
        }  //end if

        ActionBar actionBar = getSupportActionBar();
        ActionBar.Tab shoppingTab, couponTab , receiptTab;

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        shoppingTab = actionBar.newTab().setText("Items");
        couponTab = actionBar.newTab().setText("Coupons");
        receiptTab = actionBar.newTab().setText("Receipts");

        Bundle bundle=new Bundle();
        bundle.putString("id", groupId);
        bundle.putString("name", groupName);
        tab1.setArguments(bundle);
        tab2.setArguments(bundle);
        tab3.setArguments(bundle);

        shoppingTab.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.replace(R.id.viewlist, tab1);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.remove(tab1);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        });

        couponTab.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.replace(R.id.viewlist, tab2);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.remove(tab2);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        });

        receiptTab.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.replace(R.id.viewlist, tab3);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                ft.remove(tab3);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        });

        actionBar.addTab(shoppingTab);
        actionBar.addTab(couponTab);
        actionBar.addTab(receiptTab);

    }  //end onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_list, menu);
        return true;
    }  //end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_payment:
            {
                Intent intent = new Intent(ViewListActivity.this, PaymentPage.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", groupName);
                startActivity(intent);
                break;
            }
            case R.id.action_new:
            {
                final CharSequence[] options = { "          ITEM", "          COUPON", "          RECEIPT", "          CANCEL" };
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewListActivity.this);
                builder.setTitle("Add: ");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("          ITEM")) {
                            addItem();
                        } else if (options[item].equals("          COUPON")) {
                            selectImage("c");
                        } else if (options[item].equals("          RECEIPT")) {
                            selectImage("r");
                        } else if (options[item].equals("          CANCEL")) {
                            dialog.dismiss();
                        }  //end if
                    }  //end onClick
                });  //end builder.setItems
                builder.show();

                break;
            }
            case R.id.action_addmember:
            {
                toggleAddFriend();
                break;
            }
        }  //end switch case

        return super.onOptionsItemSelected(item);
    }  //end onOptionsItemSelected

    public void toggleAddFriend ()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("friends");
        query.whereEqualTo("user", mUser);
        try {
            List<ParseObject> results = query.find();

            if (results.size() > 0) {
                for (ParseObject obj : results) {
                    array.add(obj.get("friend").toString());
                }  //end for loop
            }  //end if
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(ViewListActivity.this, FriendsList.class);
        intent.putStringArrayListExtra("key", array);
        intent.putExtra("groupId", groupId);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
    }  //end toggleAddFriend

    public void addItem()
    {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        AlertDialog.Builder builderItem = new AlertDialog.Builder(ViewListActivity.this);
        builderItem.setTitle("Enter New Item Name!");
        builderItem.setView(input);

        // Set up the buttons
        builderItem.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }  //end onClick
        });  //end button

        builderItem.setNegativeButton("Add          ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postTitle = input.getText().toString();
                postTitle = postTitle.trim();

                if (!postTitle.isEmpty()) {
                    // create new post
                    ParseObject post = new ParseObject("item");
                    post.put("title", postTitle);
                    post.put("author", ParseUser.getCurrentUser().getObjectId());
                    post.put("groupId", groupId);
                    setProgressBarIndeterminateVisibility(true);
                    post.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null) {
                                // Saved successfully.
                                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ViewListActivity.this, ViewListActivity.class);
                                intent.putExtra("id", groupId);
                                intent.putExtra("name", groupName);
                                startActivity(intent);
                            } else {
                                // The save failed.
                                Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                                Log.d(getClass().getSimpleName(), "User update error: " + e);
                            }  //end else
                        }  //end done
                    });  //end query
                } //end if
                else {
                    Toast.makeText(ViewListActivity.this, "Your input is empty!", Toast.LENGTH_LONG).show();
                }  //end else
            }  //end onClick
        });  //end button

        builderItem.show();
    }

    private void selectImage(String check)
    {
        strCheck = check;
        final CharSequence[] options = { "          Take Photo", "          Choose from Gallery","          Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewListActivity.this);

        if(strCheck.equals("c"))
            builder.setTitle("Add Coupon!");

        if(strCheck.equals("r"))
            builder.setTitle("Add Receipt!");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("          Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("          Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("          Cancel")) {
                    dialog.dismiss();
                }  //end if
            }  //end onClick
        });  //end builder.setItems
        builder.show();
    }  //end selectImage

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
                {
                    File f = new File(Environment.getExternalStorageDirectory().toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("temp.jpg")) {
                            f = temp;
                            break;
                        }  //end if
                    }  //end for loop
                    try {
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        thumbnail = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                        //mImageView.setImageBitmap(thumbnail);
                        savePicture();
                        String path = android.os.Environment.getExternalStorageDirectory()
                                + File.separator + "CartShare" + File.separator + "default";
                        f.delete();
                        OutputStream outFile = null;
                        File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");

                        try {
                            outFile = new FileOutputStream(file);
                            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
                            outFile.flush();
                            outFile.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }  //end catch
                }  //end if
                else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                }  //end else
            } else if (requestCode == 2) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    thumbnail = (BitmapFactory.decodeFile(picturePath));
                    savePicture();
                    //mImageView.setImageBitmap(thumbnail);
                    c.close();
                }  //end if
                else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }  //end else
            }  //end else if
        }  //end if
    } //end onActivityResult

    private void savePicture()
    {
        if(thumbnail != null)
        {
            //if picture is not null then save it to parse
            // Convert it to byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Compress image to lower quality scale 1 - 100
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();

            // Create the ParseFile
            ParseFile file = new ParseFile(ParseUser.getCurrentUser().getObjectId(), image);

            // Upload the image into Parse Cloud
            if(strCheck.equals("c")) {
                ParseObject post = new ParseObject("coupon");
                post.put("coupons", file);
                post.put("author", ParseUser.getCurrentUser().getObjectId());
                post.put("groupId", groupId);

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
                    }  //end done
                });  //end query
            }  //end if

            if(strCheck.equals("r")) {
                ParseObject post = new ParseObject("receipt");
                post.put("receipts", file);
                post.put("author", ParseUser.getCurrentUser().getObjectId());
                post.put("groupId", groupId);

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
                    }  //end done
                });  //end query
            }  //end if
        }  //end if

        Intent intent = new Intent(ViewListActivity.this, ViewListActivity.class);
        intent.putExtra("id", groupId);
        intent.putExtra("name", groupName);

        startActivity(intent);
    }  //end savePicture
}  //end class
