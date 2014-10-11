package com.mars.snickers;


import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mars.snickers.adapter.Adapter_LocationSpinner;
import com.mars.snickers.helper.FontManager;
import com.mars.snickers.helper.LocaleManager;
import com.mars.snickers.listners.IfacebookListener;
import com.mars.snickers.listners.IleftMenuListner;
import com.mars.snickers.prefs.Prefs;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_SelectLocation extends Fragment {

    public final String TAG = "SelectLocationFragment";
    private Spinner locationSpinner;
    private Button enter;
    private ImageView flag;
    private TextView selectLocation;
    private int mPosition = 0;
    private String POSITION = "position";
    private IfacebookListener fbListener;
    private IleftMenuListner menuListner;


    public Fragment_SelectLocation() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fbListener = (Activity_Facebook) activity;
            menuListner = (Activity_Super) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IfacebookListener");
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_location, container, false);
        flag = (ImageView) view.findViewById(R.id.fsl_iv_flag);
        selectLocation = (TextView) view.findViewById(R.id.fsl_tv_selectLocation);
        FontManager.setFont(getActivity(), selectLocation, null, FontManager.FontType.FRANKLIN);

        enter = (Button) view.findViewById(R.id.fsl_btn_enter);
        FontManager.setFont(getActivity(), enter, null, FontManager.FontType.FRANKLIN);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterClicked();
            }
        });
        locationSpinner = (Spinner) view.findViewById(R.id.fsl_spnr_location);


        locationSpinner.setAdapter(new Adapter_LocationSpinner(getActivity(), getResources().getStringArray(R.array.locations)));
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mPosition = position;
                setImage(position);
                Prefs.setLocationIndex(getActivity(), mPosition);
//                LocaleManager.setLanguage(getActivity(), "en", "US");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mPosition = Prefs.getLocationIndex(getActivity());
        locationSpinner.setSelection(mPosition);
        setImage(mPosition);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(POSITION);
            locationSpinner.setSelection(mPosition);
            setImage(mPosition);
        } else {
            Log.d(TAG, "savedInstance = null");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, mPosition);
        Log.d(TAG, "savedInstance called");
    }


    private void setImage(int position) {
        TypedArray imgs = getResources().obtainTypedArray(R.array.locations_images);
        flag.setImageResource(imgs.getResourceId(position, -1));
        imgs.recycle();
    }

    private void enterClicked() {
        int position = Prefs.getLocationIndex(getActivity());

        int pos = locationSpinner.getSelectedItemPosition();
//        LocaleManager.setLanguage(getActivity(), getActivity().getResources().getStringArray(R.array.locations_language)[pos], getActivity().getResources().getStringArray(R.array.locations_country)[pos]);
        menuListner.updateMenu();
        if (getResources().getStringArray(R.array.locations)[position].equals(getResources().getString(R.string.TURKEY))) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Fragment_CampaignGameOption fcgo = new Fragment_CampaignGameOption();
            fm.popBackStack(fcgo.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.container, fcgo, fcgo.TAG).addToBackStack(fcgo.TAG).commit();
//            fm.beginTransaction().replace(R.id.container, fcgo,fcgo.getTag()).addToBackStack(TAG).commitAllowingStateLoss();
        } else {

            if (Prefs.getUserId(getActivity())== null) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment_Register fr = new Fragment_Register();
                fm.popBackStack(fr.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction().replace(R.id.container, fr, fr.TAG).addToBackStack(fr.TAG).commit();

//                fm.beginTransaction().replace(R.id.container, fr,fr.getTag()).addToBackStack(TAG).commitAllowingStateLoss();
            } else {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment_GetPicture fgp = new Fragment_GetPicture();
                fm.popBackStack(fgp.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction().replace(R.id.container, fgp, fgp.TAG).addToBackStack(fgp.TAG).commit();
//                fm.beginTransaction().replace(R.id.container, fgp).addToBackStack(TAG).commit();
            }
        }
    }


}
