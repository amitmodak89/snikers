package com.mars.snickers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.ksoap2.serialization.PropertyInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.sdk.ed;
import com.flurry.sdk.em;
import com.mars.snickers.adapter.Adapter_LocationSpinner;
import com.mars.snickers.helper.FontManager;
import com.mars.snickers.helper.LocaleManager;
import com.mars.snickers.helper.SoapController;
import com.mars.snickers.prefs.Prefs;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Form extends Fragment {

    public String TAG = "Fragment_Form";
    private Button submit;
    private CheckBox agree;
    private TextView header, note, dd, mm, yyyy, dob, preMob;
    private EditText name, surname, email, mobile, address;
    private Spinner cityList;
    private LinearLayout datePicker;
    private DatePickerDialog dpd;
    private ProgressDialog pd;
    //AMIT CODE TO HIDE KEYBOARD
    OnFocusChangeListener ofcListener = null;

    public Fragment_Form() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO REMOVE FRAGMENT WHEN LOGGED IN
//        if (Prefs.getUserCity(getActivity()) != null) {
//            getActivity().getSupportFragmentManager().popBackStack();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//      if(LocaleManager.getLanguage() != "");
    	// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form, container, false);
        submit = (Button) view.findViewById(R.id.ff_btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitClicked();
            }
        });
        FontManager.setFont(getActivity(), submit, null, FontManager.FontType.FRANKLIN);
        agree = (CheckBox) view.findViewById(R.id.ff_cb_agree);
        agree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    showTNCalert();
                }
            }
        });
        FontManager.setFont(getActivity(), agree, null, FontManager.FontType.FRANKLIN);
        header = (TextView) view.findViewById(R.id.ff_tv_headerNotice);
        FontManager.setFont(getActivity(), header, null, FontManager.FontType.FRANKLIN);
        note = (TextView) view.findViewById(R.id.ff_tv_note);
        FontManager.setFont(getActivity(), note, null, FontManager.FontType.FRANKLIN);
        dd = (TextView) view.findViewById(R.id.ff_tv_dd);
        FontManager.setFont(getActivity(), dd, null, FontManager.FontType.FRANKLIN);
        mm = (TextView) view.findViewById(R.id.ff_tv_mm);
        FontManager.setFont(getActivity(), mm, null, FontManager.FontType.FRANKLIN);
        yyyy = (TextView) view.findViewById(R.id.ff_tv_yyyy);
        FontManager.setFont(getActivity(), yyyy, null, FontManager.FontType.FRANKLIN);
        dob = (TextView) view.findViewById(R.id.ff_tv_dob);
        FontManager.setFont(getActivity(), dob, null, FontManager.FontType.FRANKLIN);
        name = (EditText) view.findViewById(R.id.ff_et_name);
        FontManager.setFont(getActivity(), name, null, FontManager.FontType.FRANKLIN);
        surname = (EditText) view.findViewById(R.id.ff_et_surname);
        FontManager.setFont(getActivity(), surname, null, FontManager.FontType.FRANKLIN);
        email = (EditText) view.findViewById(R.id.ff_et_email);
        FontManager.setFont(getActivity(), email, null, FontManager.FontType.FRANKLIN);
        preMob = (TextView) view.findViewById(R.id.ff_tv_preMobileNo);
        FontManager.setFont(getActivity(), preMob, null, FontManager.FontType.FRANKLIN);
        mobile = (EditText) view.findViewById(R.id.ff_et_mobileNo);
        FontManager.setFont(getActivity(), mobile, null, FontManager.FontType.FRANKLIN);
        address = (EditText) view.findViewById(R.id.ff_et_addr);
        FontManager.setFont(getActivity(), address, null, FontManager.FontType.FRANKLIN);

        RelativeLayout linearLayout_lostFocus = (RelativeLayout)view.findViewById(R.id.linearLayout_form);
        onTapOutsideBehaviour(linearLayout_lostFocus); 
        datePicker = (LinearLayout) view.findViewById(R.id.ff_ll_datepicker);
        String userDob = Prefs.getUserDob(getActivity());
        final int year, month, day;
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        dd.setText("" + day);
        mm.setText("" + (month + 1));
        yyyy.setText("" + (year));
       datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd = new DatePickerDialog(getActivity(), datePickerListener, year, month, day);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date d = null, maxd = null;
                try {
                    d = sdf.parse("1/1/1900");
                    maxd = sdf.parse(day + "/" + (month + 1) + "/" + (year ));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dpd.getDatePicker().setMinDate(d.getTime());
                dpd.getDatePicker().setMaxDate(maxd.getTime());
                dpd.show();
            }
        });
        cityList = (Spinner) view.findViewById(R.id.ff_sp_city); 
        String[] cityListArray = getResources().getStringArray(R.array.city_list);
        if(Prefs.getLanguage(getActivity()).equals("tr"))
        {
        	cityListArray[0]="sher";
        }
        if(Prefs.getLanguage(getActivity()).equals("ar"))
        {
        	cityListArray[0]="المدينة";
        }
        else
        {
        	cityListArray[0]="CITY";
        }
        cityList.setAdapter(new Adapter_LocationSpinner(getActivity(),cityListArray ));  
        cityList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view.findViewById(R.id.sll_tv_item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        pd = new ProgressDialog(getActivity());
        pd.setTitle("");
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.progressMessage));
        return view;
    }

    private void submitClicked() {
        if (validate())
            if (agree.isChecked()) {
                if (SoapController.isNetworkAvailable(getActivity())) {
                    new sendRequest(getActivity()).execute();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.agreeTC), Toast.LENGTH_SHORT).show();
            }
    }

    private boolean isNotEmpty(View v) {
        boolean result = true;
        TextView box = (TextView) v;
        result = box.getText().toString().trim().isEmpty();
        if (result)
            box.setError(getResources().getString(R.string.required));
        return !result;
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
    
    private boolean validate() {
        boolean result = false;
        if (isNotEmpty(name) && isNotEmpty(surname) && isNotEmpty(mobile) && isNotEmpty(address) && isNotEmpty(dd) && isNotEmpty(mm) && isNotEmpty(yyyy) && isNotEmpty(email)) {
            if (isValidEmail() && isValidPhoneNo()) {
                result = true;
            }
        }
        return result;
    }
    
    private void onTapOutsideBehaviour(View view) {
    	try{
        if(!(view instanceof EditText) || !(view instanceof Button)) {
            view.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }
    	}catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    }
//    \\Function to hide keyboard
    private static void hideSoftKeyboard(Activity activity) {
        try{
    	InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
        
    }
    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            int year = selectedYear;
            int month = selectedMonth;
            int day = selectedDay;
            // set selected date into textview
            dd.setText("" + day);
            mm.setText("" + (month + 1));
            yyyy.setText("" + year);
        }
    };


    private void showNoticeAlert() {
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
        data.setText(getResources().getString(R.string.remember));
        close.setImageDrawable(getResources().getDrawable(R.drawable.done));
        builder.setTitle("");
        final Dialog dialog = builder.create();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment_SelectAvatar fsa = new Fragment_SelectAvatar();
                fm.popBackStack(fsa.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction().replace(R.id.container, fsa, fsa.TAG).commit();
//                fm.beginTransaction().replace(R.id.container, fsa).addToBackStack(null).commit();
            }
        });
        dialog.show();
    }

    private void showTNCalert() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View inflatedView = inflater.inflate(R.layout.alert_detail, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inflatedView);
        builder.setCancelable(true);
        TextView header = (TextView) inflatedView.findViewById(R.id.alert_header);
        header.setVisibility(View.GONE);
        TextView data = (TextView) inflatedView.findViewById(R.id.alert_data);
        data.setGravity(Gravity.LEFT);
        ImageView close = (ImageView) inflatedView.findViewById(R.id.alert_iv_close);
        FontManager.setFont(getActivity(), data, null, FontManager.FontType.FRANKLIN);
        data.setText(getResources().getString(R.string.termsAndCondition_form).toUpperCase());
        close.setImageDrawable(getResources().getDrawable(R.drawable.done));
        builder.setTitle("");
        final Dialog dialog = builder.create();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                Fragment_SelectAvatar fsa = new Fragment_SelectAvatar();
////                fm.popBackStack(fsa.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
////                fm.beginTransaction().replace(R.id.container, fsa, fsa.TAG).addToBackStack(null).commit();
//                fm.beginTransaction().replace(R.id.container, fsa).addToBackStack(null).commit();
            }
        });
        dialog.show();
    }

    public class sendRequest extends AsyncTask<Void, Void, String> {
        Context context;

        sendRequest(Context context) {
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
            PropertyInfo PI_city = SoapController.generatePropertyInfo("sCode", cityList.getSelectedItem().toString().trim(), String.class);

            ArrayList<PropertyInfo> piList = new ArrayList<PropertyInfo>();
            piList.add(PI_email);
            piList.add(PI_name);
            piList.add(PI_mobile);
            piList.add(PI_location);
            piList.add(PI_language);
            piList.add(PI_city);

            String response = SoapController.makeRequest(SoapController.SoapConstant.METHOD_RegisterNormalUser, piList);
            Log.d("Hit", "Response: " + response);
            if (response != null) {
                Prefs.setUserId(context, response);
                Prefs.setUserName(getActivity(), name.getText().toString().trim());
                Prefs.setUserSurname(getActivity(), surname.getText().toString().trim());
                Prefs.setUserEmail(getActivity(), email.getText().toString().trim());
                Prefs.setUserMobileNo(getActivity(), mobile.getText().toString().trim());
                Prefs.setUserCity(getActivity(), cityList.getSelectedItem().toString());
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            Log.e(TAG, s);
            if (s != null) {
                showNoticeAlert();
            } else {
                Toast.makeText(getActivity(), getString(R.string.serverError), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
