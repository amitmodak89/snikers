package com.mars.snickers;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.ksoap2.serialization.PropertyInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.mars.snickers.helper.FontManager;
import com.mars.snickers.helper.SoapController;
import com.mars.snickers.listners.IfacebookListener;
import com.mars.snickers.prefs.Prefs;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Register extends Fragment {

    public final String TAG = "Fragment_Register";

    TextView header, note, orOption, fbConnectionText, tnc;
    EditText name, surname, email, code, mobile;
    Button submit;
    LoginButton fbConnect;
    private IfacebookListener fbListener;
    private ProgressDialog pd;
    OnFocusChangeListener ofcListener = null;
    
    LinearLayout linearLayout_lostFocus;
    
    public Fragment_Register() {
        // Required empty public constructor
    }

    ;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fbListener = (Activity_Facebook) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IfacebookListener");
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO REMOVE FRAGMENT WHEN LOGGED IN
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        header = (TextView) view.findViewById(R.id.fr_tv_header);
        note = (TextView) view.findViewById(R.id.fr_tv_header);
        orOption = (TextView) view.findViewById(R.id.fr_tv_header);
        fbConnectionText = (TextView) view.findViewById(R.id.fr_tv_header);
        tnc = (TextView) view.findViewById(R.id.fr_tv_tnc);
        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTncAlert();
            }
        });
        name = (EditText) view.findViewById(R.id.fr_et_name);
        surname = (EditText) view.findViewById(R.id.fr_et_surname);
        email = (EditText) view.findViewById(R.id.fr_et_email);
        code = (EditText) view.findViewById(R.id.fr_et_code);
        mobile = (EditText) view.findViewById(R.id.fr_et_mobileNo);

        
        linearLayout_lostFocus = (LinearLayout)view.findViewById(R.id.LinearLayout);
        onTapOutsideBehaviour(linearLayout_lostFocus);

        
        submit = (Button) view.findViewById(R.id.fr_btn_submit);
        fbConnect = (LoginButton) view.findViewById(R.id.fr_btn_facebookConnect);

     
        
        Log.d("Facebook", "" + Session.getActiveSession());
        fbConnect.setReadPermissions(Arrays.asList("email", "user_location", "user_friends", "public_profile"));
//        fbConnect.setReadPermissions(Arrays.asList("basic_info", "email", "user_location"));
        fbConnect.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                fbListener.setUser(user);
                if (user != null) {
                    new sendFacebookUserDetails(getActivity(), user).execute();
                }
            }


        });


        FontManager.setFont(getActivity(), header, null, FontManager.FontType.HERMESTR_BOLD);
        FontManager.setFont(getActivity(), note, null, FontManager.FontType.FRANKLIN);
        FontManager.setFont(getActivity(), fbConnectionText, null, FontManager.FontType.HERMESTR_BOLD);
        FontManager.setFont(getActivity(), orOption, null, FontManager.FontType.HERMESTR_BOLD);

        FontManager.setFont(getActivity(), fbConnect, null, FontManager.FontType.FRANKLIN);
        FontManager.setFont(getActivity(), submit, null, FontManager.FontType.FRANKLIN);


        FontManager.setFont(getActivity(), name, null, FontManager.FontType.FRANKLIN);
        FontManager.setFont(getActivity(), surname, null, FontManager.FontType.FRANKLIN);
        FontManager.setFont(getActivity(), email, null, FontManager.FontType.FRANKLIN);
        FontManager.setFont(getActivity(), code, null, FontManager.FontType.FRANKLIN);
        FontManager.setFont(getActivity(), mobile, null, FontManager.FontType.FRANKLIN);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitClicked();
//                fbListener.setImagePath(new File(getActivity().getFilesDir(), "photo_snicker.jpg").getAbsolutePath());
//                fbListener.setMessage("Testing code!");
            }
        });

        pd = new ProgressDialog(getActivity());
        pd.setTitle("");
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.progressMessage));

        Log.d("Facebook", "Is session state open? ans=" + fbListener.getFacebookSessionState());

        return view;
    }
    private void onTapOutsideBehaviour(View view) {
        if(!(view instanceof EditText) || !(view instanceof Button)) {
            view.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }

            });
        }
    }
//    \\Function to hide keyboard
    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private class MyFocusChangeListener implements OnFocusChangeListener {
    	int id;
    	    	public MyFocusChangeListener(int e) {
    				// TODO Auto-generated constructor stub
    	    		id = e;
    			}
    	        public void onFocusChange(View v, boolean hasFocus){

    	            if(v.getId() == id && !hasFocus) {

 	            	   InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
      	                imm.showSoftInputFromInputMethod(v.getWindowToken(), 0);
    	           
    	            }
    	            else
    	            {
    	                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    	            }
    	        }
    }
    public void onSubmitClicked() {
        if (checkInputValidity()) {
            if (SoapController.isNetworkAvailable(getActivity())) {
                new sendRegisterRequest(getActivity()).execute();
            }
        }
    }

    private boolean checkInputValidity() {
        boolean result = false;

        if (isNotEmpty(code) && isNotEmpty(email) && isNotEmpty(name) && isNotEmpty(surname) && isNotEmpty(mobile)) {
            if (isValidEmail() && isValidPhoneNo()) {
                result = true;
            }
        }

        return result;
    }


    public boolean isValidPhoneNo() {
        boolean result = android.util.Patterns.PHONE.matcher(mobile.getText()).matches();
        if (!result) {
            mobile.setError(getString(R.string.inCorrectMobileNo));
        }
        return result;
    }

    public boolean isValidEmail() {
        boolean result = android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches();
        if (!result) {
            email.setError(getString(R.string.inCorrectEmail));
        }
        return result;
    }

    private boolean isNotEmpty(View v) {
        boolean result = true;
        TextView box = (TextView) v;
        result = box.getText().toString().trim().isEmpty();
        if (result)
            box.setError(getResources().getString(R.string.required));
        return !result;
    }

    public class sendRegisterRequest extends AsyncTask<Void, Void, String> {
        Context context;

        sendRegisterRequest(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            PropertyInfo PI_email = SoapController.generatePropertyInfo("sUserEmail", email.getText().toString().trim(), String.class);
            PropertyInfo PI_name = SoapController.generatePropertyInfo("sUsername", name.getText().toString().trim() + " " + surname.getText().toString().trim(), String.class);
            PropertyInfo PI_mobile = SoapController.generatePropertyInfo("sMobileNumber", mobile.getText().toString().trim(), String.class);
            PropertyInfo PI_location = SoapController.generatePropertyInfo("sLocation", getResources().getStringArray(R.array.locations)[Prefs.getLocationIndex(getActivity())], String.class);
            PropertyInfo PI_language = SoapController.generatePropertyInfo("sLanguage", getResources().getStringArray(R.array.locations_country)[Prefs.getLocationIndex(getActivity())], String.class);
            PropertyInfo PI_code = SoapController.generatePropertyInfo("sCode", code.getText().toString().trim(), String.class);

            ArrayList<PropertyInfo> piList = new ArrayList<PropertyInfo>();
            piList.add(PI_email);
            piList.add(PI_name);
            piList.add(PI_mobile);
            piList.add(PI_location);
            piList.add(PI_language);
            piList.add(PI_code);

            String response = SoapController.makeRequest(SoapController.SoapConstant.METHOD_RegisterNormalUser, piList);
            Log.d("Hit", "Response: " + response);
            if (response != null) {
                Prefs.setUserId(context, response);
                Prefs.setUserEmail(context, email.getText().toString().trim());
                Prefs.setUserName(context, name.getText().toString().trim());
                Prefs.setUserSurname(context, surname.getText().toString().trim());
                Prefs.setUserMobileNo(context, mobile.getText().toString().trim());
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (s != null) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment_GetPicture fgp = new Fragment_GetPicture();
                fm.popBackStack(fgp.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction().replace(R.id.container, fgp, fgp.TAG).commit();
//                fm.beginTransaction().replace(R.id.container, fgp).addToBackStack(null).commit();
            } else {
                Toast.makeText(getActivity(), getString(R.string.serverError), Toast.LENGTH_SHORT).show();
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
            if (response != null) {
                Prefs.setUserId(context, response);
                Prefs.setUserEmail(getActivity(), user.getProperty("email").toString());
                Prefs.setUserName(getActivity(), user.getFirstName());
                Prefs.setUserSurname(getActivity(), user.getLastName());
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (s != null) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment_GetPicture fgp = new Fragment_GetPicture();
                fm.popBackStack(fgp.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction().replace(R.id.container, fgp, fgp.TAG).commit();
//                fm.beginTransaction().replace(R.id.container, fgp).addToBackStack(null).commit();
            } else {
                Toast.makeText(getActivity(), getString(R.string.serverError), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showTncAlert() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View inflatedView = inflater.inflate(R.layout.alert_detail, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inflatedView);
        builder.setCancelable(true);
        TextView header = (TextView) inflatedView.findViewById(R.id.alert_header);
        header.setVisibility(View.GONE);
        TextView data = (TextView) inflatedView.findViewById(R.id.alert_data);
        ImageView close = (ImageView) inflatedView.findViewById(R.id.alert_iv_close);
        FontManager.setFont(getActivity(), data, null, FontManager.FontType.FRANKLIN);
        if (Prefs.getLocationIndex(getActivity()) == 1) {
            data.setText(Html.fromHtml(getResources().getString(R.string.tnc_jordan)).toString());
        } else {
            data.setText(Html.fromHtml(getResources().getString(R.string.termsAndCondition_registerData)).toString());
        }
        close.setImageDrawable(getResources().getDrawable(R.drawable.done));
        builder.setTitle("");
        final Dialog dialog = builder.create();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
