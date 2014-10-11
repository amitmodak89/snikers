package com.mars.snickers.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mars.snickers.R;
import com.mars.snickers.helper.LocaleManager;
import com.mars.snickers.prefs.Prefs;

/**
 * Created by malpani on 9/4/14.
 */
public class Adapter_LeftMenu extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    TypedArray imgsArray, imgsBackgroundArray;
    String[] languages;

    public Adapter_LeftMenu(Context context, TypedArray imgsArray, TypedArray imgsBackgroundArray) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imgsArray = imgsArray;
        this.imgsBackgroundArray = imgsBackgroundArray;
        languages = context.getResources().getStringArray(R.array.locations_language);
    }

    @Override
    public int getCount() {
        return imgsArray.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_listview_leftmenu, null);
            holder = new ViewHolder();
            holder.item_iv = (ImageView) convertView.findViewById(R.id.illm_iv_menuitem);
            holder.item_tv = (TextView) convertView.findViewById(R.id.illm_tv_menuitem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

boolean x_for_music = false;
        switch (position) {
            case 0:
                //info
                break;
            case 1:
                //EN
                if (!Prefs.getLanguage(context).equals("en")) {
                    holder.item_tv.setTextColor(context.getResources().getColor(R.color.THEME_YELLOW));
                } else {
                    holder.item_tv.setTextColor(Color.WHITE);
                }
                break;
            case 2:
                //AR
                if (!Prefs.getLanguage(context).equals("ar")) {
                    holder.item_tv.setTextColor(context.getResources().getColor(R.color.THEME_YELLOW));
                } else {
                    holder.item_tv.setTextColor(Color.WHITE);
                }

                break;
            case 3:
                //TR
                if (!Prefs.getLanguage(context).equals("tr")) {
                    holder.item_tv.setTextColor(context.getResources().getColor(R.color.THEME_YELLOW));
                } else {
                    holder.item_tv.setTextColor(Color.WHITE);
                }

                break;
            case 4:
                //fb
                break;
            case 5:
                //tw
                break;
            case 6:
                //music
                if (Prefs.getDidUserSetToPlayMusic(context)) {
                	holder.item_tv.setText("iIIi");
                    holder.item_tv.setTextColor(context.getResources().getColor(R.color.THEME_YELLOW));
                    holder.item_iv.setVisibility(View.GONE);
                    x_for_music = false;
                } else {
                	holder.item_iv.setVisibility(View.VISIBLE);
                	holder.item_tv.setTextColor(Color.WHITE);
                    holder.item_tv.setText("");
                    holder.item_iv.setBackgroundResource(R.drawable.music_mute);
                    x_for_music = true;
//                	holder.item_tv.setBackgroundResource(R.drawable.music_mute);
                }
                break;
            case 7:
                //copyrights
                break;
        }
        if ("drawable".equals(imgsArray.getResources().getResourceTypeName(imgsArray.getResourceId(position, -1)))) {
            holder.item_iv.setImageResource(imgsArray.getResourceId(position, -1));
            holder.item_iv.setBackgroundColor(imgsBackgroundArray.getColor(position, -1));
        } else {
        	if(!x_for_music)
        	{
            holder.item_tv.setText(imgsArray.getString(position));
            holder.item_tv.setBackgroundColor(imgsBackgroundArray.getColor(position, -1));
        }
}
        return convertView;
    }

    private static class ViewHolder {
        ImageView item_iv;
        TextView item_tv;
    }

}
