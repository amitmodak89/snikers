package com.mars.snickers.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.widget.Toast;

import com.mars.snickers.R;

/**
 * Created by malpani on 9/5/14.
 */
public class CameraManager {
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public static boolean checkCameraHardware(Context context) {
        boolean result = false;
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            result = true;
        } else {
            // no camera on this device
            Toast.makeText(context, context.getString(R.string.noCamera),Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
