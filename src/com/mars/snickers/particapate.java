package com.mars.snickers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mars.snickers.helper.FontManager;

public class particapate extends Fragment {
    public final String TAG = "Fragment_Particapate";
    ImageView close ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	// TODO Auto-generated method stub
    	getActivity().getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//    	try{
//    	getActivity().getActionBar().hide();
//    	}	catch (Exception e) {
//		e.printStackTrace();
//		}
    	View inflatedView = inflater
                .inflate(R.layout.alert, container, false);
    	
    	TextView header = (TextView) inflatedView.findViewById(R.id.alert_header);
          TextView data = (TextView) inflatedView.findViewById(R.id.alert_data);
        close = (ImageView) inflatedView.findViewById(R.id.alert_iv_close);
          header.setText(getResources().getString(R.string.howToParticipate_amit));

          FontManager.setFont(getActivity(), header, null, FontManager.FontType.FRANKLIN);

          data.setText(getResources().getString(R.string.howToParticipatedataAMIT));

          FontManager.setFont(getActivity(), data, null, FontManager.FontType.FRANKLIN);
//          close.setImageDrawable(getResources().getDrawable(R.drawable.cancel_btn));
          
          close.setOnClickListener(new View.OnClickListener() {
        	  @Override
        	  public void onClick(View view) {
        		 getActivity().onBackPressed();
        	  }
          });
    	return inflatedView;
    }
}
