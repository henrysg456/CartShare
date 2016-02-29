package com.example.david_000.cartshare;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by david_000 on 11/17/2015.
 */
public class MyReceipt extends AppCompatActivity
{
    ParseUser user = ParseUser.getCurrentUser();
    private ImageView mImageView;
    private Bitmap thumbnail;
    String mCurrentPhotoPath;
    ParseFile profile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.managereceipts);

        final ProgressDialog d = new ProgressDialog(MyReceipt.this);
        mImageView = (ImageView) findViewById(R.id.pictureView);

        if (user != null) {
            profile = user.getParseFile("receipt");

            if(profile != null) {
                profile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        Bitmap bitpic = BitmapFactory.decodeByteArray(data, 0, data.length);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitpic.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        mImageView.setImageBitmap(bitpic);
                    }  //end done
                });  //end getDataInBackground
            }  //end if
        }  //end if

        findViewById(R.id.capture).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectImage();
            }  //end onClick
        });  //end clickListener

        findViewById(R.id.savePicture).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(thumbnail != null)
                {
                    //if picture is not null then save it to parse
                    // Convert it to byte
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();
                    // Create the ParseFile
                    ParseFile file = new ParseFile("myReceipt.png", image);
                    // Upload the image into Parse Cloud
                    user.put("receipt", file);
                    user.saveInBackground(new SaveCallback() {
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                // Save was successful!
                                d.setTitle("Succesfully Updated!");
                                d.show();
                                startActivity(new Intent(MyReceipt.this, HomePageActivity.class));
                            } else {
                                // Save failed. Inspect e for details.
                                d.setTitle("Error in updating the information.");
                                d.show();
                            }  //end else
                        }  //end done
                    });  //end saveInBackground
                }  //end if
            }  //end onClick
        });  //end clickListener

        findViewById(R.id.cancelReceipts).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the home page activity
                startActivity(new Intent(MyReceipt.this, HomePageActivity.class));
            }  //end onClick
        });
    }  //end onCreate


    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(MyReceipt.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
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
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
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
                        mImageView.setImageBitmap(thumbnail);
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
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                    mImageView.setImageBitmap(thumbnail);
                    c.close();
                }  //end if
                else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }  //end else
            }  //end else if
        }  //end if
    } //end onActivityResult
}  //end MyReceipt
