package com.mars.snickers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mars.snickers.helper.FontManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_RevealHungryDetail extends Fragment {

    public final String TAG = "Fragment_RevealHungryDetail";

    private ImageView proceed;
    TextView divaName, divaDescription, grandmaName, grandmaDescription, cavemanName, cavemanDescription, whoAreYou, useTheApp;


    public Fragment_RevealHungryDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reveal_hungry_detail, container, false);
        divaName = (TextView) view.findViewById(R.id.frhd_tv_divaName);
        FontManager.setFont(getActivity(), divaName, null, FontManager.FontType.HERMESTR);
        divaDescription = (TextView) view.findViewById(R.id.frhd_tv_divaDescription);
        FontManager.setFont(getActivity(), divaDescription, null, FontManager.FontType.FRANKLIN);
        grandmaName = (TextView) view.findViewById(R.id.frhd_tv_grandmaName);
        FontManager.setFont(getActivity(), grandmaName, null, FontManager.FontType.HERMESTR);
        grandmaDescription = (TextView) view.findViewById(R.id.frhd_tv_grandmaDescription);
        FontManager.setFont(getActivity(), grandmaDescription, null, FontManager.FontType.FRANKLIN);
        cavemanName = (TextView) view.findViewById(R.id.frhd_tv_cavemanName);
        FontManager.setFont(getActivity(), cavemanName, null, FontManager.FontType.HERMESTR);
        cavemanDescription = (TextView) view.findViewById(R.id.frhd_tv_cavemanDescription);
        FontManager.setFont(getActivity(), cavemanDescription, null, FontManager.FontType.FRANKLIN);
        whoAreYou = (TextView) view.findViewById(R.id.frhd_tv_whoAreYou);
        FontManager.setFont(getActivity(), whoAreYou, null, FontManager.FontType.FRANKLIN);
        useTheApp = (TextView) view.findViewById(R.id.frhd_tv_useTheApp);
        FontManager.setFont(getActivity(), useTheApp,null, FontManager.FontType.FRANKLIN);
        proceed = (ImageView) view.findViewById(R.id.btn_proceed_amit);
//        FontManager.setFont(getActivity(), proceed, null, FontManager.FontType.FRANKLIN);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        return view;
    }

    private void next() {
        getActivity().onBackPressed();
    }


}
