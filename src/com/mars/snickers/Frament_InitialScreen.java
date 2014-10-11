package com.mars.snickers;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mars.snickers.helper.FontManager;
import com.mars.snickers.helper.LocaleManager;
import com.mars.snickers.prefs.Prefs;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frament_InitialScreen extends Fragment {

    public final String TAG = "InitialScreenFragment";

    private Button revealBtn;
    private TextView play;
    private ImageView mirror;


    public Frament_InitialScreen() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        String lang = Prefs.getLanguage(activity);
        String country = Prefs.getLanguage(activity);
        if(lang != null)
        LocaleManager.setLanguage(activity, lang, country );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_initial_screen, container, false);
        revealBtn = (Button) view.findViewById(R.id.fis_btn_reveal);
        play = (TextView) view.findViewById(R.id.fis_tv_playgame);
        mirror = (ImageView) view.findViewById(R.id.fis_iv_mirror);
        FontManager.setFont(getActivity(), play, null, FontManager.FontType.FRANKLIN);
        FontManager.setFont(getActivity(), revealBtn, null, FontManager.FontType.FRANKLIN);
        revealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revealMe();
            }
        });
        startMirrorAnimation();
        return view;
    }

    private void startMirrorAnimation() {
        AnimationDrawable frameAnimation = (AnimationDrawable) mirror.getBackground();
        frameAnimation.start();
    }

    private void revealMe() {
        Fragment_SelectLocation fsl = new Fragment_SelectLocation();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fsl, fsl.TAG).addToBackStack(fsl.TAG).commit();
    }


}
