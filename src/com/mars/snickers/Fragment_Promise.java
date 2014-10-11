package com.mars.snickers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Promise extends Fragment {

    public final String TAG = "Fragment_Promise";
    public ImageView next;

    public Fragment_Promise() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promise, container, false);
        next = (ImageView) view.findViewById(R.id.btn_proceed_amit);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextClicked();
            }
        });
        return view;
    }

    private void nextClicked() {
        getActivity().onBackPressed();
    }


}
