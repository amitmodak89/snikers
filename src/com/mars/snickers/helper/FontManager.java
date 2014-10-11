package com.mars.snickers.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.mars.snickers.R;
import com.mars.snickers.prefs.Prefs;

/**
 * Created by malpani on 9/7/14.
 */
public class FontManager {

    public static enum FontType {
        FRANKLIN("Franklin Gothic Extra Condensed.ttf"), ARABIC("SnickersArabic_1.ttf"), HERMES_BLACK("Hermes-Black-1.ttf"), HERMESTR("HermesTR.ttf"), HERMESTR_BLACK("HermesTR-Black.ttf"), HERMESTR_BOLD("HermesTR-Bold.ttf");
        private String fontName;

        FontType(String s) {
            fontName = s;
        }

        public String getFontName() {
            return fontName;
        }
    }

    public static void setFont(Context context, View v, Float size, FontType font) {
        TextView tv = (TextView) v;

        if (!Prefs.getLanguage(context).equals("ar")) {
            if (size != null)
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
            tv.setTypeface(Typeface.createFromAsset(context.getAssets(), font.getFontName()));
        } else {
            if (size != null)
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
            tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "SnickersArabic_1.ttf"));
        }
    }
}
