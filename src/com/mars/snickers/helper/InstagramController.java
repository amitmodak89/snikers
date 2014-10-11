package com.mars.snickers.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;

/**
 * Created by malpani on 9/10/14.
 */
public class InstagramController {
    public void uploadImage(Context context, String imagePath, String imageName, String imageDescription)
    {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (intent != null)
        {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.instagram.android");
            try {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), imagePath, imageName, imageDescription)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            shareIntent.setType("image/jpeg");

            context.startActivity(shareIntent);
        }
        else
        {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id="+"com.instagram.android"));
            context.startActivity(intent);
        }

    }
}
