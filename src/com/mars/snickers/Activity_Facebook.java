package com.mars.snickers;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.mars.snickers.helper.ImageHelper;
import com.mars.snickers.helper.SoapController;
import com.mars.snickers.listners.IfacebookListener;
import com.mars.snickers.prefs.Prefs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;


public class Activity_Facebook extends ActionBarActivity implements IfacebookListener {

    private static final String[] PERMISSION = new String[]{"publish_actions", "email", "user_location", "user_friends", "user_birthday", "publish_stream"};
    private final String PENDING_ACTION_BUNDLE_KEY = "com.mars.snickers:PendingAction";
    private PendingAction pendingAction = PendingAction.NONE;

    private GraphUser user;

    private boolean canPresentShareDialog;
    private boolean canPresentShareDialogWithPhotos;
    private String imageUploadPath, uploadMessage, uploadTitle;

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    private interface GraphObjectWithId extends GraphObject {
        String getId();
    }

    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            Prefs.setFacebookLoginStatus(Activity_Facebook.this, onSessionStateChange(session, state, exception));
            List<String> permissions = session.getPermissions();
            for (String per : permissions) {
                Log.d("Facebook", "Permission: " + per);
            }
        }
    };

    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d("Facebook", String.format("Error: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d("Facebook", "Success!");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }

        // Can we present the share dialog for regular links?
        canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = FacebookDialog.canPresentShareDialog(this,
                FacebookDialog.ShareDialogFeature.PHOTOS);

    }

    public GraphUser getUser() {
        return user;
    }

    @Override
    public void handlePendingFbAction() {
        handlePendingAction();
    }

    @Override
    public Session getSession() {
        Session session = Session.getActiveSession();
        if (session != null && !session.getPermissions().contains("publish_actions")) {
            session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
        } else {
            Log.d("Facebook", "Session should have publish actions");
        }

        return session;
    }

    @Override
    public void setImagePath(String imagePath) {
        this.imageUploadPath = imagePath;
        postPhoto(imageUploadPath);
    }

    @Override
    public void setMessage(String message) {
        if (Prefs.getLocationIndex(Activity_Facebook.this) == 0) {
            this.uploadTitle = getString(R.string.fb_share_title_t);
            this.uploadMessage = (getString(R.string.fb_share_desc_t));
        } else {
            this.uploadTitle = getString(R.string.fb_share_title);
            this.uploadMessage = (getString(R.string.fb_share_desc));
        }
        postStatusUpdate();
//        publishFeedDialog();
    }

    @Override
    public boolean getFacebookSessionState() {
        return Session.getActiveSession().getState().isOpened();
    }


    public void setUser(GraphUser user) {
        this.user = user;
        if (user != null)
            Log.d("Yogesh", "User: " + user);
    }

    private boolean onSessionStateChange(Session session, SessionState state, Exception exception) {

        boolean result = false;
        if (state.isOpened()) {
            result = true;
        } else if (state.isClosed()) {
            result = false;
        }
        Log.d("Facebook", "Facebook Status: " + result);
        return result;
    }

    @SuppressWarnings("incomplete-switch")
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_PHOTO:
                if (imageUploadPath != null)
                    postPhoto(imageUploadPath);
                break;
            case POST_STATUS_UPDATE:
                if (uploadMessage != null)
                    postStatusUpdate();
                break;
        }
    }

    private void showPublishResult(GraphObject result, FacebookRequestError error) {
        String title = null;
        String alertMessage = null;
        if (error == null) {
            title = getString(R.string.ok);
            String id = result.cast(GraphObjectWithId.class).getId();
            alertMessage = "ID: " + id;
        } else {
            title = getString(R.string.cancelled);
            alertMessage = error.getErrorMessage();
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void postPhoto(String path) {
        Log.d("Facebook", path);
        Bitmap image = ImageHelper.decodeFile(path, ImageHelper.getDeviceWidth(Activity_Facebook.this), ImageHelper.getDeviceHeight(Activity_Facebook.this), ImageHelper.ScalingLogic.FIT);
        if (canPresentShareDialogWithPhotos) {
            Log.d("Facebook", "if");
            FacebookDialog shareDialog = createShareDialogBuilderForPhoto(image).build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else if (hasPublishPermission()) {
            Log.d("Facebook", "elseif");
            Request request = Request.newUploadPhotoRequest(getSession(), image, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {

//                    showPublishResult(response.getGraphObject(), response.getError());
                    imageUploadPath = null;
                }

            });
            Bundle params = request.getParameters();
            params.putString("title", "");
            params.putString("message", "Your Caption Here");
            request.setParameters(params);
            request.executeAsync();
        } else {
            Log.d("Facebook", "else");
            pendingAction = PendingAction.POST_PHOTO;
        }
    }

    private void postStatusUpdate() {
        if (canPresentShareDialog) {
            FacebookDialog shareDialog = createShareDialogBuilderForLink().build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else if (user != null && hasPublishPermission()) {
            Request request = Request
                    .newStatusUpdateRequest(getSession(), uploadMessage, null, null, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
//                            showPublishResult(response.getGraphObject(), response.getError());
                            Toast.makeText(Activity_Facebook.this, getString(R.string.sucessfulShare), Toast.LENGTH_SHORT).show();
                            uploadMessage = null;
                        }
                    });
            request.executeAsync();
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
            Log.d("Facebook", "No permission to post");
        }
    }

    private FacebookDialog.ShareDialogBuilder createShareDialogBuilderForLink() {
        return new FacebookDialog.ShareDialogBuilder(this)
                .setName(uploadTitle)
                .setDescription(uploadMessage);
//                .setLink("http://developers.facebook.com/android");
    }

    private FacebookDialog.PhotoShareDialogBuilder createShareDialogBuilderForPhoto(Bitmap... photos) {
        return new FacebookDialog.PhotoShareDialogBuilder(this)
                .addPhotos(Arrays.asList(photos));
    }


    private void publishFeedDialog() {
        Bundle params = new Bundle();
//        params.putString("name", "Snickers");
        params.putString("caption", uploadTitle);
        params.putString("description", uploadMessage);
//        params.putString("link", "https://developers.facebook.com/android");
//        params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(Activity_Facebook.this,
                        getSession(),
                        params)).build();
        feedDialog.show();
    }

    private boolean hasPublishPermission() {
        boolean result = false;
        Session session = getSession();

        Log.d("Facebook", "Session is " + session);
        for (String permissions : session.getPermissions()) {
            Log.d("Facebook", "Session permission " + permissions);
        }

        result = session != null && session.getPermissions().contains("publish_actions");

        Log.d("Facebook", "Session result " + result);
        return result;
    }

    private void performPublish(PendingAction action, boolean allowNoSession) {
        Session session = getSession();
        if (session != null) {
            pendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else if (session.isOpened()) {
                // We need to get new permissions, then complete the action when we get called back.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
                return;
            }
        }

        if (allowNoSession) {
            pendingAction = action;
            handlePendingAction();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);

//        updateUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }


}
