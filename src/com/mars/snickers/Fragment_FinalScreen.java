package com.mars.snickers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.mars.snickers.helper.FontManager;
import com.mars.snickers.helper.ImageHelper;
import com.mars.snickers.helper.InstagramController;
import com.mars.snickers.helper.SoapController;
import com.mars.snickers.listners.IfacebookListener;
import com.mars.snickers.listners.ItwitterListner;
import com.mars.snickers.prefs.Prefs;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_FinalScreen extends Fragment implements IfacebookListener {

	public final String TAG = "Fragment_FinalScreen";
	private ImageView avatarCreated;
	private TextView youare, whenhungry, share;
	Button inviteFriends, playAgain, save;
	ImageButton fb, twtr, insta, mail;
	private IfacebookListener fbListener;
	private ItwitterListner twitterListner;
	private ProgressDialog pd;
	private Request.GraphUserCallback requestGraphUserCallback;
	private boolean sendMessage = true;
	private UiLifecycleHelper uiHelper;

	public Fragment_FinalScreen() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			fbListener = (Activity_Facebook) activity;
			twitterListner = (Activity_Twitter) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IfacebookListener");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_finalscreen, container,
				false);

		File photo1=new File(Environment.getExternalStorageDirectory().getAbsolutePath(), getString(R.string.userAvatarFileName));
		if (photo1.exists()) {
			photo1.delete();
		}
		
		avatarCreated = (ImageView) view
				.findViewById(R.id.ffs_iv_avatarcreated);

		uiHelper = new UiLifecycleHelper(getActivity(), null);
		uiHelper.onCreate(savedInstanceState);

		Bitmap image = ImageHelper.decodeFile(
				getArguments().getString("avatarImagePath"),
				ImageHelper.getDeviceWidth(getActivity()) / 2,
				ImageHelper.getDeviceHeight(getActivity()) / 2,
				ImageHelper.ScalingLogic.FIT);
		avatarCreated.setImageBitmap(image);
		youare = (TextView) view.findViewById(R.id.ffs_tv_youare);
		FontManager.setFont(getActivity(), youare, null,
				FontManager.FontType.FRANKLIN);
		whenhungry = (TextView) view.findViewById(R.id.ffs_tv_whenHungry);
		FontManager.setFont(getActivity(), whenhungry, null,
				FontManager.FontType.FRANKLIN);

		share = (TextView) view.findViewById(R.id.ffs_tv_share);
		FontManager.setFont(getActivity(), share, null,
				FontManager.FontType.FRANKLIN);
		fb = (ImageButton) view.findViewById(R.id.ffs_btn_facebook);
		fb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pendingPublishReauthorization = false;
//				OpenGraphObject meal = OpenGraphObject.Factory
//						.createForPost("cooking-app:meal");
//				meal.setProperty("title", "Buffalo Tacos");
//				meal.setProperty("image",
//						"http://example.com/cooking-app/images/buffalo-tacos.png");
//				meal.setProperty("url",
//						"https://example.com/cooking-app/meal/Buffalo-Tacos.html");
//				meal.setProperty("description",
//						"Leaner than beef and great flavor.");
//
//				OpenGraphAction action = GraphObject.Factory
//						.create(OpenGraphAction.class);
//				action.setProperty("meal", meal);
//
//				FacebookDialog shareDialog = new FacebookDialog.OpenGraphActionDialogBuilder(
//						getActivity(), action, "cooking-app:cook", "meal")
//						.build();
//				uiHelper.trackPendingDialogCall(shareDialog.present());
//				publishStory();

				if (FacebookDialog.canPresentShareDialog( getActivity().getApplicationContext(), 
	                    FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
	// Publish the post using the Share Dialog
	FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(getActivity())

	.setName("Snickers")
	.setCaption(getResources().getString(R.string.fb_share_title_t))
	.setDescription(getResources().getString(R.string.fb_share_desc_t))
	.setLink("http://snickers.com")
	.setPicture("https://snickerspromo.dessertmoments.com/images/icon.jpg")
	.build();
	
	
	uiHelper.trackPendingDialogCall(shareDialog.present());

	} else {
		if (!getFacebookSessionState()) {
			openFacebookSession();
		} else {
		publishFeedDialog();
		}
	}
				sendMessage = true;
//				if (!fbListener.getFacebookSessionState()) {
//					// openFacebookSession();
//				} else {
//
//					// fbListener.setMessage("Testing");
//				}
			}
		});

		/*
		 * try { adapter = new SocialAuthAdapter(new ResponseListener());
		 * adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook); //
		 * adapter.addCallBack(Provider.TWITTER, //
		 * "http://www.achieverstechnology.com"); //
		 * adapter.addCallBack(Provider.YAMMER, //
		 * "http://www.achieverstechnology.com"); adapter.enable(fb); } catch
		 * (Exception e) { e.printStackTrace(); }
		 */

		twtr = (ImageButton) view.findViewById(R.id.ffs_btn_twitter);
		twtr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				twitterListner
						.postImageTweet("Testing", new File(getActivity()
								.getFilesDir(),
								getString(R.string.userAvatarFileName)));
			}
		});
		insta = (ImageButton) view.findViewById(R.id.ffs_btn_instagram);
		insta.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				File photo = new File(getActivity().getFilesDir(),
						getString(R.string.userAvatarFileName));
				if (photo.exists()) {
					new InstagramController().uploadImage(getActivity(),
							photo.getAbsolutePath(), "Snicker",
							"Snicker Description");
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.fileMissingError),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		mail = (ImageButton) view.findViewById(R.id.ffs_btn_email);
		mail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendMail();
			}
		});
		inviteFriends = (Button) view.findViewById(R.id.ffs_btn_inviteFriends);
		inviteFriends.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendMessage = false;
				if (!fbListener.getFacebookSessionState()) {
					openFacebookSession();
				} else {
					sendInvites();
				}
			}
		});
		playAgain = (Button) view.findViewById(R.id.ffs_btn_playagain);
		FontManager.setFont(getActivity(), playAgain, null,
				FontManager.FontType.FRANKLIN);
		playAgain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				FragmentManager fm = getActivity().getSupportFragmentManager();
				Fragment_GetPicture fgp = new Fragment_GetPicture();
				fm.popBackStack(fgp.TAG,
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
				fm.beginTransaction().replace(R.id.container, fgp, fgp.TAG)
						.addToBackStack(fgp.TAG).commit();
				// fm.beginTransaction().replace(R.id.container,
				// fgp).addToBackStack(null).commit();

			}
		});
		save = (Button) view.findViewById(R.id.ffs_btn_save);
		FontManager.setFont(getActivity(), save, null,
				FontManager.FontType.FRANKLIN);
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				File photo = new File(getActivity().getFilesDir(),
						getString(R.string.userAvatarFileName));
				String path = null;
				if (photo.exists()) {
					path = photo.getAbsolutePath();
				}
				if (path != null)
					new SavePhotoTask().execute(path);
			}
		});

		pd = new ProgressDialog(getActivity());
		pd.setTitle("");
		pd.setCancelable(false);
		pd.setMessage(getString(R.string.progressMessage));
		requestGraphUserCallback = new RequestGraphUserCallback();

		return view;
	}

	private void sendMail() {

		Uri URI = null;
		String emailAddresses[] = { "" };
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent
				.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddresses);
		String title = null, desc = null;
		if (Prefs.getLocationIndex(getActivity()) == 0) {
			title = getString(R.string.fb_share_title_t);
			desc = getString(R.string.fb_share_desc_t);
		} else {
			title = getString(R.string.fb_share_title);
			desc = getString(R.string.fb_share_desc);
		}
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
		
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, desc);
		emailIntent.setType("image/*");
		
		File photo = new File(getActivity().getFilesDir(),
				getString(R.string.userAvatarFileName));
		String path = null;
		if (photo.exists()) {
			path = photo.getAbsolutePath();
		Bitmap bitmap = ImageHelper.decodeFile(path,
				2 * ImageHelper.getDeviceWidth(getActivity()),
				2 * ImageHelper.getDeviceHeight(getActivity()),
				ImageHelper.ScalingLogic.FIT);
//		File photo = new File(getActivity().getExternalFilesDir(
//				Environment.DIRECTORY_PICTURES),
//				getString(R.string.userAvatarFileName));
		File photo1=new File(Environment.getExternalStorageDirectory().getAbsolutePath(), getString(R.string.userAvatarFileName));
		if (photo1.exists()) {
			photo1.delete();
		}
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(photo1);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		} catch (java.io.IOException e) {
			Log.e("PictureDemo", "Exception in photoCallback", e);
		} finally {

			if (fos != null)
				try {
					bitmap.recycle();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		if (photo1.exists()) {
//			URI = Uri.fromFile(photo);
			  URI = Uri.fromFile(photo1);
		}
		if (URI != null)
			emailIntent.putExtra(Intent.EXTRA_STREAM, URI);

		startActivity(emailIntent);
	}
	}
	class SavePhotoTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... path) {
			Bitmap bitmap = ImageHelper.decodeFile(path[0],
					2 * ImageHelper.getDeviceWidth(getActivity()),
					2 * ImageHelper.getDeviceHeight(getActivity()),
					ImageHelper.ScalingLogic.FIT);
//			File photo = new File(getActivity().getExternalFilesDir(
//					Environment.DIRECTORY_PICTURES),
//					getString(R.string.userAvatarFileName));
			File photo=new File(Environment.getExternalStorageDirectory().getAbsolutePath(), getString(R.string.userAvatarFileName));
			if (photo.exists()) {
				photo.delete();
			}
			FileOutputStream fos = null;

			try {
				fos = new FileOutputStream(photo);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			} catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			} finally {

				if (fos != null)
					try {
						bitmap.recycle();
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
			if (s != null) {
				Toast.makeText(getActivity(), "Image Saved!",
						Toast.LENGTH_SHORT).show();
			} else {
			}
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
		openActiveSession(getActivity(), true, permissions,
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
				if (sendMessage) {
					fbListener.setMessage("Testing");
				} else {
					sendInvites();
				}
			} else {
				Log.d("Facebook", "Send Message Failed");
			}
		}
	}

	private void sendInvites() {

		Bundle params = new Bundle();
		params.putString("message",
				"I just smashed 100 friends! Can you beat it?");
		params.putString("title", "Invite Friend");

		WebDialog dialog = new WebDialog.Builder(getActivity(),
				Session.getActiveSession(), "apprequests", params)
				.setOnCompleteListener(new WebDialog.OnCompleteListener() {
					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error != null) {
							if (error instanceof FacebookOperationCanceledException) {
								Toast.makeText(getActivity(),
										getString(R.string.requestCancelled),
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getActivity(),
										getString(R.string.noInternetFound),
										Toast.LENGTH_SHORT).show();
							}
						} else {
							final String requestId = values
									.getString("request");
							if (requestId != null) {
								Toast.makeText(getActivity(),
										getString(R.string.requestSent),
										Toast.LENGTH_SHORT).show();
								Log.i("TAG", " onComplete req dia ");
							} else {
								Toast.makeText(getActivity(),
										getString(R.string.requestCancelled),
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				}).build();

		Window dialog_window = dialog.getWindow();
		dialog_window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.show();
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		uiHelper.onActivityResult(requestCode, resultCode, data,
				new FacebookDialog.Callback() {

					@Override
					public void onError(FacebookDialog.PendingCall pendingCall,
							Exception error, Bundle data) {
						Log.e("Activity",
								String.format("Error: %s", error.toString()));
					}

					@Override
					public void onComplete(
							FacebookDialog.PendingCall pendingCall, Bundle data) {
						Log.i("Activity", "Success!");
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

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

				URL a = new File(getActivity().getFilesDir(),
						getString(R.string.userAvatarFileName)).toURI().toURL();
				/*
				 * Bitmap image = BitmapFactory.decodeFile(a.toString()); Bundle
				 * parameters = new Bundle(); parameters.putParcelable("source",
				 * image); parameters.putString("message",
				 * "my message for the page"); Request request1 = new
				 * Request(Session.getActiveSession(), "me/photos", parameters,
				 * HttpMethod.POST, new Request.Callback() {
				 * 
				 * @Override public void onCompleted(Response response) { //
				 * showPublishResult(getActivity().getString(R.string.app_name),
				 * response.getGraphObject(), response.getError()); String
				 * postId = null; try { JSONObject graphResponse =
				 * response.getGraphObject() .getInnerJSONObject();
				 * 
				 * postId = graphResponse.getString("id"); } catch
				 * (JSONException e) { Log.i(TAG, "JSON error " +
				 * e.getMessage()); } FacebookRequestError error =
				 * response.getError(); if (error != null) { Toast.makeText(
				 * getActivity().getApplicationContext(),
				 * error.getErrorMessage(), Toast.LENGTH_SHORT) .show(); } else
				 * { Toast.makeText( getActivity().getApplicationContext(),
				 * postId, Toast.LENGTH_LONG).show(); }
				 * 
				 * } }); request1.executeAsync();
				 */

				// postParams.putString("picture", a.toString());
				// File imgPath = new File(a.toString());
				File x = new File(getActivity().getFilesDir(),
						getString(R.string.userAvatarFileName));
				// RandomAccessFile f = new RandomAccessFile(, "r");
				// byte[] bytes =
				// org.apache.commons.io.FileUtils.readFileToByteArray new
				// byte[(int) x.length()];
				byte[] data;
				RandomAccessFile f = new RandomAccessFile(x, "r");
				try {
					// Get and check length
					long longlength = f.length();
					int length = (int) longlength;
					if (length != longlength)
						throw new IOException("File size >= 2 GB");
					// Read file and return data
					data = new byte[length];
					f.readFully(data);
					// return data;
				} finally {
					f.close();
				}
				Bundle postParams = new Bundle();
				postParams.putString("name",
						getString(R.string.fb_share_title_t) + "\n\n"
								+ getString(R.string.fb_share_desc_t));
				postParams.putString("caption", "Snikers");
				postParams.putString("description",
						getString(R.string.fb_share_desc_t));
				postParams.putString("link", "https://resultrix.com");
				postParams.putByteArray("picture", data);

				// postParams.putString("picture", new File(getActivity()
				// .getFilesDir(), getString(R.string.userAvatarFileName))
				// .toString());

				Request.Callback callback = new Request.Callback() {
					public void onCompleted(Response response) {
						String postId = null;
						try {
							JSONObject graphResponse = response
									.getGraphObject().getInnerJSONObject();

							postId = graphResponse.getString("id");

							FacebookRequestError error = response.getError();
							if (error != null) {
								Toast.makeText(
										getActivity().getApplicationContext(),
										error.getErrorMessage(),
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(
										getActivity().getApplicationContext(),
										postId, Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Exception with Facebook api",
									Toast.LENGTH_LONG).show();
							Log.i(TAG, "JSON error " + e.getMessage());
						}
					}
				};

				Request request = new Request(session, "me/photos", postParams,
						HttpMethod.POST, callback);

				RequestAsyncTask task = new RequestAsyncTask(request);
				task.execute();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	
	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    params.putString("name", "Snickers");
	    params.putString("caption",
				getResources().getString(R.string.fb_share_title_t));
	    params.putString("description",
				getResources().getString(R.string.fb_share_desc_t));
	    params.putString("link", "http://snickers.com");
	    params
				.putString("picture",
						"https://snickerspromo.dessertmoments.com/images/icon.jpg");

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(getActivity(),
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(getActivity(),
	                            "Thank You!",
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(getActivity().getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(getActivity().getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(getActivity().getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}

    @Override
    public boolean getFacebookSessionState() {
        return Session.getActiveSession().getState().isOpened();
    }

	@Override
	public void setUser(GraphUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GraphUser getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handlePendingFbAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Session getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setImagePath(String imagePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMessage(String message) {
		// TODO Auto-generated method stub
		
	}
	
}
