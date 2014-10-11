package com.mars.snickers;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mars.snickers.helper.FontManager;
import com.mars.snickers.listners.IfacebookListener;
import com.mars.snickers.prefs.Prefs;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_CampaignGameOption extends Fragment {

    public final String TAG = "Fragment_CampaignGameOption";

    private Button btn_howToParticipate, btn_enter, btn_play;
    private TextView pickOptionHeader, campaignParticipationHeader, orOption, playGameHeader;
    private IfacebookListener fbListener;

    public Fragment_CampaignGameOption() {
        // Required empty public constructor
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_campaign_game_option, container, false);
        btn_howToParticipate = (Button) view.findViewById(R.id.fcgo_btn_howToParticipate);
        btn_howToParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                howToParticipate();
            }
        });
        FontManager.setFont(getActivity(), btn_howToParticipate, null, FontManager.FontType.FRANKLIN);

        btn_enter = (Button) view.findViewById(R.id.fcgo_btn_enter);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterCampaign();
            }
        });
        FontManager.setFont(getActivity(), btn_enter, null, FontManager.FontType.FRANKLIN);

        btn_play = (Button) view.findViewById(R.id.fcgo_btn_play);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playGame();
            }
        });
        FontManager.setFont(getActivity(), btn_play, null, FontManager.FontType.FRANKLIN);

        pickOptionHeader = (TextView) view.findViewById(R.id.fcgo_tv_pickOptionHeader);
        FontManager.setFont(getActivity(), pickOptionHeader, null, FontManager.FontType.FRANKLIN);

        campaignParticipationHeader = (TextView) view.findViewById(R.id.fcgo_tv_campaignParticipationHeader);
        FontManager.setFont(getActivity(), campaignParticipationHeader, null, FontManager.FontType.HERMESTR_BOLD);

        orOption = (TextView) view.findViewById(R.id.fcgo_tv_or);
        FontManager.setFont(getActivity(), orOption, null, FontManager.FontType.HERMESTR_BOLD);

        playGameHeader = (TextView) view.findViewById(R.id.fcgo_tv_playGameHeader);
        FontManager.setFont(getActivity(), playGameHeader, null, FontManager.FontType.HERMESTR_BOLD);
        return view;
    }

    private void playGame() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (Prefs.getUserId(getActivity())== null) {
            Fragment_Register fr = new Fragment_Register();
            fm.popBackStack(fr.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.container, fr, fr.TAG).addToBackStack(fr.TAG).commit();
//            fm.beginTransaction().replace(R.id.container, fr).addToBackStack(null).commit();
        } else {
            Fragment_GetPicture fgp = new Fragment_GetPicture();
            fm.popBackStack(fgp.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.container, fgp, fgp.TAG).addToBackStack(fgp.TAG).commit();
//            fm.beginTransaction().replace(R.id.container, fgp).addToBackStack(null).commit();
        }
    }

    private void enterCampaign() {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (Prefs.getUserCity(getActivity())==null) {
            Fragment_Form ff = new Fragment_Form();
            fm.popBackStack(ff.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.container, ff, ff.TAG).addToBackStack(ff.TAG).commit();
//            fm.beginTransaction().replace(R.id.container, ff).commit();
        } else {
            Fragment_SelectAvatar fsa = new Fragment_SelectAvatar();
            fm.popBackStack(fsa.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.container, fsa, fsa.TAG).addToBackStack(fsa.TAG).commit();
//            fm.beginTransaction().replace(R.id.container, fsa).addToBackStack(null).commit();
        }

    }

    private void howToParticipate() {
//        getActivity();
    	  FragmentManager fm = getActivity().getSupportFragmentManager();
    	   particapate fgp = new particapate();
           fm.popBackStack(fgp.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
           fm.beginTransaction().replace(R.id.container, fgp, fgp.TAG).addToBackStack(fgp.TAG).commit();
           
		/*LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflatedView = inflater.inflate(R.layout.alert_detail, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inflatedView);
        builder.setCancelable(true);
        TextView header = (TextView) inflatedView.findViewById(R.id.alert_header);
        TextView data = (TextView) inflatedView.findViewById(R.id.alert_data);
        ImageView close = (ImageView) inflatedView.findViewById(R.id.alert_iv_close);
        header.setText(getResources().getString(R.string.howToParticipate_amit));

        FontManager.setFont(getActivity(), header, null, FontManager.FontType.FRANKLIN);

        data.setText(getResources().getString(R.string.howToParticipatedataAMIT));

        FontManager.setFont(getActivity(), data, null, FontManager.FontType.FRANKLIN);
        close.setImageDrawable(getResources().getDrawable(R.drawable.cancel_btn));
        builder.setTitle("");
        final Dialog dialog = builder.create();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
*/    }


}
