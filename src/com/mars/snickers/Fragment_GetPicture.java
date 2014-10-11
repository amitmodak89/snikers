package com.mars.snickers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mars.snickers.helper.CameraManager;
import com.mars.snickers.helper.FontManager;
import com.mars.snickers.helper.ImageHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_GetPicture extends Fragment {

	public String TAG = "Fragment_GetPicture";
	public static Boolean camera_click = false;
	Button upload, camera;
	TextView header;
	private final int REQUEST_CODE_CAMERA = 111;
	private final int REQUEST_CODE_FILE_GALLERY_SELECTION = 112;
	private final int REQUEST_CODE_CROP_IMAGE = 113;
	private final int REQUEST_CODE_ACCEPT_IMAGE = 114;

	public static boolean galleryLoad = false;

	public Fragment_GetPicture() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_get_picture, container,
				false);
		upload = (Button) view.findViewById(R.id.fgp_btn_gallery);
		upload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startGalleryExplorerActivityForResult();
			}
		});

		camera = (Button) view.findViewById(R.id.fgp_btn_camera);
		camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (CameraManager.checkCameraHardware(getActivity())) {
					galleryLoad = false;
					startCameraActivityForResult();
				}
			}
		});

		header = (TextView) view.findViewById(R.id.fgp_tv_header);

		FontManager.setFont(getActivity(), header, getActivity().getResources()
				.getDimension(R.dimen.gap), FontManager.FontType.HERMESTR_BOLD);
		FontManager.setFont(getActivity(), upload, getActivity().getResources()
				.getDimension(R.dimen.gapx0_75), FontManager.FontType.FRANKLIN);
		FontManager.setFont(getActivity(), camera, getActivity().getResources()
				.getDimension(R.dimen.gapx0_75), FontManager.FontType.FRANKLIN);
		return view;
	}

	/*
	 * private void startImageCroppingActivityForResult(Uri imageUri) { Intent i
	 * = new Intent(getActivity(), Activity_ImageCrop.class);
	 * i.putExtra("imageUri", imageUri.toString()); startActivityForResult(i,
	 * REQUEST_CODE_CROP_IMAGE); }
	 */
	private void startGalleryExplorerActivityForResult() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		galleryLoad = true;
		startActivityForResult(i, REQUEST_CODE_FILE_GALLERY_SELECTION);
	}

	private void startCameraActivityForResult() {
		Intent i = new Intent(getActivity(), Activity_Camera.class);
		startActivityForResult(i, REQUEST_CODE_CAMERA);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_FILE_GALLERY_SELECTION:
			// gallerySelectionResult(resultCode, data);
			// String cropImagePath = data.getData().toString();
			try {
				if (resultCode == getActivity().RESULT_OK){
				Uri selectedImage = data.getData();
				cropImageResult(112, data);
/*				if (selectedImage != null) {
					String[] filePathColumn = { MediaStore.Images.Media.DATA };
					Cursor cursor = getActivity().getContentResolver().query(
							selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String cropImagePath = cursor.getString(columnIndex);
					cursor.close();
					if (cropImagePath != null) {
						// TODO pass image for face work
						Log.d("Yogesh", "Crop Image path:" + cropImagePath);
						goToAvatarMaker(cropImagePath);
					}
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.failedToLoad),
							Toast.LENGTH_SHORT).show();
				}*/
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			break;

		/*
		 * case REQUEST_CODE_CROP_IMAGE: cropImageResult(resultCode, data);
		 * break;
		 */
		case REQUEST_CODE_CAMERA:
			cameraImageResult(resultCode, data);
			break;

		case REQUEST_CODE_ACCEPT_IMAGE:
			acceptImageResult(resultCode, data);
		}

	}

	private void acceptImageResult(int resultCode, Intent data) {
		if (resultCode == getActivity().RESULT_OK) {
			Log.d("Yogesh", "RESULT CODE :" + resultCode + " - "
					+ getActivity().RESULT_OK);
			String selectedImagePath = data.getStringExtra("selectedImagePath");
			if (selectedImagePath != null) {
				goToAvatarMaker(selectedImagePath);
			} else {
				Toast.makeText(getActivity(), getString(R.string.failedToLoad),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getActivity(), getString(R.string.processCancelled),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void cameraImageResult(int resultCode, Intent data) {

		if (resultCode == getActivity().RESULT_OK) {
			Log.d("Yogesh", "RESULT CODE :" + resultCode + " - "
					+ getActivity().RESULT_OK);
			String cameraImagePath = data.getStringExtra("cameraImagePath");
			if (cameraImagePath != null) {
				Log.d("Yogesh", "Camera Success path:" + cameraImagePath);
				Intent intent = new Intent(getActivity(),
						Activity_DisplayPicture.class);
				intent.putExtra("selectedImagePath", cameraImagePath);
				startActivityForResult(intent, REQUEST_CODE_ACCEPT_IMAGE);
			} else {
				Toast.makeText(getActivity(), getString(R.string.failedToLoad),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getActivity(), getString(R.string.processCancelled),
					Toast.LENGTH_SHORT).show();
		}
	}
public String getImagePath(Bitmap image)
{
	  File photo = new File(getActivity().getFilesDir(), "photo_snicker.jpg");
      if (photo.exists()) {
          photo.delete();
      }
      FileOutputStream fos = null;

      try {
          fos = new FileOutputStream(photo);
          image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
      } catch (java.io.IOException e) {
          Log.e("PictureDemo", "Exception in photoCallback", e);
      } finally {

          if (fos != null)
              try {
                  image.recycle();
                  fos.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
      }

      return photo.getAbsolutePath().toString();      
}
	private void cropImageResult(int resultCode, Intent data) {
		Log.d("Yogesh", "RESULT CODE :" + resultCode + " - "
				+ getActivity().RESULT_OK);
//		if (resultCode == getActivity().RESULT_OK) {
		Uri selectedImage = data.getData();
		String cropImagePath = "";
//		String cropImagePathOne = "";
				if (selectedImage != null) {
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			 cropImagePath = cursor.getString(columnIndex);
			cursor.close();
			if (cropImagePath != null) {
				// TODO pass image for face work
				Log.d("Yogesh", "Crop Image path:" + cropImagePath);
			
			
		Bitmap image = ImageHelper.decodeFile(
				cropImagePath,
				ImageHelper.getDeviceWidth(getActivity()),
				ImageHelper.getDeviceHeight(getActivity()),
				ImageHelper.ScalingLogic.FIT);
		
//			 cropImagePathOne =getImagePath(image);// = data.getStringExtra("cropImagePath");
				goToAvatarMaker(cropImagePath);
			} else {
				Toast.makeText(getActivity(), getString(R.string.failedToLoad),
						Toast.LENGTH_SHORT).show();
			}}
//		} else {
//			Toast.makeText(getActivity(), getString(R.string.processCancelled),
//					Toast.LENGTH_SHORT).show();
//		}
	}

	/*
	 * private void gallerySelectionResult(int resultCode, Intent data) {
	 * Log.d("Yogesh", "RESULT CODE :" + resultCode + " - " +
	 * getActivity().RESULT_OK); if (resultCode == getActivity().RESULT_OK) {
	 * Uri pickedImage = data.getData(); if (pickedImage != null) {
	 * startImageCroppingActivityForResult(pickedImage); } else {
	 * Toast.makeText(getActivity(), getString(R.string.failedToLoad),
	 * Toast.LENGTH_SHORT).show(); } } else { Toast.makeText(getActivity(),
	 * getString(R.string.noSelectionMade), Toast.LENGTH_SHORT).show(); } }
	 */

	private void goToAvatarMaker(String imagePath) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		Fragment_PictureAvatarMaker fpam = new Fragment_PictureAvatarMaker();
		Bundle b = new Bundle();
		b.putString("selectedImagePath", imagePath);
		fpam.setArguments(b);
		fm.popBackStack(fpam.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		fm.beginTransaction().replace(R.id.container, fpam, fpam.TAG)
				.addToBackStack(fpam.TAG).commit();
		// fm.beginTransaction().replace(R.id.container,
		// fpam).addToBackStack(null).commit();
	}

}
