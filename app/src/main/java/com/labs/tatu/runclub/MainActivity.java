package com.labs.tatu.runclub;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.labs.tatu.runclub.fragments.ChallengesFragment;
import com.labs.tatu.runclub.fragments.LocationsFragment;
import com.labs.tatu.runclub.fragments.QuickStartFragment;
import com.labs.tatu.runclub.helpers.BottomNavigationViewHelper;
import com.labs.tatu.runclub.helpers.SectionsPagerAdapter;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager mViewPager;
    private Toolbar toolbar;
    public static TextView toolBarTitle;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
//            case R.id.ic_search:

//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");




        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        sectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager=(ViewPager)findViewById(R.id.container);

        setupViewPager(mViewPager);

        TabLayout tabLayout=(TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_my_location_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_directions_run_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_format_align_left);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(2);
        menuItem.setChecked(true);





        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch(item.getItemId())
                    {
                        case R.id.ic_events:
                            startActivity(new Intent(MainActivity.this,EventsActivity.class));
                            break;


                        case R.id.ic_friends:
                            startActivity(new Intent(MainActivity.this,FriendsActivity.class));

                            break;

                        case R.id.ic_inbox:
                            startActivity(new Intent(MainActivity.this,InboxActivity.class));

                            break;

                        case R.id.ic_stats:
                            startActivity(new Intent(MainActivity.this,StatsActivity.class));

                            break;
                        case R.id.ic_run:
                            startActivity(new Intent(MainActivity.this,MainActivity.class));

                            break;


                    }
                    return false;
                }
        });

        drawer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPagerAdapter mAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new LocationsFragment());
        mAdapter.addFragment(new QuickStartFragment());
        mAdapter.addFragment(new ChallengesFragment());

        viewPager.setAdapter(mAdapter);
    }


    public void drawer()
    {

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Alpha Beta").withEmail("alpha.beta@gmail.com").withIcon(getResources().getDrawable(R.drawable.ic_account_circle))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Home").withTag("Home");
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(1).withName("Profile").withTag("Profile");
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(1).withName("Add Location").withTag("AddLoc");
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(1).withName("Add Event").withTag("AddEvent");
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(1).withName("Awards").withTag("Awards");
        SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(2).withName("Log Out").withTag("LogOut");

//create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1.withIcon(R.drawable.ic_home_black_24dp),
                        item2.withIcon(R.drawable.ic_account_box_black_24dp),
                        item3.withIcon(R.drawable.ic_add_location_black_24dp),
                        item4.withIcon(R.drawable.ic_event_note_black_24dp),
                        item5.withIcon(R.drawable.ic_action_achievement),

                        new DividerDrawerItem(),
                        item6.withIcon(R.drawable.ic_log_out_black_24dp)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (drawerItem.getTag().toString())
                        {
                            case "AddLoc":
                                startActivity(new Intent(MainActivity.this,AddLocationActivity.class));
                                break;
                            case "AddEvent":
                                startActivity(new Intent(MainActivity.this,AddEventActivity.class));
                                break;

                        }
                        return true;
                    }
                })
                .withAccountHeader(headerResult)
                .build();

        result.setSelection(1);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
    }




}
