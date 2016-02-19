package com.gmail.nelsonr462.crossover;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListUrlFragment extends ListFragment {

    private ArrayList<String> mTabs;
    protected ArrayAdapter<String> adapter;
    protected Button mSelectButton;
    protected Button mAllButton;
    protected ArrayList<String> urlSelect = new ArrayList<>();

    public static ListUrlFragment newInstance(ArrayList<String> tabs) {
        ListUrlFragment fragment = new ListUrlFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("tabs", tabs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTabs = getArguments().getStringArrayList("tabs");
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
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_checked, mTabs);
        setListAdapter(adapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), parent.getItemAtPosition(position) + " is selected",
//                        Toast.LENGTH_LONG).show();

                if (getListView().isItemChecked(position)) {
                    // add the URL
                    urlSelect.add(parent.getItemAtPosition(position) + "");
                } else {
                    // remove the URL
                    urlSelect.remove(parent.getItemAtPosition(position) + "");
                }

            }
        });

        mSelectButton = (Button) getView().findViewById(R.id.select_specific_urls_button);
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urlSelect.size() != 0) {
                    for (int i = 0; i < urlSelect.size(); i++) {
                        Uri uri = Uri.parse("http://" + urlSelect.get(i)); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                } else {
                    //Display a dialog here!
                }

            }
        });

        mAllButton = (Button) getView().findViewById(R.id.open_all_urls_button);
        mAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String i : mTabs) {
                    Uri uri = Uri.parse("http://" + i); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });


    }


}
