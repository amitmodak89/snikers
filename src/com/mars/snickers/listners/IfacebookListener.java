package com.mars.snickers.listners;

import com.facebook.Session;
import com.facebook.model.GraphUser;

/**
 * Created by malpani on 9/7/14.
 */
public interface IfacebookListener {
    public void setUser(GraphUser user);

    public GraphUser getUser();

    public void handlePendingFbAction();

    public Session getSession();

    public void setImagePath(String imagePath);

    public void setMessage(String message);

    public boolean getFacebookSessionState();

}
