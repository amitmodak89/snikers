package com.mars.snickers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mars.snickers.R;
import com.mars.snickers.helper.FontManager;

public class Adapter_LocationSpinner extends ArrayAdapter<String> {

        Context context;
        String[] list;
        private int defaultPosition;

        public int getDefaultPosition() {
            return defaultPosition;
        }

        public Adapter_LocationSpinner(Context context, String[] objects) {
            super(context, 0, objects);
            this.context = context;
            list = objects;
        }

        public void setDefaultPostion(int position) {
            this.defaultPosition = position;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustom(position, convertView, parent);
        }

        public View getCustom(int position, View convertView, ViewGroup parent) {

            View row = LayoutInflater.from(context).inflate(
                    R.layout.spinner_layout_locations, parent, false);
            TextView label = (TextView) row.findViewById(R.id.sll_tv_item);
            FontManager.setFont(context, label, context.getResources().getDimension(R.dimen.gapx0_5), FontManager.FontType.FRANKLIN);

            label.setText(list[position]);

            return row;
        }

        public View getCustomView(int position, View convertView,
                ViewGroup parent) {

            View row = LayoutInflater.from(context).inflate(
                    R.layout.item_spinner_locations, parent, false);
            TextView label = (TextView) row.findViewById(R.id.isl_tv_item);
            FontManager.setFont(context, label, context.getResources().getDimension(R.dimen.gapx0_5), FontManager.FontType.FRANKLIN);
            label.setText(list[position]);

            return row;
        }
    }