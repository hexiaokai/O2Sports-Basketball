package com.o2sports.hxiao.o2sports_basketball;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.o2sports.hxiao.o2sports_basketball.entity.*;
import com.o2sports.hxiao.o2sports_basketball.fragment.ArenaListFragment;
import com.o2sports.hxiao.o2sports_basketball.fragment.ArenaProfileFragment;
import com.o2sports.hxiao.o2sports_basketball.fragment.FriendListFragment;
import com.o2sports.hxiao.o2sports_basketball.fragment.PlayerProfileFragment;
import com.o2sports.hxiao.o2sports_basketball.fragment.SocialFragment;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, PlayerProfileFragment.OnFragmentInteractionListener,
        ArenaListFragment.OnFragmentInteractionListener, SocialFragment.OnFragmentInteractionListener, FriendListFragment.OnFragmentInteractionListener,
        ArenaProfileFragment.OnFragmentInteractionListener, View.OnClickListener
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;


    public static MobileServiceClient mClient;

    public static PlayerProfileFragment mPlayerProfile;
    public static FriendListFragment mFriends;
    public static ArenaListFragment mArenas;
    public static SocialFragment mBlogs;

    public String localPlayerID = "1c211a55-2a53-4152-b74f-ece1606e172a";

    public static PlayerProfileFragment mFriendProfile;
    public static ArenaProfileFragment mArenaProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // put actionbar to bottom - failed
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        try {
            mClient = new MobileServiceClient(
                    "https://o2service.azure-mobile.net/",
                    "qJNqJihCYMDTfwYsbHbfURxaOfUNwh32",
                    this
            );
        }
        catch (Exception e)
        {
            messageDialog("Cannot connect to service");
        }

        //Get local player ID
        String FILENAME = "playerID";
        try
        {
            FileInputStream fis = openFileInput(FILENAME);
            byte[] buffer = new byte[]{};
            fis.read(buffer);
            this.localPlayerID = buffer.toString();

        }
        catch (FileNotFoundException fileNotFoundException)
        {
            Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(mIntent);
            //startActivityForResult(mIntent, R.layout.activity_main);

        }
        catch (IOException iOException)
        {
            messageDialog(iOException.getMessage());
        }

    }

    //Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case R.layout.activity_main:
                if(requestCode==20){
                    localPlayerID= data.getStringExtra("ID");
                }
                break;

            default:
                break;
        }
    }


    protected void messageDialog(String dialogMessage)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(dialogMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.this.finish();
            }
        });
        builder.create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        while (getSupportFragmentManager().getBackStackEntryCount() > 0){

                getSupportFragmentManager().popBackStackImmediate();
        }


        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onFragmentInteraction(Uri uri)    {    }

    @Override
    public void onFragmentInteraction(String id)
    {
    }

    public void checkInClicked(View v){
        mArenaProfile.checkIn(v);
    }

    public void registerClicked(View v){
        mArenaProfile.register(v);
    }



    @Override
    public void onClick(View v) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction;

        while (manager.getBackStackEntryCount() > 0){

            manager.popBackStackImmediate();
        }


        if (v.getTag() instanceof Player) {

            Player p = (Player) v.getTag();

            if (mFriendProfile == null || mFriendProfile.playerID != p.id) {
                mFriendProfile = PlayerProfileFragment.newInstance(p.id);
            }
            fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.hide(mFriends);
            fragmentTransaction.add(android.R.id.content, mFriendProfile);
            fragmentTransaction.addToBackStack("Player");
            fragmentTransaction.commit();
        }
        else
        {
            if (v.getTag() instanceof Arena) {


                Arena a = (Arena) v.getTag();

                if (mArenaProfile == null || mArenaProfile.arenaID != a.id) {
                    mArenaProfile = ArenaProfileFragment.newInstance(a.id);

                }

                fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.hide(mArenas);
                fragmentTransaction.add(android.R.id.content, mArenaProfile);
                fragmentTransaction.addToBackStack("Arena");
                fragmentTransaction.commit();

            }
            else
            {
                // do nothing
            }
        }
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    if (mPlayerProfile == null) {
                        mPlayerProfile = PlayerProfileFragment.newInstance(localPlayerID);
                    }
                    return mPlayerProfile;
                case 1:
                    if (mFriends == null){
                        mFriends = FriendListFragment.newInstance(localPlayerID);
                    }
                    return mFriends;
                case 2:
                    if (mArenas == null){
                        mArenas = ArenaListFragment.newInstance("1");
                    }
                    return mArenas;
                case 3:
                    if (mBlogs == null){
                        mBlogs = SocialFragment.newInstance();
                    }
                    return mBlogs;
            }

            return null;


        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }

}
