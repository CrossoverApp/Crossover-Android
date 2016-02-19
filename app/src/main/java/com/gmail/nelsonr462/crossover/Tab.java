package com.gmail.nelsonr462.crossover;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Jeffrey on 2/14/2016.
 */
public class Tab {
    private String mObjectId;
    private String mTitle;
    private String mUrl;

    //Used in getTab Method
    private Tab mTab;
    private boolean taskDone;

    public Tab(String id, String title, String url) {
        mObjectId = id;
        mTitle = title;
        mUrl = url;
    }

    public Tab() {

    }

    public Tab getTab(String id) {
        //Get all information within the tab with the corresponding object ID
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstant.KEY_TAB);
        try {
            ParseObject tab = query.get(id);
            mTab = new Tab(
                    tab.getObjectId(),
                    tab.getString(ParseConstant.KEY_TAB_TITLE),
                    tab.getString(ParseConstant.KEY_TAB_URL)
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return mTab;
    }


    public String getObjectId() {
        return mObjectId;
    }

    public void setObjectId(String objectId) {
        mObjectId = objectId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
