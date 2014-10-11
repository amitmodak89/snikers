package com.mars.snickers.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.mars.snickers.prefs.Prefs;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Created by malpani on 9/8/14.
 */
public class LocaleManager {

    public enum Language {
        ENGLISH("en","US"),
        TURKEY("tr", "TR"),
        QATAR("ar", "QA"),
        JORDAN("ar", "JO"),
        UAE("ar", "AE"),
        KUWAIT("ar", "KW"),
        BAHRAIN("ar", "BH"),
        OMAN("ar", "OM");

        String language, country;

        Language(String language, String country) {
            this.language = language;
            this.country = country;
        }

        public String getLanguage() {
            return language;
        }

        public String getCountry() {
            return country;
        }

    }

    public static void setLanguage(Context con, String language, String country) {



        if(Integer.valueOf(Build.VERSION.SDK_INT) > 16 )
        {
            Log.d("Yogesh", "Locale set to "+language);
            Locale myLocale = new Locale(language);
            Locale.setDefault(myLocale);
            android.content.res.Configuration config = new android.content.res.Configuration();
            config.locale = myLocale;
            con.getResources().updateConfiguration(config, con.getResources().getDisplayMetrics());
            Prefs.setLanguage(con, language);
            Prefs.setCountry(con, country);
        }else {


            try {
                /** here add language code */
                Locale locale = new Locale(language, country);

                Class amnClass = Class.forName("android.app.ActivityManagerNative");
                Object amn = null;
                Configuration config = null;

                // amn = ActivityManagerNative.getDefault();
                Method methodGetDefault = amnClass.getMethod("getDefault");
                methodGetDefault.setAccessible(true);
                amn = methodGetDefault.invoke(amnClass);

                // config = amn.getConfiguration();
                Method methodGetConfiguration = amnClass
                        .getMethod("getConfiguration");
                methodGetConfiguration.setAccessible(true);
                config = (Configuration) methodGetConfiguration.invoke(amn);

                // config.userSetLocale = true;
                Class configClass = config.getClass();
                Field f = configClass.getField("userSetLocale");
                f.setBoolean(config, true);

                // set the locale to the new value
                config.locale = locale;

                // amn.updateConfiguration(config);
                Method methodUpdateConfiguration = amnClass.getMethod(
                        "updateConfiguration", Configuration.class);
                methodUpdateConfiguration.setAccessible(true);
                methodUpdateConfiguration.invoke(amn, config);

                Prefs.setLanguage(con, language);
                Prefs.setCountry(con, country);

            } catch (NoSuchMethodException e) {
                Log.e("Yogesh", "NoSuchMethodException: ");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                Log.e("Yogesh", "IllegalAccessException: ");
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                Log.e("Yogesh", "NoSuchFieldException: ");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Log.e("Yogesh", "InvocationTargetException ");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                Log.e("Yogesh", "ClassNotFoundException: ");
                e.printStackTrace();
            }
        }
    }
}
