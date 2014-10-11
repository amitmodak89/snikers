package com.mars.snickers;

import android.app.Application;
import android.media.MediaPlayer;
import android.util.Log;

import com.mars.snickers.prefs.Prefs;

/**
 * Created by malpani on 9/13/14.
 */
public class ApplicationController extends Application {

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        mediaPlayer = MediaPlayer.create(ApplicationController.this, R.raw.music);
        mediaPlayer.setLooping(true);
        Prefs.setMediaPlayerPlayingStatus(this, false);
    }

    public void startMediaPlayer() {
        if (Prefs.getDidUserSetToPlayMusic(this))
            if (mediaPlayer != null && !Prefs.getMediaPlayerPlayingStatus(this)) {
                Prefs.setMediaPlayerPlayingStatus(this, true);
                mediaPlayer.start();
                Log.d("music", "start");
            } else {
                Log.d("music", "start failed");
            }
    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null && Prefs.getMediaPlayerPlayingStatus(this)) {
            Prefs.setMediaPlayerPlayingStatus(this, false);
            mediaPlayer.stop();
            Log.d("music", "stop");
        } else {
            Log.d("music", "stop failed");
        }
    }

    public void pauseMediaPlayer() {
        if (mediaPlayer != null && Prefs.getMediaPlayerPlayingStatus(this)) {
            Prefs.setMediaPlayerPlayingStatus(this, false);
            mediaPlayer.pause();
            Log.d("music", "pause");
        } else {
            Log.d("music", "pause failed");
        }
    }

    public void playMusic() {
        boolean userPreference = Prefs.getDidUserSetToPlayMusic(this);
        Prefs.setDidUserSetToPlayMusic(this, !userPreference);
        if (!userPreference) {
            startMediaPlayer();
        } else {
            pauseMediaPlayer();
        }
    }


    @Override
    public void onTerminate() {
        stopMediaPlayer();
        super.onTerminate();
    }
}
