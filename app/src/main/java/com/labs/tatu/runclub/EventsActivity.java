package com.labs.tatu.runclub;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.labs.tatu.runclub.helpers.BottomNavigationViewHelper;
import com.labs.tatu.runclub.model.Event;
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
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventsActivity extends AppCompatActivity {
    private static final String TAG = "EventsActivity";


    private RecyclerView mEventList;
    private DatabaseReference mDatabase;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);


        toolbar = (Toolbar) findViewById(R.id.events_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Events");

        mEventList=(RecyclerView)findViewById(R.id.events_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(this));



        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.ic_events:
                        startActivity(new Intent(EventsActivity.this,EventsActivity.class));
                        break;

                    case R.id.ic_friends:
                        startActivity(new Intent(EventsActivity.this,FriendsActivity.class));

                        break;

                    case R.id.ic_inbox:
                        startActivity(new Intent(EventsActivity.this,InboxActivity.class));

                        break;

                    case R.id.ic_stats:
                        startActivity(new Intent(EventsActivity.this,StatsActivity.class));

                        break;
                    case R.id.ic_run:
                        startActivity(new Intent(EventsActivity.this,MainActivity.class));

                        break;

                }
                return false;
            }
        });

        drawer();
    }
    public static class EventsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public EventsViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }
        public void setEventsName(String name)
        {
            TextView eventsName=(TextView)mView.findViewById(R.id.event_name);
            eventsName.setText(name);
        }
        public void setEventsDate(String date)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date firstDate = (Date) sdf.parse(date);
                SimpleDateFormat parseFormat = new SimpleDateFormat("E MMMM dd,yyyy");
                String eventDate=parseFormat.format(firstDate);

                TextView eventsDate=(TextView)mView.findViewById(R.id.event_date);
                eventsDate.setText(eventDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        public void setEventsTime(String time)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

            try {
                Date firstTime=sdf.parse(time);
                String eventTime=sdf.format(firstTime);

                TextView eventsTime=(TextView)mView.findViewById(R.id.event_time);
                eventsTime.setText(eventTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        public void setEventsAttending(String att)
        {
            TextView eventAtt=(TextView)mView.findViewById(R.id.event_attending);
            eventAtt.setText(String.valueOf(att)+" ATTENDING");
        }

        public void setEventsImage(Context ctx, String image)
        {
            ImageView events_image=(ImageView)itemView.findViewById(R.id.event_image);
            Picasso.with(ctx).load(image).into(events_image);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Event,EventsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Event, EventsViewHolder>(
                Event.class,
                R.layout.event_row,
                EventsViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(EventsViewHolder viewHolder, final Event model, final int position) {
                viewHolder.setEventsName(model.getEventName());
                Log.d(TAG, "populateViewHolder: "+model.getEventName());
                viewHolder.setEventsAttending (model.getEventAttending());
                viewHolder.setEventsDate(model.getEventDate());
                viewHolder.setEventsTime(model.getEventTime());
                viewHolder.setEventsImage(EventsActivity.this,model.getEventImage());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: You clicked on "+position);
                        Intent intent=new Intent(EventsActivity.this,SingleEventActivity.class);
                        intent.putExtra("eventName",model.getEventName());
                        startActivity(intent);
                    }
                });

            }
        };
        mEventList.setAdapter(firebaseRecyclerAdapter);




    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
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
                                startActivity(new Intent(EventsActivity.this,AddLocationActivity.class));
                                break;
                            case "AddEvent":
                                startActivity(new Intent(EventsActivity.this,AddEventActivity.class));
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
