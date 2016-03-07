package com.gmail.nelsonr462.crossover;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;

public class AddUrlActivity extends AppCompatActivity {

    private ArrayList<String> mTabGroups;
    private ArrayList<String> mTabGroupsId;
    private String mCurrentTabGroup;

    protected EditText mUrlField;
    protected EditText mTitleField;
    protected Button mSaveButton;
    protected Button mCancelButton;
    protected Spinner mTabGroupsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_url);

        mUrlField = (EditText) findViewById(R.id.addUrlText);
        mTitleField = (EditText) findViewById(R.id.addUrlTitle);
        mSaveButton = (Button) findViewById(R.id.addUrlSaveButton);
        mCancelButton = (Button) findViewById(R.id.addUrlCancelButton);

        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            mTabGroups = bundle.getStringArrayList(getString(R.string.addUrl_intent_extra_stringArray_TabGroups));
            mTabGroupsId = bundle.getStringArrayList(getString(R.string.addUrl_intent_extra_stringArray_TabGroupsId));
            mCurrentTabGroup = mTabGroups.get(0);
            mTabGroupsSpinner = (Spinner) findViewById(R.id.addUrlSpinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddUrlActivity.this, android.R.layout.simple_spinner_dropdown_item, mTabGroups);
            mTabGroupsSpinner.setAdapter(adapter);
            mTabGroupsSpinner.setSelection(bundle.getInt(getString(R.string.CurrentTabGroup)));
        }

        mTabGroupsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentTabGroup = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = mUrlField.getText().toString();
                final String title = mTitleField.getText().toString();

                if (!url.equals("")) {
                    final ParseObject tab = new ParseObject(ParseConstant.KEY_TAB);
                    tab.put(ParseConstant.KEY_TAB_URL, mUrlField.getText().toString());
                    tab.put(ParseConstant.KEY_TAB_TITLE, mTitleField.getText().toString());

                    tab.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Saved successfully.
                                ParseQuery query = ParseQuery.getQuery(ParseConstant.KEY_TABGROUP);
                                try {
                                    ParseObject tabGroup = query.get(mTabGroupsId.get(mTabGroups.indexOf(mCurrentTabGroup)));
                                    JSONArray tempTabs = tabGroup.getJSONArray(ParseConstant.KEY_TABGROUP_TABS);
                                    tempTabs.put(tab.getObjectId());
                                    tabGroup.put(ParseConstant.KEY_TABGROUP_TABS, tempTabs);
                                    tabGroup.saveInBackground();
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                // The save failed.
                            }
                        }
                    });

                }

                finish();
            }

        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

