package com.mars.snickers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.mars.snickers.adapter.Adapter_LeftMenu;
import com.mars.snickers.helper.LocaleManager;
import com.mars.snickers.helper.MasterLayout;
import com.mars.snickers.helper.SoapController;
import com.mars.snickers.listners.IleftMenuListner;
import com.mars.snickers.prefs.Prefs;

public class Activity_Super_bakup extends Activity_Twitter implements
		IleftMenuListner {

	private MasterLayout masterLayout;
	private ListView leftMenu;
	private ImageView menuButton;
	private Adapter_LeftMenu adapter;
	private String[] menuItems;
	protected ApplicationController applicationController;
	private ProgressDialog pd;
	private Request.GraphUserCallback requestGraphUserCallback;

	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	protected static final String TAG = "Activity Super";
	private boolean pendingPublishReauthorization = false;
	private UiLifecycleHelper uiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		masterLayout = (MasterLayout) this.getLayoutInflater().inflate(
				R.layout.activity_super, null);
		setContentView(masterLayout);
		applicationController = (ApplicationController) getApplication();
		menuItems = getResources().getStringArray(R.array.left_menu_items);
		leftMenu = (ListView) findViewById(R.id.as_lv_menu);
		leftMenu.setVerticalScrollBarEnabled(false);
		leftMenu.setHorizontalScrollBarEnabled(false);

		uiHelper = new UiLifecycleHelper(this, null);
		uiHelper.onCreate(savedInstanceState);

		TypedArray imgs = getResources().obtainTypedArray(
				R.array.left_menu_items);
		TypedArray imgs_background = getResources().obtainTypedArray(
				R.array.left_menu_background);
		adapter = new Adapter_LeftMenu(this, imgs, imgs_background);
		leftMenu.setAdapter(adapter);
		leftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				switch (position) {
				case 0:
					// info
					FragmentManager fm = getSupportFragmentManager();
					Fragment_RevealHungryDetail frhd = new Fragment_RevealHungryDetail();

					fm.beginTransaction()
							.replace(R.id.container, frhd, frhd.TAG)
							.addToBackStack("backFragment").commit();
					break;
				case 1:
					// EN
					LocaleManager.setLanguage(Activity_Super_bakup.this,
							LocaleManager.Language.ENGLISH.getLanguage(),
							LocaleManager.Language.ENGLISH.getCountry());
					break;
				case 2:
					// AR
					LocaleManager.setLanguage(Activity_Super_bakup.this,
							LocaleManager.Language.QATAR.getLanguage(),
							LocaleManager.Language.QATAR.getCountry());
					break;
				case 3:
					// TR
					LocaleManager.setLanguage(Activity_Super_bakup.this,
							LocaleManager.Language.TURKEY.getLanguage(),
							LocaleManager.Language.TURKEY.getCountry());
					break;
				case 4:
					// fb
//					 publishStory();
					if (!getFacebookSessionState()) {
						openFacebookSession();
					} else {
						publishStory();
					}
					/* *
					Bundle params = new Bundle();
					params.putString(
							getResources().getString(R.string.fb_share_title_t),
							getResources().getString(R.string.fb_share_desc_t)); 
					new Request(getSession(), "/me/feed", params,
							HttpMethod.POST, new Request.Callback() {
								public void onCompleted(Response response) {
Toast.makeText(Activity_Super.this,"Achieve", 1).show();
								}
							}).executeAsync();
					/**/

					break;
				case 5:
					// tw
					postTweet(getResources().getString(R.string.tweet_t));
					break;
				case 6:
					// music
					applicationController.playMusic();
					break;
				case 7:
					// copyrights
					FragmentManager fm2 = getSupportFragmentManager();
					Fragment_Promise fp = new Fragment_Promise();
					fm2.beginTransaction().replace(R.id.container, fp, fp.TAG)
							.addToBackStack("backFragment").commit();
					break;
				}

				masterLayout.toggleMenu();
				adapter.notifyDataSetInvalidated();
			}
		});

		menuButton = (ImageView) findViewById(R.id.as_iv_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("Yogesh", "menu button clicked");
				toggleMenu(view);
			}
		});

		// AMIT FRAGMENT CHANGE added replace insted of add
		Frament_InitialScreen fis = new Frament_InitialScreen();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fis).commit();

		pd = new ProgressDialog(this);
		pd.setTitle("");
		pd.setCancelable(false);
		pd.setMessage(getString(R.string.progressMessage));
		requestGraphUserCallback = new RequestGraphUserCallback();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	public void toggleMenu(View v) {
		masterLayout.toggleMenu();
	}

	@Override
	public void onBackPressed() {
		if (masterLayout.isMenuShown()) {
			masterLayout.toggleMenu();
		} else {
			super.onBackPressed();
			FragmentManager fm = this.getSupportFragmentManager();
			int i = fm.getBackStackEntryCount();
			if (i < 0) {
				AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
				alertDlg.setMessage("Are you sure you want to exit?");
				alertDlg.setCancelable(false); // We avoid that the dialong can
												// be
												// cancelled, forcing the user
												// to
												// choose
												// one of the options
				alertDlg.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						});
				alertDlg.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// We do nothing
							}
						});
				alertDlg.create().show();
			}
		}
	}

	public void setFont(ViewGroup group, Typeface font) {
		int count = group.getChildCount();
		View v;
		for (int i = 0; i < count; i++) {
			v = group.getChildAt(i);
			if (v instanceof TextView || v instanceof EditText
					|| v instanceof Button) {
				((TextView) v).setTypeface(font);
			} else if (v instanceof ViewGroup)
				setFont((ViewGroup) v, font);
		}
	}

	private void publishStory() {
		Session session = Session.getActiveSession();
		try {
			
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

				Bundle postParams = new Bundle();
				postParams.putString("name", "Snickers");
				postParams.putString("caption",
						getResources().getString(R.string.fb_share_title_t));
				postParams.putString("description",
						getResources().getString(R.string.fb_share_desc_t));
				postParams.putString("link", "http://snickers.com");
				postParams
						.putString("picture",
								"https://snickerspromo.dessertmoments.com/images/icon.jpg");

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
									Activity_Super_bakup.this.getApplicationContext(),
									error.getErrorMessage(), Toast.LENGTH_SHORT)
									.show();
						} else {
							Toast.makeText(
									Activity_Super_bakup.this.getApplicationContext(),
									"Posted story, Thank You.",
									Toast.LENGTH_LONG).show();
						}
					}
				};

				Request request = new Request(session, "me/feed", postParams,
						HttpMethod.POST, callback);

				RequestAsyncTask task = new RequestAsyncTask(request);
				task.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Session.getActiveSession().onActivityResult(this, requestCode,
		// resultCode, data);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("state", "onResume() called");
		applicationController.startMediaPlayer();

	}

	@Override
	public void onPause() {
		Log.d("state", "onPause() called");
		applicationController.pauseMediaPlayer();
		super.onPause();
	}

	@Override
	public void updateMenu() {
		adapter.notifyDataSetChanged();
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
				URL url = new URL("https://graph.facebook.com/" + user.getId()
						+ "/picture?type=small");
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				HttpURLConnection.setFollowRedirects(HttpURLConnection
						.getFollowRedirects());
				connection.setDoInput(true);
				connection.connect();
				input = connection.getInputStream();
				bitmap = BitmapFactory.decodeStream(input);
				byteArrayOutputStream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100,
						byteArrayOutputStream);
				byte[] byteArray = byteArrayOutputStream.toByteArray();
				fb_image_base64 = Base64.encodeToString(byteArray,
						Base64.DEFAULT);
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

			PropertyInfo PI_image = SoapController.generatePropertyInfo(
					"fbphoto", fb_image_base64 == null ? "" : fb_image_base64,
					String.class);
			PropertyInfo PI_name = SoapController.generatePropertyInfo(
					"fbname", user.getFirstName() + " " + user.getLastName(),
					String.class);
			PropertyInfo PI_id = SoapController.generatePropertyInfo("fbid",
					user.getId(), String.class);
			PropertyInfo PI_username = SoapController.generatePropertyInfo(
					"fbusername", user.getUsername(), String.class);
			PropertyInfo PI_gender = SoapController.generatePropertyInfo(
					"fbgender", user.getProperty("gender").toString(),
					String.class);
			PropertyInfo PI_email = SoapController.generatePropertyInfo(
					"fbemail", user.getProperty("email").toString(),
					String.class);
			PropertyInfo PI_fbLink = SoapController.generatePropertyInfo(
					"fblink", user.getLink(), String.class);
			PropertyInfo PI_fbLocation = null;
			try {
				PI_fbLocation = SoapController.generatePropertyInfo(
						"sLocation",
						user.getInnerJSONObject().getJSONObject("location")
								.getString("id"), String.class);
			} catch (JSONException e) {
				e.printStackTrace();
				Log.d("Facebook", user.getInnerJSONObject().toString());
				PI_fbLocation = SoapController.generatePropertyInfo(
						"sLocation", "", String.class);
			}
			PropertyInfo PI_language = SoapController.generatePropertyInfo(
					"sLanguage", user.getProperty("locale"), String.class);
			PropertyInfo PI_mobile = SoapController.generatePropertyInfo(
					"strMob", "", String.class);
			PropertyInfo PI_ipAddress = SoapController.generatePropertyInfo(
					"sIPAddress", "", String.class);

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

			String response = SoapController
					.makeRequest(
							SoapController.SoapConstant.METHOD_AuthenticateFacebookUser,
							piList);
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
				// setMessage("Testing");
			} else {
				Log.d("Facebook", "Send Message Failed");
			}
//			publishStory();
		}
	}

	private Session openActiveSession(Activity activity, boolean allowLoginUI,
			List permissions, Session.StatusCallback callback) {
		Session.OpenRequest openRequest = new Session.OpenRequest(activity)
				.setPermissions(permissions).setCallback(callback);
		Session session = new Session.Builder(activity).build();
		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())
				|| allowLoginUI) {
			Session.setActiveSession(session);
			session.openForRead(openRequest);
			return session;
		}
		return null;
	}

	private void openFacebookSession() {
		List permissions = Arrays.asList("email", "user_location",
				"user_friends", "public_profile");
		openActiveSession(Activity_Super_bakup.this, true, permissions,
				new Session.StatusCallback() {
					@Override
					public void call(Session session, SessionState state,
							Exception exception) {
						if (exception != null) {
							Log.d("Facebook", exception.getMessage());
						}

						if (state.isOpened()) {
							Request.newMeRequest(session,
									requestGraphUserCallback).executeAsync();
						} else {
							Log.d("Facebook", "Closed");
						}
						Log.d("Facebook",
								"Session State: " + session.getState());
						// you can make request to the /me API or do other stuff
						// like post, etc. here
						publishStory();
					}
				});
	}

	private class RequestGraphUserCallback implements Request.GraphUserCallback {

		// callback after Graph API response with user object
		@Override
		public void onCompleted(GraphUser user, Response response) {
			if (user != null) {
				setUser(user);
				Log.d("Facebook", "RequestGraph successful");
				Bundle params = new Bundle();
				params.putString(
						getResources().getString(R.string.fb_share_title_t),
						getResources().getString(R.string.fb_share_desc_t)); 
				new Request(getSession(), "/me/feed", params,
						HttpMethod.POST, new Request.Callback() {
							public void onCompleted(Response response) {
Toast.makeText(Activity_Super_bakup.this,"Achieve", 1).show();
							}
						}).executeAsync();
				new sendFacebookUserDetails(Activity_Super_bakup.this, user)
						.execute();
			} else {
				Log.d("Facebook", "RequestGraph Failed");
			}
		}
	}

}
