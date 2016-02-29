package com.gmail.nelsonr462.crossover;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class ListUrlFragment extends ListFragment {

    private ArrayList<String> mTabsView;
    private ArrayList<String> mTabs;
    private String curGroupId;
    protected ArrayAdapter<String> adapter;
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
            Tab[] tabs = (Tab[]) getArguments().getParcelableArray("tabs");
            if (tabs.length != 0) {
                for (Tab tab : tabs) {
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
        return inflater.inflate(R.layout.fragment_list_url, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mTabsView);
        setListAdapter(adapter);

        mSelectButton = (ToggleButton) getView().findViewById(R.id.select_specific_urls_button);
        mSelectButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.fabMenuHide();
                    mSelectButton.setTextOn(getString(R.string.select_specific_urls_button_textOn));
                    adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_checked, mTabsView);
                    setListAdapter(adapter);

                    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            if (getListView().isItemChecked(position)) {
                                // add the URL
                                urlSelect.add(mTabs.get(position));
                            } else {
                                // remove the URL
                                urlSelect.remove(mTabs.get(position));
                            }

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
                    MainActivity.fabMenuShow();
                    mSelectButton.setTextOff(getString(R.string.select_specific_urls_button_textOff));
                    adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mTabsView);
                    setListAdapter(adapter);
                }
            }
        });

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


}
