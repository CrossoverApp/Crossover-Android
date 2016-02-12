package com.gmail.nelsonr462.crossover;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
    private ArrayList<String> mTabsId = new ArrayList<String>();
    private ArrayList<String> mTabs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // *** PARSE LOGIN CHECK *** //
        mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser == null) {
            navigateToLogin();
        } else {
            Snackbar.make(findViewById(R.id.drawer_layout), "Logged in!", Snackbar.LENGTH_LONG).show();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        final Menu drawerMenu = navigationView.getMenu();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("TabGroup");
        query.whereEqualTo("user", mCurrentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> tabGroups, ParseException e) {
                if (e == null) {
                    for (ParseObject tabGroup : tabGroups) {
                        drawerMenu.add(tabGroup.getString("title"));
                    }
                } else {
                    //TODO
                }
            }
        });


        navigationView.setNavigationItemSelectedListener(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mTabsId = new ArrayList<String>();
        mTabs = new ArrayList<String>();
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        ParseQuery query = ParseQuery.getQuery("TabGroup");
        query.whereEqualTo("user", mCurrentUser);
        query.whereEqualTo("title", item.getTitle());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> tabs, ParseException e) {
                if (e == null) {
                    for (ParseObject tab : tabs) {
                        JSONArray tempTabs = tab.getJSONArray("tabs");
                        if (tempTabs != null) {
                            try {
                                for (int i = 0; i < tempTabs.length(); i++) {
                                    mTabsId.add(tempTabs.getString(i));
                                }
                                //Get the tabs with the corresponding tab ObjectId from mTabsId
                                ParseQuery<ParseObject> queryTab = ParseQuery.getQuery("Tab");
                                queryTab.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> tabs, ParseException e) {
                                        for (ParseObject tab : tabs) {
                                            if (e == null && mTabsId.contains(tab.getObjectId())) {
                                                mTabs.add(tab.getString("url"));
                                            } else {

                                            }
                                        }

                                        //Switch to the corresponding Tabgroup that has the specific tabs
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        ListUrlFragment mFragment = ListUrlFragment.newInstance(mTabs);
                                        fragmentTransaction.replace(R.id.content_main_ListUrlFrag, mFragment).commit();
                                    }
                                });

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                } else {
                    //TODO
                }
            }
        });


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

}