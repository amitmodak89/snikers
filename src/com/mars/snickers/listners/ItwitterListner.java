package com.mars.snickers.listners;

import java.io.File;

/**
 * Created by malpani on 9/10/14.
 */
public interface ItwitterListner {

    public void postTweet(String message);

    public void postImageTweet(String message, File imageFile);

}
