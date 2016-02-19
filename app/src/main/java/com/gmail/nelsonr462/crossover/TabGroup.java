package com.gmail.nelsonr462.crossover;

/**
 * Created by Jeffrey on 2/14/2016.
 */
public class TabGroup {
    private String mObjectId;
    private String mTitle;
    private Tab[] mTabs;

    public TabGroup(String id, String title, Tab[] tabs) {
        mObjectId = id;
        mTitle = title;
        mTabs = tabs;
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

    public Tab[] getTabs() {
        return mTabs;
    }

    public void setTabs(Tab[] tabs) {
        mTabs = tabs;
    }
}
