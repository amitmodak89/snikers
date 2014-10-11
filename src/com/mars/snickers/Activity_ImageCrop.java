package com.mars.snickers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.edmodo.cropper.CropImageView;
import com.mars.snickers.helper.ImageHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class Activity_ImageCrop extends ActionBarActivity {

    // Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";

    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

    Bitmap croppedImage;
    Button cropButton;
    Button cropReset;
    CropImageView cropImageView;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_crop);
        Uri imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

        // Initialize components of the app
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);

        //Set AspectRatio fixed for circular selection
        cropImageView.setFixedAspectRatio(false);

        // Sets initial aspect ratio to 10/10
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
        cropImageView.setGuidelines(0);

        cropButton = (Button) findViewById(R.id.aic_btn_submitcrop);
        cropReset = (Button) findViewById(R.id.aic_btn_reset);
        cropReset.setEnabled(true);
        cropReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity_ImageCrop.this.sendResponse(RESULT_CANCELED, null);
            }
        });
        cropButton.setEnabled(false);

        if (imageUri != null) {
            new GetBitMapFromUri(Activity_ImageCrop.this, imageUri).execute();
        }


        cropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                croppedImage = cropImageView.getCroppedCircleImage(Activity_ImageCrop.this);
                Log.d("Yogesh", "Crop Clicked");
                new SavePhotoTask().execute(croppedImage);
            }
        });

    }


    private class GetBitMapFromUri extends AsyncTask<Void, Void, Bitmap> {
        Context context;
        Uri imageUri;

        public GetBitMapFromUri(Context context, Uri imageUri) {
            this.context = context;
            this.imageUri = imageUri;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap mBitmap = null;
            mBitmap = ImageHelper.decodeMedia(imageUri, ImageHelper.getDeviceWidth(Activity_ImageCrop.this), ImageHelper.getDeviceHeight(Activity_ImageCrop.this), context, ImageHelper.ScalingLogic.FIT);
            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                cropImageView.setImageBitmap(bitmap);
                cropButton.setEnabled(true);
            } else {
                sendResponse(RESULT_CANCELED, null);
            }
        }
    }

    class SavePhotoTask extends AsyncTask<Bitmap, String, String> {

        @Override
        protected String doInBackground(Bitmap... jpeg) {
            File photo = new File(getFilesDir(), "photo_snicker.jpg");
            if (photo.exists()) {
                photo.delete();
            }
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(photo);
                jpeg[0].compress(Bitmap.CompressFormat.JPEG, 90, fos);
            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            } finally {

                if (fos != null)
                    try {
                        jpeg[0].recycle();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            return photo.getAbsolutePath().toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                sendResponse(RESULT_OK, s);
            } else {
                sendResponse(RESULT_CANCELED, s);
            }
        }
    }

    private void sendResponse(int response, String bitmapPath) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("cropImagePath", bitmapPath);
        setResult(response, returnIntent);
        finish();
    }


}
