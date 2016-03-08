package com.gmail.nelsonr462.crossover;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ParseUser mCurrentUser;
    private ArrayList<TabGroup> mTabGroups;
    private NavigationView mNavigationView;
    private int mCurrentTabGroup;
    private int mTabSwitch = 0;
    private int mUpdateControl = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // *** PARSE LOGIN CHECK *** //
        mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser == null) {
            navigateToLogin();
        } else {
            Snackbar.make(findViewById(R.id.drawer_layout), "Logged in!", Snackbar.LENGTH_LONG).show();


        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled} // enabled
        };

        int[] colors = new int[] {
                Color.WHITE
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setBackgroundColor(Color.parseColor("#2C3136"));
        mNavigationView.setItemTextColor(colorStateList);
        View navHeader = mNavigationView.getHeaderView(0);
        TextView logoText = (TextView)navHeader.findViewById(R.id.logoText);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        logoText.setTypeface(typeface);



        updateTabGroups("Please Wait...","Loading Your Tabs...", mUpdateControl);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mCurrentUser == null) {
            navigateToLogin();
        } else {
            updateTabGroups("", "Loading..", mUpdateControl);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if(mCurrentTabGroup == 0) {
            menu.findItem(R.id.action_delete_tab_group).setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_tab_group) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstant.KEY_TABGROUP);
            String objectId = mTabGroups.get(mCurrentTabGroup).getObjectId();
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            updateTabGroups("Deleting Tab Group", "Loading your tabs", mUpdateControl);
                            switchToTabGroup(0, 0);
                        }
                    });
                }
            });
            return true;
        }

        if (id == R.id.action_logout) {
            ParseUser.logOut();
            navigateToLogin();
        }

        if (id == R.id.action_addurl) {
            Intent intent = new Intent(this, AddUrlActivity.class);
            ArrayList<String> temp = new ArrayList<>();
            ArrayList<String> tempId = new ArrayList<>();
            for (TabGroup tabGroup : mTabGroups) {
                temp.add(tabGroup.getTitle());
                tempId.add(tabGroup.getObjectId());
            }
            intent.putStringArrayListExtra(getString(R.string.addUrl_intent_extra_stringArray_TabGroups), temp);
            intent.putStringArrayListExtra(getString(R.string.addUrl_intent_extra_stringArray_TabGroupsId), tempId);
            intent.putExtra(getString(R.string.CurrentTabGroup), mCurrentTabGroup);
            startActivity(intent);
        }

        if(id == R.id.action_add_tab_group) {
            createTabGroup();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createTabGroup() {
        View view = getLayoutInflater().inflate(R.layout.activity_add_url, null);
        TextView textView = (TextView) view.findViewById(R.id.addingToText);
        textView.setVisibility(View.GONE);
        Spinner mSpinner = (Spinner) view.findViewById(R.id.addUrlSpinner);
        mSpinner.setVisibility(View.GONE);
        Button mButton = (Button) view.findViewById(R.id.addUrlSaveButton);
        mButton.setVisibility(View.GONE);
        Button mButton2 = (Button) view.findViewById(R.id.addUrlCancelButton);
        mButton2.setVisibility(View.GONE);
        EditText mEditTextUrl = (EditText) view.findViewById(R.id.addUrlText);
        mEditTextUrl.setVisibility(View.GONE);
        final EditText mEditTextTitle = (EditText) view.findViewById(R.id.addUrlTitle);
        mEditTextTitle.setHint("Tab Group Title");

        new AlertDialog.Builder(this)
                .setTitle("Add New Tab Group")
                .setView(view)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String title = mEditTextTitle.getText().toString();
                        if (!title.isEmpty()) {
                            final ParseObject tabGroup = new ParseObject(ParseConstant.KEY_TABGROUP);
                            tabGroup.put(ParseConstant.KEY_TAB_TITLE, title);
                            tabGroup.put(ParseConstant.KEY_TABGROUP_USER, mCurrentUser);
                            tabGroup.put(ParseConstant.KEY_TABGROUP_CANDELETE, true);

                            tabGroup.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // Saved successfully.
                                        Snackbar.make(findViewById(R.id.drawer_layout), "Tab Group Created!", Snackbar.LENGTH_LONG).show();
                                        updateTabGroups("Loading New Tab Group...", "", mUpdateControl);
                                    } else {
                                        // The save failed.
                                        Snackbar.make(findViewById(R.id.drawer_layout), "Failed to save! Try again later.", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switchToTabGroup(id, 0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // INT TYPE = TYPE OF SWITCH;
    // 0 = ONCREATE AND NAVIGATION
    // 1 = ONRESUME
    private void switchToTabGroup(int id, int type) {
        if(type == 0) {
            if(id == 0) {

            }
            MenuItem menuItem = mNavigationView.getMenu().getItem(id);

            try {
                getSupportActionBar().setTitle(menuItem.getTitle());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            TabGroup tabGroup = mTabGroups.get(id);

            //Switch to the corresponding Tabgroup that has the specific tabs
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ListUrlFragment mFragment = ListUrlFragment.newInstance(tabGroup.getObjectId(), tabGroup.getTabs());
            fragmentTransaction.replace(R.id.content_main_ListUrlFrag, mFragment).commit();
            mCurrentTabGroup = id;
            invalidateOptionsMenu();
        }
    }

    public void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    public void updateTabGroups(String title, String message, int control) {
        if(control == 0) {
            mUpdateControl = 1;
            final ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(message);
            mProgressDialog.show();

            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstant.KEY_TABGROUP);
            query.whereEqualTo(ParseConstant.KEY_TABGROUP_USER, mCurrentUser);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> allTabGroups, ParseException e) {
                    mTabGroups = new ArrayList<TabGroup>();
                    if (e == null) {
                        int a = 0;
                        for (ParseObject mTabGroup : allTabGroups) {
                            JSONArray tempTab = mTabGroup.getJSONArray(ParseConstant.KEY_TABGROUP_TABS);
                            Tab[] mTabs = new Tab[tempTab.length()];
                            try {
                                for (int i = 0; i < tempTab.length(); i++) {
                                    mTabs[i] = Tab.getTab(tempTab.getString(i));
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            TabGroup temp = new TabGroup(
                                    mTabGroup.getObjectId(),
                                    mTabGroup.getString(ParseConstant.KEY_TABGROUP_TITLE),
                                    mTabs
                            );
                            mTabGroups.add(temp);
                            a++;
                        }

                    } else {

                    }

                    Menu drawerMenu = mNavigationView.getMenu();
                    drawerMenu.clear();

                    // Move Home Group to front of mTabGroups
                    for (int j = 0; j < mTabGroups.size(); j++) {
                        TabGroup temp = mTabGroups.get(j);
                        if (temp.getTitle().equals(getBaseContext().getString(R.string.first_group_name))) {
                            mTabGroups.remove(j);
                            mTabGroups.add(0, temp);
                        }
                    }

                    // Populate Drawer Menu
                    for (int i = 0; i < mTabGroups.size(); i++) {
                        TabGroup tabGroup = mTabGroups.get(i);
                        drawerMenu.add(0, i, i, tabGroup.getTitle());
                    }

                    mNavigationView.setNavigationItemSelectedListener(MainActivity.this);

                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);

                    // Switches to Home Group on first call, turns off afterwards.
                    switchToTabGroup(0, mTabSwitch);
                    mTabSwitch = 1;

                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    drawer.setDrawerListener(toggle);
                    toggle.syncState();

                    mProgressDialog.dismiss();
                    mUpdateControl = 0;

                }
            });
        }
    }

    public void updateTabGroups(String id, Tab[] mTabs) {
        for ( TabGroup temp : mTabGroups ) {
            if ( temp.getObjectId().equals(id) ) {
                temp.setTabs(mTabs);
            }
        }
    }

}