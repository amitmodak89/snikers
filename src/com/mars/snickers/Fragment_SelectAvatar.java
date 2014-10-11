package com.mars.snickers;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mars.snickers.helper.FontManager;
import com.mars.snickers.helper.ImageHelper;
import com.mars.snickers.helper.SoapController;
import com.mars.snickers.prefs.Prefs;

import org.ksoap2.serialization.PropertyInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_SelectAvatar extends Fragment {

    public final String TAG = "Fragment_SelectAvatar";

    private Button upload;
    private ImageView proceed;
    TextView header, subHeader, selectAvatar, uploadHeader;
    private boolean isUploaded = false;
    private ProgressDialog pd;
    private RadioGroup avatars;
    private final int REQUEST_CODE_FILE_GALLERY_SELECTION = 212;


    public Fragment_SelectAvatar() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_avatar, container, false);
        proceed = (ImageView) view.findViewById(R.id.btn_proceed_to_game);
//        FontManager.setFont(getActivity(), proceed, null, FontManager.FontType.FRANKLIN);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (avatars.getCheckedRadioButtonId() != -1) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment_ThankYou fty = new Fragment_ThankYou();
                    fm.popBackStack(fty.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fm.beginTransaction().replace(R.id.container, fty, fty.TAG).addToBackStack(fty.TAG).commit();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.selectAvatar), Toast.LENGTH_SHORT).show();
                }
            }
        });

        upload = (Button) view.findViewById(R.id.fsa_btn_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGalleryExplorerActivityForResult();
            }
        });
        FontManager.setFont(getActivity(), upload, null, FontManager.FontType.FRANKLIN);

        header = (TextView) view.findViewById(R.id.fsa_tv_header);
        FontManager.setFont(getActivity(), header, null, FontManager.FontType.FRANKLIN);
        subHeader = (TextView) view.findViewById(R.id.fsa_tv_subHeader);
        FontManager.setFont(getActivity(), subHeader, null, FontManager.FontType.FRANKLIN);
        selectAvatar = (TextView) view.findViewById(R.id.fsa_tv_selectChar);
        FontManager.setFont(getActivity(), selectAvatar, null, FontManager.FontType.HERMESTR_BOLD);
        uploadHeader = (TextView) view.findViewById(R.id.fsa_tv_upload);
        FontManager.setFont(getActivity(), uploadHeader, null, FontManager.FontType.HERMESTR_BOLD);

        pd = new ProgressDialog(getActivity());
        pd.setTitle("");
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.progressMessage));

        avatars = (RadioGroup) view.findViewById(R.id.fsa_rg_avatars);

        return view;
    }

    private void startGalleryExplorerActivityForResult() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_CODE_FILE_GALLERY_SELECTION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_FILE_GALLERY_SELECTION:
                gallerySelectionResult(resultCode, data);
                break;
        }
    }

    private void gallerySelectionResult(int resultCode, Intent data) {
        Log.d("Yogesh", "RESULT CODE :" + resultCode + " - " + getActivity().RESULT_OK);
        if (resultCode == getActivity().RESULT_OK) {
            Uri pickedImage = data.getData();
            if (pickedImage != null) {
                new UploadImageToServer().execute(pickedImage);
            } else {
                Toast.makeText(getActivity(), getString(R.string.failedToLoad), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.noSelectionMade), Toast.LENGTH_SHORT).show();
        }
    }

    private class UploadImageToServer extends AsyncTask<Uri, Void, String> {

        Bitmap bitmap;
        ByteArrayOutputStream byteArrayOutputStream = null;
        String image_base64 = null, responseData = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(Uri... uris) {
            try {
                bitmap = ImageHelper.decodeMedia(uris[0], 2 * ImageHelper.getDeviceWidth(getActivity()), 2 * ImageHelper.getDeviceHeight(getActivity()), getActivity(), ImageHelper.ScalingLogic.FIT);
                byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                image_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (Exception e) {
                image_base64 = null;
            } finally {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {

                }
            }
            if (image_base64 != null) {
                int index = avatars.indexOfChild(avatars.findViewById(avatars.getCheckedRadioButtonId()));
                PropertyInfo PI_userId = SoapController.generatePropertyInfo("iUserID", Prefs.getUserId(getActivity()), String.class);
                PropertyInfo PI_imagePath = SoapController.generatePropertyInfo("sImagePath", null, String.class);
                PropertyInfo PI_imageCaption = SoapController.generatePropertyInfo("sImageCaption", null, String.class);
                PropertyInfo PI_location = SoapController.generatePropertyInfo("sLanguage", getResources().getStringArray(R.array.locations)[Prefs.getLocationIndex(getActivity())], String.class);
                PropertyInfo PI_language = SoapController.generatePropertyInfo("sLocation", getResources().getStringArray(R.array.locations_country)[Prefs.getLocationIndex(getActivity())], String.class);
                PropertyInfo PI_ipAddress = SoapController.generatePropertyInfo("sIPAddress", null, String.class);
                PropertyInfo PI_isPlay = SoapController.generatePropertyInfo("IsPlay", null, String.class);
                PropertyInfo PI_playedFrom = SoapController.generatePropertyInfo("PlayedFrom", "Android App", String.class);
                PropertyInfo PI_char = SoapController.generatePropertyInfo("Character", getResources().getStringArray(R.array.avatarNames)[index], String.class);
                PropertyInfo PI_image = SoapController.generatePropertyInfo("ImageByte64", image_base64, String.class);

                ArrayList<PropertyInfo> piList = new ArrayList<PropertyInfo>();
                piList.add(PI_userId);
                piList.add(PI_imagePath);
                piList.add(PI_imageCaption);
                piList.add(PI_ipAddress);
                piList.add(PI_isPlay);
                piList.add(PI_playedFrom);
                piList.add(PI_char);
                piList.add(PI_image);
                piList.add(PI_location);
                piList.add(PI_language);

                responseData = SoapController.makeRequest(SoapController.SoapConstant.METHOD_UploadPicture, piList);
                Log.d("Hit", "Response: " + responseData);
            }

            return responseData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (s != null) {
                isUploaded = true;
            } else {
                Toast.makeText(getActivity(), getString(R.string.failedToLoad), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
