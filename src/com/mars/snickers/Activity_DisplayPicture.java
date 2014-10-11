package com.mars.snickers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mars.snickers.helper.ImageHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Activity_DisplayPicture extends ActionBarActivity {

	Button cancel, rotate, done;
	ImageView userImage;
	String imagePath;
	Bitmap image;
	public static final int FLIP_VERTICAL = 1;
	public static final int FLIP_HORIZONTAL = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_picture);
		cancel = (Button) findViewById(R.id.adp_btn_cancel);
		rotate = (Button) findViewById(R.id.adp_btn_rotate);
		done = (Button) findViewById(R.id.adp_btn_done);
		userImage = (ImageView) findViewById(R.id.adp_iv_picture);
		imagePath = getIntent().getStringExtra("selectedImagePath");

		image = ImageHelper.decodeFile(imagePath,
				ImageHelper.getDeviceWidth(Activity_DisplayPicture.this),
				ImageHelper.getDeviceHeight(Activity_DisplayPicture.this),
				ImageHelper.ScalingLogic.FIT);
		userImage.setImageBitmap(flipImage(image, 1));
	}

	public void onDoneClick(View v) {
		new SavePhotoTask().execute(image);
	}

	public void onCancelClick(View v) {
		sendResponse(RESULT_CANCELED, null);
	}

	public void onRotateClick(View v) {

		Matrix mat = new Matrix();
		mat.postRotate(90);
		Bitmap bMapRotate = Bitmap.createBitmap(image, 0, 0, image.getWidth(),
				image.getHeight(), mat, true);
		image.recycle();
		image = bMapRotate;
		BitmapDrawable bmd = new BitmapDrawable(bMapRotate);
		userImage.setImageBitmap(bMapRotate);
	}

	private void sendResponse(int response, String path) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("selectedImagePath", path);
		setResult(response, returnIntent);
		finish();
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
				fos = new FileOutputStream(photo.getPath());
				jpeg[0].compress(Bitmap.CompressFormat.JPEG, 100, fos);
			} catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			} finally {
				try {
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
			sendResponse(RESULT_OK, s);
		}
	}

	public Bitmap flipImage(Bitmap src, int type) {
		// create new matrix for transformation
		Matrix matrix = new Matrix();
		// if vertical
		if (type == FLIP_VERTICAL) {
			// y = y * -1
			matrix.preScale(1.0f, -1.0f);
		}
		// if horizonal
		else if (type == FLIP_HORIZONTAL) {
			// x = x * -1
			matrix.preScale(-1.0f, 1.0f);
			// unknown type
		} else {
			return null;
		}

		// return transformed image
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
				matrix, true);
	}

}
