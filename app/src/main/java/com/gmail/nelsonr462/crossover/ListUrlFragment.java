package com.gmail.nelsonr462.crossover;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUrlFragment extends Fragment {

    private ArrayList<String> mTabsView;
    private ArrayList<String> mTabs;
    private DynamicListView mDynamicListView;
    private ListView mListView;
    private String curGroupId;
    private Tab[] getTabs;
    protected CheckedAdapter<String> adapter;
    protected ToggleButton mSelectButton;
    protected Button mAllButton;
    protected ArrayList<String> urlSelect = new ArrayList<>();

    public static ListUrlFragment newInstance(String id, Tab[] tabs) {
        ListUrlFragment fragment = new ListUrlFragment();
        Bundle args = new Bundle();
        args.putParcelableArray("tabs", tabs);
        args.putString("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            curGroupId = getArguments().getString("id");
            mTabsView = new ArrayList<>();
            mTabs = new ArrayList<>();
            getTabs = (Tab[]) getArguments().getParcelableArray("tabs");
            if (getTabs.length != 0) {
                for (Tab tab : getTabs) {
                    if (!tab.getTitle().equals("")) {
                        mTabsView.add(tab.getTitle());
                    } else {
                        mTabsView.add(tab.getUrl());
                    }
                    mTabs.add(tab.getUrl());
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_url, container, false);
        mDynamicListView = (DynamicListView) rootView.findViewById(R.id.dynamiclistview);
        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAllButton = (Button) getView().findViewById(R.id.open_all_urls_button);
        mAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String url : mTabs) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, convertToUri(url));
                    startActivity(intent);
                }
            }
        });

        mSelectButton = (ToggleButton) getView().findViewById(R.id.select_specific_urls_button);
        mSelectButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSelectButton.setTextOn(getString(R.string.select_specific_urls_button_textOn));
                    mDynamicListView.setVisibility(View.INVISIBLE);
                    mListView = (ListView) getView().findViewById(R.id.listview);
                    mListView.setVisibility(View.VISIBLE);
                    adapter = new CheckedAdapter<>(getActivity(), android.R.layout.simple_list_item_checked, mTabsView);
                    mListView.setAdapter(adapter);

                    mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            if (mListView.isItemChecked(position)) {
                                // add the URL
                                urlSelect.add(mTabs.get(position));
                            } else {
                                // remove the URL
                                urlSelect.remove(mTabs.get(position));
                            }

                        }
                    });

                    mAllButton.setText(getString(R.string.cancel));
                    mAllButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            urlSelect = new ArrayList<>();
                            mSelectButton.toggle();
                        }
                    });

                } else {
                    if (urlSelect.size() != 0) {
                        for (int i = 0; i < urlSelect.size(); i++) {
                            try {
                                String url = urlSelect.get(i);
                                Intent intent = new Intent(Intent.ACTION_VIEW, convertToUri(url));
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        urlSelect = new ArrayList<>();
                    } else {
                        //Display a dialog here!
                    }
                    mSelectButton.setTextOff(getString(R.string.select_specific_urls_button_textOff));
                    mAllButton.setText(getString(R.string.open_all_urls_button_text));
                    mListView.setVisibility(View.INVISIBLE);
                    mDynamicListView.setVisibility(View.VISIBLE);
                    mAllButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (String url : mTabs) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, convertToUri(url));
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });

        ArrayAdapter<String> myAdapter = new DragNDropAdapter(getActivity(), mTabsView);
        SimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new SimpleSwipeUndoAdapter(myAdapter, getActivity(), new MyOnDismissCallback(myAdapter));
        SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(simpleSwipeUndoAdapter);
        animationAdapter.setAbsListView(mDynamicListView);
        mDynamicListView.setAdapter(animationAdapter);

        mDynamicListView.enableDragAndDrop();
        mDynamicListView.setDraggableManager(new TouchViewDraggableManager(R.id.list_row_draganddrop_touchview));
        mDynamicListView.setOnItemMovedListener(new MyOnItemMovedListener(myAdapter));
        mDynamicListView.setOnItemLongClickListener(new MyOnItemLongClickListener(mDynamicListView));
        mDynamicListView.setOnItemClickListener(new MyOnItemClickListener(mDynamicListView));
        mDynamicListView.enableSimpleSwipeUndo();


    }

    private Uri convertToUri(String url) {
        if (!url.contains("http://")) {
            url = "http://" + url;
        }
        if (!url.contains(".")) {
            url = url + ".com";
        }
        Uri uri = Uri.parse(url);
        return uri;
    }

    public class MyOnItemMovedListener implements OnItemMovedListener {

        private final ArrayAdapter<String> mAdapter;

        MyOnItemMovedListener(final ArrayAdapter<String> adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onItemMoved(final int startPosition, final int endPosition) {

            mSelectButton = (ToggleButton) getView().findViewById(R.id.select_specific_urls_button);
            mSelectButton.setText(getString(R.string.delete_url_confirmText));
            final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
            mSelectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (endPosition - startPosition > 0) { //User dragging from top to bottom
                        Tab.dragTopNBot(getTabs, startPosition, endPosition);
                    } else {
                        Tab.dragBotNTop(getTabs, startPosition, endPosition);
                    }

                    mSelectButton.toggle();
                    mProgressDialog.setMessage("Saving...");
                    mProgressDialog.show();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstant.KEY_TABGROUP);
                    query.getInBackground(curGroupId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject TabGroup, ParseException e) {
                            if (e == null) {
                                JSONArray jArray = new JSONArray();
                                int i = 0;
                                for (Tab tempTab : getTabs) {
                                    try {
                                        jArray.put(i, tempTab.getObjectId());
                                        i++;
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                TabGroup.put(ParseConstant.KEY_TABGROUP_TABS, jArray);
                                TabGroup.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                        } else {
                                            e.printStackTrace();
                                        }
                                        ((MainActivity) getActivity()).updateTabGroups(curGroupId, getTabs);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        ListUrlFragment mFragment = ListUrlFragment.newInstance(curGroupId, getTabs);
                                        fragmentTransaction.replace(R.id.content_main_ListUrlFrag, mFragment).commit();
                                        mProgressDialog.dismiss();
                                    }
                                });
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            mAllButton = (Button) getView().findViewById(R.id.open_all_urls_button);
            mAllButton.setText(getString(R.string.cancel));
            mAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ListUrlFragment mFragment = ListUrlFragment.newInstance(curGroupId, getTabs);
                    fragmentTransaction.replace(R.id.content_main_ListUrlFrag, mFragment).commit();
                }
            });

        }

    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

        private long lastClickTime = 0;
        private final DynamicListView mListView;

        public MyOnItemClickListener(final DynamicListView listView) {
            mListView = listView;
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                onDoubleClick(view,position);
            } else {
                onSingleClick(view);
            }
            lastClickTime = clickTime;
        }

        public void onSingleClick(View v) {
            Log.v("MainActivity","single click");
        }

        public void onDoubleClick(View v, int position) {
            Log.v("MainActivity", "double click");
        }

    }

    private static class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private final DynamicListView mListView;

        MyOnItemLongClickListener(final DynamicListView listView) {
            mListView = listView;
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            if (mListView != null) {
                mListView.startDragging(position - mListView.getHeaderViewsCount());
            }
            return true;
        }
    }

    private class MyOnDismissCallback implements OnDismissCallback {

        private final ArrayAdapter<String> mAdapter;

        @Nullable
        private Toast mToast;

        MyOnDismissCallback(final ArrayAdapter<String> adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
            String item = "";
            int delPosition = -1;
            for (int position : reverseSortedPositions) {
                item = mAdapter.getItem(position);
                delPosition = position;
                mAdapter.remove(position);
            }
            final Tab[] tempTabs = getTabs;
            List<Tab> list = new ArrayList<>(Arrays.asList(getTabs));
            list.remove(delPosition);
            getTabs = list.toArray(new Tab[list.size()]);

            mSelectButton = (ToggleButton) getView().findViewById(R.id.select_specific_urls_button);
            mSelectButton.setText(getString(R.string.delete_url_confirmText));
            final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
            mSelectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectButton.toggle();
                    mProgressDialog.setMessage("Saving...");
                    mProgressDialog.show();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstant.KEY_TABGROUP);
                    query.getInBackground(curGroupId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject TabGroup, ParseException e) {
                            if (e == null) {
                                JSONArray jArray = new JSONArray();
                                int i = 0;
                                for (Tab tempTab : getTabs) {
                                    try {
                                        jArray.put(i, tempTab.getObjectId());
                                        i++;
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                TabGroup.put(ParseConstant.KEY_TABGROUP_TABS, jArray);
                                TabGroup.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                        } else {
                                            e.printStackTrace();
                                        }
                                        ((MainActivity)getActivity()).updateTabGroups(curGroupId, getTabs);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        ListUrlFragment mFragment = ListUrlFragment.newInstance(curGroupId, getTabs);
                                        fragmentTransaction.replace(R.id.content_main_ListUrlFrag, mFragment).commit();
                                        mProgressDialog.dismiss();
                                    }
                                });
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            mAllButton = (Button) getView().findViewById(R.id.open_all_urls_button);
            mAllButton.setText(getString(R.string.cancel));
            mAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ListUrlFragment mFragment = ListUrlFragment.newInstance(curGroupId,tempTabs);
                    fragmentTransaction.replace(R.id.content_main_ListUrlFrag, mFragment).commit();
                }
            });

            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(
                    getActivity(),
                    "removed " + item + " from " + ((MainActivity) getActivity()).getSupportActionBar().getTitle(),
                    Toast.LENGTH_LONG
            );
            mToast.show();
        }
    }

}
