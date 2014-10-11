package com.mars.snickers.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by malpani on 9/3/14.
 */
public class Prefs {
    private static final String PREF = "android";

    private Prefs() {
    }

    private static synchronized SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF, context.MODE_PRIVATE);
    }

    public static int getLocationIndex(Context context) {
        return getSharedPreferences(context).getInt("locationIndex", 0);
    }

    public static void setLocationIndex(Context context, int locationIndex) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt("locationIndex", locationIndex);
        editor.apply();
    }

    public static String getLanguage(Context context) {
        return getSharedPreferences(context).getString("language", "en");
    }

    public static void setLanguage(Context context, String language) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("language", language);
        editor.apply();
    }

    public static String getTwitterAccessToken(Context context) {
        return getSharedPreferences(context).getString("accessToken", null);
    }

    public static void setTwitterAccessToken(Context context, String accessToken) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("accessToken", accessToken);
        editor.apply();
    }

    public static String getTwitterAccessTokenSecret(Context context) {
        return getSharedPreferences(context).getString("accessTokenSecret", null);
    }

    public static void setTwitterAccessTokenSecret(Context context, String accessToken) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("accessTokenSecret", accessToken);
        editor.apply();
    }

    public static Boolean getMediaPlayerPlayingStatus(Context context) {
        return getSharedPreferences(context).getBoolean("media_playing", false);
    }

    public static void setMediaPlayerPlayingStatus(Context context, boolean enable) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("media_playing", enable);
        editor.apply();
    }

    public static Boolean getDidUserSetToPlayMusic(Context context) {
        return getSharedPreferences(context).getBoolean("user_play_music", true);
    }

    public static void setDidUserSetToPlayMusic(Context context, boolean enable) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("user_play_music", enable);
        editor.apply();
    }

    public static String getUserName(Context context) {
        return getSharedPreferences(context).getString("name", null);
    }

    public static void setUserName(Context context, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("name", name);
        editor.apply();
    }

    public static String getUserSurname(Context context) {
        return getSharedPreferences(context).getString("surname", null);
    }

    public static void setUserSurname(Context context, String surname) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("surname", surname);
        editor.apply();
    }

    public static String getUserEmail(Context context) {
        return getSharedPreferences(context).getString("email", null);
    }

    public static void setUserEmail(Context context, String email) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("email", email);
        editor.apply();
    }

    public static String getUserMobileNo(Context context) {
        return getSharedPreferences(context).getString("mobileNo", null);
    }

    public static void setUserMobileNo(Context context, String mobileNo) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("mobileNo", mobileNo);
        editor.apply();
    }

    public static String getUserAddr(Context context) {
        return getSharedPreferences(context).getString("address", null);
    }

    public static void setUserAddr(Context context, String address) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("address", address);
        editor.apply();
    }

    public static String getUserCity(Context context) {
        return getSharedPreferences(context).getString("city", null);
    }

    public static void setUserCity(Context context, String city) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("city", city);
        editor.apply();
    }

    public static String getUserDob(Context context) {
        return getSharedPreferences(context).getString("dob", null);
    }

    public static void setUserDob(Context context, String dob) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("dob", dob);
        editor.apply();
    }

    public static String getCode(Context context) {
        return getSharedPreferences(context).getString("code", null);
    }

    public static void setCode(Context context, String code) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("code", code);
        editor.apply();
    }

    public static String getUserId(Context context) {
        return getSharedPreferences(context).getString("userId", null);
    }

    public static void setUserId(Context context, String userId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("userId", userId);
        editor.apply();
    }

    public static void setFacebookLoginStatus(Context context, boolean b) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("fbLoginedIn", b);
        editor.apply();
    }

    public static Boolean getFacebookLoginStatus(Context context){
        return getSharedPreferences(context).getBoolean("fbLoginedIn", false);
    }

    public static void setCountry(Context con, String country) {
        SharedPreferences.Editor editor = getSharedPreferences(con).edit();
        editor.putString("country", country);
        editor.apply();
    }

    public static String getCountry(Context context)
    {
        return getSharedPreferences(context).getString("country", null);
    }
}
