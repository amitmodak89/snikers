package com.mars.snickers;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.mars.snickers.helper.FontManager;
import com.mars.snickers.helper.SoapController;
import com.mars.snickers.listners.IfacebookListener;
import com.mars.snickers.listners.ItwitterListner;
import com.mars.snickers.prefs.Prefs;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_ThankYou extends Fragment {
    public final String TAG = "Fragment_ThankYou";


    private Button playGame, home;
    private TextView header, name, message;//, share;
    private ImageView fb, twtr;
    private IfacebookListener fbListener;
    private ItwitterListner twitterListner;
    private ProgressDialog pd;
    private Request.GraphUserCallback requestGraphUserCallback;


    public Fragment_ThankYou() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fbListener = (Activity_Facebook) activity;
            twitterListner = (Activity_Twitter) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IfacebookListener");
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thank_you, container, false);
        playGame = (Button) view.findViewById(R.id.fty_btn_playGame);
        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment_Register fr = new Fragment_Register();
                fm.popBackStack(fr.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction().replace(R.id.container, fr, fr.TAG).addToBackStack(fr.TAG).commit();
//                fm.beginTransaction().replace(R.id.container, fr).addToBackStack(null).commit();
            }
        });

        home = (Button) view.findViewById(R.id.fty_btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Frament_InitialScreen fis = new Frament_InitialScreen();
                fm.popBackStack(fis.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction().replace(R.id.container, fis, fis.TAG).addToBackStack(fis.TAG).commit();
//                fm.beginTransaction().replace(R.id.container, fis).addToBackStack(null).commit();
            }
        });

        header = (TextView) view.findViewById(R.id.fty_tv_header);
        name = (TextView) view.findViewById(R.id.fty_tv_name_amit);
        name.setText(Prefs.getUserName(getActivity())+" "+Prefs.getUserSurname(getActivity()) );
        message = (TextView) view.findViewById(R.id.fty_tv_thankYouMessage);
//        share = (TextView) view.findViewById(R.id.fty_tv_share);

        fb = (ImageView) view.findViewById(R.id.fty_iv_facebook);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fbListener.getFacebookSessionState()) {
                    openFacebookSession();
                } else {
                    fbListener.setMessage("Testing");
                }
            }
        });
        twtr = (ImageView) view.findViewById(R.id.fty_iv_twitter);
        twtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterListner.postTweet("Testing");
            }
        });


        FontManager.setFont(getActivity(), playGame, null, FontManager.FontType.FRANKLIN);
        FontManager.setFont(getActivity(), home, null, FontManager.FontType.FRANKLIN);
        FontManager.setFont(getActivity(), name, null, FontManager.FontType.HERMESTR_BOLD);
        FontManager.setFont(getActivity(), header, null, FontManager.FontType.FRANKLIN);
        FontManager.setFont(getActivity(), message, null, FontManager.FontType.FRANKLIN);
//        FontManager.setFont(getActivity(), share, null, FontManager.FontType.FRANKLIN);

        pd = new ProgressDialog(getActivity());
        pd.setTitle("");
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.progressMessage));
        requestGraphUserCallback = new RequestGraphUserCallback();

        return view;
    }

    private Session openActiveSession(Activity activity, boolean allowLoginUI, List permissions, Session.StatusCallback callback) {
        Session.OpenRequest openRequest = new Session.OpenRequest(activity).setPermissions(permissions).setCallback(callback);
        Session session = new Session.Builder(activity).build();
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
            Session.setActiveSession(session);
            session.openForRead(openRequest);
            return session;
        }
        return null;
    }

    private void openFacebookSession() {
        List permissions = Arrays.asList("email", "user_location", "user_friends", "public_profile");
        openActiveSession(getActivity(), true, permissions, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (exception != null) {
                    Log.d("Facebook", exception.getMessage());
                }

                if (state.isOpened()) {
                    Request.newMeRequest(session, requestGraphUserCallback).executeAsync();
                } else {
                    Log.d("Facebook", "Closed");
                }
                Log.d("Facebook", "Session State: " + session.getState());
// you can make request to the /me API or do other stuff like post, etc. here
            }
        });
    }

    private class RequestGraphUserCallback implements Request.GraphUserCallback {

        // callback after Graph API response with user object
        @Override
        public void onCompleted(GraphUser user, Response response) {
            if (user != null) {
                fbListener.setUser(user);
                Log.d("Facebook", "RequestGraph successful");
                new sendFacebookUserDetails(getActivity(), user).execute();
            } else {
                Log.d("Facebook", "RequestGraph Failed");
            }
        }
    }

    private class sendFacebookUserDetails extends AsyncTask<Void, Void, String> {
        GraphUser user;
        Context context;

        public sendFacebookUserDetails(Context context, GraphUser user) {
            this.user = user;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }


        @Override
        protected String doInBackground(Void... voids) {

            InputStream input = null;
            Bitmap bitmap;
            ByteArrayOutputStream byteArrayOutputStream = null;
            String fb_image_base64 = null;


            try {
                URL url = new URL("https://graph.facebook.com/" + user.getId() + "/picture?type=small");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                HttpURLConnection.setFollowRedirects(HttpURLConnection.getFollowRedirects());
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                fb_image_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (input != null)
                    try {
                        byteArrayOutputStream.close();
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            PropertyInfo PI_image = SoapController.generatePropertyInfo("fbphoto", fb_image_base64 == null ? "" : fb_image_base64, String.class);
            PropertyInfo PI_name = SoapController.generatePropertyInfo("fbname", user.getFirstName() + " " + user.getLastName(), String.class);
            PropertyInfo PI_id = SoapController.generatePropertyInfo("fbid", user.getId(), String.class);
            PropertyInfo PI_username = SoapController.generatePropertyInfo("fbusername", user.getUsername(), String.class);
            PropertyInfo PI_gender = SoapController.generatePropertyInfo("fbgender", user.getProperty("gender").toString(), String.class);
            PropertyInfo PI_email = SoapController.generatePropertyInfo("fbemail", user.getProperty("email").toString(), String.class);
            PropertyInfo PI_fbLink = SoapController.generatePropertyInfo("fblink", user.getLink(), String.class);
            PropertyInfo PI_fbLocation = null;
            try {
                PI_fbLocation = SoapController.generatePropertyInfo("sLocation", user.getInnerJSONObject().getJSONObject("location").getString("id"), String.class);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Facebook", user.getInnerJSONObject().toString());
                PI_fbLocation = SoapController.generatePropertyInfo("sLocation", "", String.class);
            }
            PropertyInfo PI_language = SoapController.generatePropertyInfo("sLanguage", user.getProperty("locale"), String.class);
            PropertyInfo PI_mobile = SoapController.generatePropertyInfo("strMob", "", String.class);
            PropertyInfo PI_ipAddress = SoapController.generatePropertyInfo("sIPAddress", "", String.class);

            ArrayList<PropertyInfo> piList = new ArrayList<PropertyInfo>();
            piList.add(PI_id);
            piList.add(PI_name);
            piList.add(PI_username);
            piList.add(PI_email);
            piList.add(PI_gender);
            piList.add(PI_image);
            piList.add(PI_fbLink);
            piList.add(PI_fbLocation);
            piList.add(PI_language);
            piList.add(PI_mobile);
            piList.add(PI_ipAddress);

            String response = SoapController.makeRequest(SoapController.SoapConstant.METHOD_AuthenticateFacebookUser, piList);
            Log.d("Hit", "Response: " + response);
            if (response != null)
                Prefs.setUserId(context, response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (s != null) {
                Log.d("Facebook", "Send Message Successful");
                fbListener.setMessage("Testing");
            } else {
                Log.d("Facebook", "Send Message Failed");
            }
        }
    }

    private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;

	/*
	 * private UiLifecycleHelper uiHelper;
	 * 
	 * @Override public void onActivityResult(int requestCode, int resultCode,
	 * Intent data) { super.onActivityResult(requestCode, resultCode, data);
	 * 
	 * uiHelper.onActivityResult(requestCode, resultCode, data, new
	 * FacebookDialog.Callback() {
	 * 
	 * @Override public void onError(FacebookDialog.PendingCall pendingCall,
	 * Exception error, Bundle data) { Log.e("Activity",
	 * String.format("Error: %s", error.toString())); }
	 * 
	 * @Override public void onComplete(FacebookDialog.PendingCall pendingCall,
	 * Bundle data) { Log.i("Activity", "Success!"); } }); }
	 * 
	 * @Override public void onResume() { super.onResume(); uiHelper.onResume();
	 * }
	 * 
	 * 
	 * @Override public void onPause() { super.onPause(); uiHelper.onPause(); }
	 * 
	 * @Override public void onDestroy() { super.onDestroy();
	 * uiHelper.onDestroy(); }
	 */
	private void publishStory() {
		Session session = null;
		try {
			session = Session.getActiveSession();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
			// final List<String> PERMISSIONS =
			// Arrays.asList("publish_actions");
			final int REAUTH_ACTIVITY_CODE = 100;
			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				Session.NewPermissionsRequest reauthRequest = new Session.NewPermissionsRequest(
						this, PERMISSIONS).setRequestCode(REAUTH_ACTIVITY_CODE);
				reauthRequest.setRequestCode(REAUTH_ACTIVITY_CODE);
				reauthRequest
						.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
				// reauthRequest.setCallback((StatusCallback) getActivity());
				session.requestNewPublishPermissions(reauthRequest);
				// session.reauthorizeForPublish(reauthRequest);
				return;
			}

			if (session != null) {

				// Check for publish permissions
				// List<String> permissions = session.getPermissions();
				if (!isSubsetOf(PERMISSIONS, permissions)) {
					boolean pendingPublishReauthorization = true;
					Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
							this, PERMISSIONS);
					session.requestNewPublishPermissions(newPermissionsRequest);
					return;
				}

				Bundle postParams = new Bundle();
				postParams.putString("name",
						getString(R.string.fb_share_title));
				postParams.putString("caption", "Snikers");
				postParams.putString("description",
						getString(R.string.fb_share_desc));
				postParams.putString("link", "https://resultrix.com");
				postParams.putString("picture", "");

				Request.Callback callback = new Request.Callback() {
					public void onCompleted(Response response) {
						JSONObject graphResponse = response.getGraphObject()
								.getInnerJSONObject();
						String postId = null;
						try {
							postId = graphResponse.getString("id");
						} catch (JSONException e) {
							Log.i(TAG, "JSON error " + e.getMessage());
						}
						FacebookRequestError error = response.getError();
						if (error != null) {
							Toast.makeText(
									getActivity().getApplicationContext(),
									error.getErrorMessage(), Toast.LENGTH_SHORT)
									.show();
						} else {
							Toast.makeText(
									getActivity().getApplicationContext(),
									postId, Toast.LENGTH_LONG).show();
						}
					}
				};

				Request request = new Request(session, "me/feed", postParams,
						HttpMethod.POST, callback);

				RequestAsyncTask task = new RequestAsyncTask(request);
				task.execute();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
