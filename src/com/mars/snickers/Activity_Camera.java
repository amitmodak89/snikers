package com.mars.snickers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

import com.mars.snickers.helper.CameraManager;
import com.mars.snickers.helper.CameraPreview;
import com.mars.snickers.listners.IsurfaceChangeListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class Activity_Camera extends ActionBarActivity implements Camera.PictureCallback, IsurfaceChangeListener {

    protected static final String EXTRA_IMAGE_PATH = "com.mars.snickers.Activity_Camera.EXTRA_IMAGE_PATH";

    private Camera camera;
    private CameraPreview cameraPreview;
    private ImageView cameraSwitch;
    int m_numOfCamera;
    int m_defaultCameraId;
    int m_currentCamera;
    int m_surfaceWidth;
    int m_surfaceHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        
        
        
        cameraSwitch = (ImageView) findViewById(R.id.ac_iv_switch);
        
        cameraSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });

        if (CameraManager.checkCameraHardware(this)) {
            if (camera == null) {
                camera = CameraManager.getCameraInstance();
                m_numOfCamera = Camera.getNumberOfCameras();
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                for (int i = 0; i < m_numOfCamera; ++i) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        m_defaultCameraId = i;
                        m_currentCamera = m_defaultCameraId;
                    }
                }
                if (m_numOfCamera < 1) {
                    cameraSwitch.setVisibility(View.GONE);
                }
                initCameraPreview();
            }
        } else {
            sendResponse(RESULT_CANCELED, null);
        }
        
    }

    private void switchCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        camera = Camera.open((m_currentCamera + 1) % m_numOfCamera);
        m_currentCamera = (m_currentCamera + 1) % m_numOfCamera;

        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size size = getOptimalPreviewSize(sizes, m_surfaceWidth, m_surfaceHeight);

        params.setPreviewSize(size.width, size.height);

        camera.setParameters(params);
        setCameraDisplayOrientation(this, m_currentCamera, camera);

        camera.startPreview();
        try {
            camera.setPreviewDisplay(cameraPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        camera.startPreview();
        super.onResume();

    }

    private void initCameraPreview() {
        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        cameraPreview.init(camera, this);
       
    }

    public void onCaptureClick(View v) {
        camera.takePicture(null, null, this);
    }

    public void onCancelClick(View v) {
        releaseCamera();
        sendResponse(RESULT_CANCELED, null);
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        Log.d("Yogesh", "Picture Taken");

        new SavePhotoTask().execute(bytes);
    }

    private void sendResponse(int response, String path) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("cameraImagePath", path);
        setResult(response, returnIntent);
        finish();
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        m_surfaceWidth = width;
        m_surfaceHeight = height;
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = getOptimalPreviewSize(sizes, width, height);
        params.setPreviewSize(selected.width, selected.height);
        camera.setParameters(params);
        setCameraDisplayOrientation(this, m_currentCamera, camera);
        camera.startPreview();
        switchCamera();
    }

    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... jpeg) {
//            File photo = new File(getFilesDir(), "photo_snicker.jpg");
            File photo = new File(getFilesDir(), "photo_snicker.jpg");
            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(photo.getPath());
                fos.write(jpeg[0]);

                BitmapFactory.Options bounds = new BitmapFactory.Options();
                bounds.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(photo.getAbsolutePath(), bounds);

                BitmapFactory.Options opts = new BitmapFactory.Options();
                Bitmap bm = BitmapFactory.decodeFile(photo.getAbsolutePath(), opts);
                ExifInterface exif = new ExifInterface(photo.getAbsolutePath());
                String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                Log.d("Yogesh", "OrienString- " + orientString);
                int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

               //Trail 1
                int rotationAngle = Surface.ROTATION_180;
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                int degrees = 0;
                switch (rotation) {
                    case Surface.ROTATION_0:
                        degrees = 0;
                        break;
                    case Surface.ROTATION_90:
                        degrees = 90;
                        break;
                    case Surface.ROTATION_180:
                        degrees = 180;
                        break;
                    case Surface.ROTATION_270:
                        degrees = 270;
                        break;
                }
                if(degrees != 0)
                {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(degrees, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
                    Log.d("Yogesh", "Rotation degree " + degrees);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                }


                //Trail 2
                if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
                    Log.d("Yogesh", "Rotation Angle " + rotationAngle);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                }

                fos.close();
            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }

            return photo.getAbsolutePath().toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            sendResponse(RESULT_OK, s);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null)
            camera.stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {


        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }

    private static void setCameraDisplayOrientation(Activity activity,
                                                    int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        /*
    	android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = Surface.ROTATION_90;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        /*
        AMIT-721227157956921
        1475518479372399
        camera.setDisplayOrientation(result);
*/
    }

}
