package com.gmail.nelsonr462.crossover;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jeffrey on 2/14/2016.
 */
public class Tab implements Parcelable{
    private String mObjectId;
    private String mTitle;
    private String mUrl;

    //Used in getTab Method
    private static Tab mTab;

    protected Tab(Parcel in) {
        mObjectId = in.readString();
        mTitle = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<Tab> CREATOR = new Creator<Tab>() {
        @Override
        public Tab createFromParcel(Parcel in) {
            return new Tab(in);
        }

        @Override
        public Tab[] newArray(int size) {
            return new Tab[size];
        }
    };

    public static Tab[] dragTopNBot(Tab[] getTabs,int startPosition, int endPosition){
        Tab mTab = getTabs[startPosition];
        for (int i = startPosition ; i < endPosition ; i++ ) {
            getTabs[i] = getTabs[i+1];
        }
        getTabs[endPosition] = mTab;
        return getTabs;
    }

    public static Tab[] dragBotNTop(Tab[] getTabs,int startPosition, int endPosition){
        Tab mTab = getTabs[startPosition];
        for (int i = startPosition ; i > endPosition ; i-- ) {
            getTabs[i] = getTabs[i-1];
        }
        getTabs[endPosition] = mTab;
        return getTabs;
    }

    public static Tab[] addTab(Tab[] getTabs, Tab newTab , int position) {
        List<Tab> list = new ArrayList<>(Arrays.asList(getTabs));
        list.add(position,newTab);
        getTabs = list.toArray(new Tab[list.size()]);
        return getTabs;
    }

    public Tab(String id, String title, String url) {
        mObjectId = id;
        mTitle = title;
        mUrl = url;
    }

    public Tab() {

    }

    public static Tab getTab(String id) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mObjectId);
        dest.writeString(mTitle);
        dest.writeString(mUrl);
    }
}
