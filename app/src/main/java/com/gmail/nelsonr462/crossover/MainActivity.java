package com.gmail.nelsonr462.crossover;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ParseUser mCurrentUser;
    private TabGroup[] mTabGroups;
    private static FloatingActionsMenu mManageActions;

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
        }

        mManageActions = (FloatingActionsMenu) findViewById(R.id.manage_actions);

        RelativeLayout mFrameLayout = (RelativeLayout) findViewById(R.id.content_main);
        mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManageActions.collapseImmediately();
            }
        });

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.floating_button_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddUrlActivity.class);
                ArrayList<String> temp = new ArrayList<>();
                ArrayList<String> tempId = new ArrayList<>();
                for (TabGroup tabGroup : mTabGroups) {
                    temp.add(tabGroup.getTitle());
                    tempId.add(tabGroup.getObjectId());
                }
                intent.putStringArrayListExtra(getString(R.string.addUrl_intent_extra_stringArray_TabGroups), temp);
                intent.putStringArrayListExtra(getString(R.string.addUrl_intent_extra_stringArray_TabGroupsId), tempId);
                startActivity(intent);
            }
        });

        updateTabGroups("Please Wait...","Loading Your Tabs...");

    }

    @Override
    public void onResume() {
        super.onResume();
        updateTabGroups("","Loading..");
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        try {
            getSupportActionBar().setTitle(item.getTitle());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        TabGroup tabGroup = mTabGroups[id];

        //Switch to the corresponding Tabgroup that has the specific tabs
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ListUrlFragment mFragment = ListUrlFragment.newInstance(tabGroup.getObjectId(),tabGroup.getTabs());
        fragmentTransaction.replace(R.id.content_main_ListUrlFrag, mFragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    public void updateTabGroups(String title, String message) {

        final ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstant.KEY_TABGROUP);
        query.whereEqualTo(ParseConstant.KEY_TABGROUP_USER, mCurrentUser);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> allTabGroups, ParseException e) {
                mTabGroups = new TabGroup[allTabGroups.size()];
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
                        mTabGroups[a] = temp;
                        a++;
                    }

                } else {

                }

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                Menu drawerMenu = navigationView.getMenu();
                drawerMenu.clear();

                for (int i = 0; i < mTabGroups.length; i++) {
                    TabGroup tabGroup = mTabGroups[i];
                    if (!tabGroup.getTitle().equals(getBaseContext().getString(R.string.first_group_name))) {
                        drawerMenu.add(0, i, i + 1, tabGroup.getTitle());
                    } else {
                        drawerMenu.add(0, i, 0, getString(R.string.first_group_name));
                    }
                }

                navigationView.setNavigationItemSelectedListener(MainActivity.this);

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();

                mProgressDialog.dismiss();

            }
        });
    }

    public static void fabMenuHide() {
        mManageActions.setVisibility(View.INVISIBLE);
    }

    public static void fabMenuShow() {
        mManageActions.setVisibility(View.VISIBLE);
    }

}