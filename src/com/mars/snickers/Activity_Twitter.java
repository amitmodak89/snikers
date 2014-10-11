package com.mars.snickers;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mars.snickers.listners.ItwitterListner;
import com.mars.snickers.prefs.Prefs;

import java.io.File;
import java.util.Calendar;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by malpani on 9/10/14.
 */
public class Activity_Twitter extends Activity_Facebook implements ItwitterListner {

    Twitter twitter;
    RequestToken requestToken = null;
    AccessToken accessToken;
    WebView web;
    String oauth_url, oauth_verifier, profile_url;
    Dialog auth_dialog;
    ProgressDialog progress;
    private String msg;
    private File imageFile;
    private boolean forPostTweet = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(Activity_Twitter.this);
        progress.setMessage("Please wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public File getImageFile()
    {
        return imageFile;
    }
    public void setImageFile(File file)
    {
        imageFile = file;
    }

    public void setTwitterObject() {
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(getString(R.string.twitterAppKey), getString(R.string.twitterSecretKey));
    }

    @Override
    public void postTweet(String message) {
        forPostTweet = true;
        if (twitter == null) {
            setTwitterObject();
        }



        if(Prefs.getLocationIndex(Activity_Twitter.this)==0) {
            setMessage(getString(R.string.tweet_t));
        } else {
            setMessage(getString(R.string.tweet));
        }
        if (Prefs.getTwitterAccessToken(Activity_Twitter.this) != null & Prefs.getTwitterAccessTokenSecret(Activity_Twitter.this) != null) {
            new PostTweet().execute(getMessage());
        } else {
            new TokenGet().execute();
        }
    }

    @Override
    public void postImageTweet(String message, File imageFile) {
        forPostTweet = false;
        if (twitter == null) {
            setTwitterObject();
        }
        if(Prefs.getLocationIndex(Activity_Twitter.this)==0) {
            setMessage(getString(R.string.tweet_t));
        } else {
            setMessage(getString(R.string.tweet));
        }
        setImageFile(imageFile);
        if (Prefs.getTwitterAccessToken(Activity_Twitter.this) != null & Prefs.getTwitterAccessTokenSecret(Activity_Twitter.this) != null) {
            new PostTweetWithImage(getMessage(), imageFile).execute();
        } else {
            new TokenGet().execute();
        }

    }

    private class TokenGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            progress.show();
            if (twitter == null) {
                setTwitterObject();
            }

        }

        @Override
        protected String doInBackground(String... args) {
            try {
                requestToken = twitter.getOAuthRequestToken();
                oauth_url = requestToken.getAuthorizationURL();
            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return oauth_url;
        }

        @Override
        protected void onPostExecute(String oauth_url) {
            progress.hide();
            if (oauth_url != null) {
                Log.e("URL", oauth_url);
                auth_dialog = new Dialog(Activity_Twitter.this);
                auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                auth_dialog.setContentView(R.layout.twitter_auth_dialog);
                web = (WebView) auth_dialog.findViewById(R.id.tad_wv_auth);
                web.getSettings().setJavaScriptEnabled(true);
                web.loadUrl(oauth_url);
                web.setWebViewClient(new WebViewClient() {
                    boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("oauth_verifier") && authComplete == false) {
                            authComplete = true;
                            Log.e("Url", url);
                            Uri uri = Uri.parse(url);
                            oauth_verifier = uri.getQueryParameter("oauth_verifier");
                            auth_dialog.dismiss();
                            new AccessTokenGet().execute();
                        } else if (url.contains("denied")) {
                            auth_dialog.dismiss();
                            Toast.makeText(Activity_Twitter.this, "Sorry !, Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setCancelable(true);
            } else {
                Toast.makeText(Activity_Twitter.this, "Sorry !, Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AccessTokenGet extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
                Prefs.setTwitterAccessToken(Activity_Twitter.this, accessToken.getToken());
                Prefs.setTwitterAccessTokenSecret(Activity_Twitter.this, accessToken.getTokenSecret());
                User user = twitter.showUser(accessToken.getUserId());
                profile_url = user.getOriginalProfileImageURL();
//                edit.putString("NAME", user.getName());
//                edit.putString("IMAGE_URL", user.getOriginalProfileImageURL());
//                edit.commit();
            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            progress.hide();
            if (response) {
                if(forPostTweet) {
                    new PostTweet().execute(getMessage());
                } else {
                    new PostTweetWithImage(getMessage(), getImageFile());
                }
            }
        }
    }


    private class PostTweet extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Posting tweet ...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
        }

        protected String doInBackground(String... args) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(getString(R.string.twitterAppKey));
            builder.setOAuthConsumerSecret(getString(R.string.twitterSecretKey));
            AccessToken accessToken = new AccessToken(Prefs.getTwitterAccessToken(Activity_Twitter.this), Prefs.getTwitterAccessTokenSecret(Activity_Twitter.this));
            Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
            try {
                twitter4j.Status response = twitter.updateStatus(args[0]);
                return response.toString();
            } catch (TwitterException e) {
                Toast.makeText(Activity_Twitter.this, getString(R.string.error) + ": " + e.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        protected void onPostExecute(String res) {
            setMessage(null);
            progress.dismiss();
            if (res != null) {
                Toast.makeText(Activity_Twitter.this, "Tweet Sucessfully Posted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class PostTweetWithImage extends AsyncTask<Void, String, String> {
        String message = null;
        File image = null;

        public PostTweetWithImage(String message, File image) {
            this.message = message;
            this.image = image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Posting tweet ...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
        }

        protected String doInBackground(Void... voids) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(getString(R.string.twitterAppKey));
            builder.setOAuthConsumerSecret(getString(R.string.twitterSecretKey));
            AccessToken accessToken = new AccessToken(Prefs.getTwitterAccessToken(Activity_Twitter.this), Prefs.getTwitterAccessTokenSecret(Activity_Twitter.this));
            Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
            try {
                StatusUpdate status = new StatusUpdate(message);
                status.setMedia(image);
                twitter4j.Status response = twitter.updateStatus(status);
                return response.toString();
            } catch (TwitterException e) {
                Toast.makeText(Activity_Twitter.this, getString(R.string.error) + ": " + e.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        protected void onPostExecute(String res) {
            setMessage(null);
            setImageFile(null);
            progress.dismiss();
            if (res != null) {
                Toast.makeText(Activity_Twitter.this, "Tweet Sucessfully Posted", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
