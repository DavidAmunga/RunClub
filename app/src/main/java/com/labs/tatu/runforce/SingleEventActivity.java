package com.labs.tatu.runforce;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;
import com.labs.tatu.runforce.helpers.BottomNavigationViewHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SingleEventActivity extends AppCompatActivity {
    private static final String TAG = "SingleEventActivity";

    private Toolbar toolbar;
    private ImageView eventImage;
    private TextView txt_event_name,txt_event_loc,txt_event_type,txt_event_fee,txt_event_date,txt_event_time,txt_event_details;
    private Button btn_interested,btn_notInterested;

    private DatabaseReference mDatabase;

    private boolean isInterested=true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        //        Set No Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
        eventImage=(ImageView)findViewById(R.id.bgEventImage);

        initViews();

        btn_interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.child("eventName").getValue().equals(txt_event_name.getText().toString().trim()) && isInterested==true)
                            {

//                                Get Snapshot value
                                Log.d(TAG, "onInterested: Success");
                                int count=1;
                                int attending=Integer.parseInt(snapshot.child("eventAttending").getValue().toString());


                                DatabaseReference eventAttending=snapshot.getRef().child("eventAttending");
                                eventAttending.setValue(String.valueOf(attending+1));

//                                Remove Listener
                                String user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference eventRef=FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("userEvents");


//                                Add Event to User Events
                                user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference userRef=FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("userEvents").push();
                                userRef.child("eventName").setValue(txt_event_name.getText().toString().trim());
                                userRef.child("eventLocation").setValue(txt_event_loc.getText().toString().trim());
                                userRef.child("eventDate").setValue(txt_event_date.getText().toString().trim());
                                userRef.child("eventTime").setValue(txt_event_time.getText().toString().trim());
                                userRef.child("eventStatus").setValue("Going");

                                DatabaseReference userPoints=FirebaseDatabase.getInstance().getReference("Users").child(user_id);
                                final String finalUser_id = user_id;
                                userPoints.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String userPoints=dataSnapshot.child("userPoints").getValue().toString();
                                        int pts=5;
                                        int userPts=Integer.parseInt(userPoints);
                                        userPts=userPts+pts;

                                        DatabaseReference addPts=FirebaseDatabase.getInstance().getReference("Users").child(finalUser_id).child("userPoints");
                                        addPts.setValue(String.valueOf(userPts));
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                Sneaker.with(SingleEventActivity.this)
                                        .setTitle("Gear on for "+snapshot.child("eventName").getValue(), R.color.white)
                                        .setMessage("Get Ready to participate in ", R.color.white)

                                        .setDuration(4000) // Time duration to show
                                        .autoHide(true) // Auto hide Sneaker view
                                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                        .setIcon(R.drawable.ic_directions_run, R.color.white, false)
                                         .setOnSneakerClickListener(new Sneaker.OnSneakerClickListener() {
                                             @Override
                                             public void onSneakerClick(View view) {

                                             }
                                         }) // Click listener for Sneaker
                                    .sneak(Color.parseColor("#f2003c")); // Sneak with background color

                                isInterested=false;

                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        btn_notInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.child("eventName").getValue().equals(txt_event_name.getText().toString().trim()) && isInterested==false)
                            {
//                                Get Snapshot value
                                Log.d(TAG, "onInterested: Success");
                                int count=1;
                                int attending=Integer.parseInt(snapshot.child("eventAttending").getValue().toString());


                                DatabaseReference eventAttending=snapshot.getRef().child("eventAttending");
                                eventAttending.setValue(String.valueOf(attending-1));

                                final String eventName=snapshot.child("eventName").getValue().toString();
                                String user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();

                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("userEvents");


                               ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            if(snapshot.child("eventName").getValue().equals(eventName))
                                            {
                                                Log.d(TAG, "Snapshot Deleted!");
                                                snapshot.getRef().setValue(null);
                                                ref.removeEventListener(this);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
//





                                Sneaker.with(SingleEventActivity.this)
                                        .setTitle("Try "+snapshot.child("eventName").getValue()+"next time!", R.color.white)
                                        .setMessage("You could always join the Event another time", R.color.white)
                                        .setDuration(4000) // Time duration to show
                                        .autoHide(true) // Auto hide Sneaker view
                                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                        .setIcon(R.drawable.ic_directions_run, R.color.white, false)
                                        .setOnSneakerClickListener(new Sneaker.OnSneakerClickListener() {
                                            @Override
                                            public void onSneakerClick(View view) {

                                            }
                                        }) // Click listener for Sneaker
                                        .sneak(android.R.color.black); // Sneak with background color

                                isInterested=true;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });


//        Get Intent
        Bundle eventData=getIntent().getExtras();
        String name=eventData.get("eventName").toString().trim();
        txt_event_name.setText(name);

        mDatabase= FirebaseDatabase.getInstance().getReference("Events");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    Log.d(TAG, "Event Name: "+snapshot.child("eventName").getValue());

                    if(snapshot.child("eventName").getValue().equals(txt_event_name.getText().toString().trim()))
                    {
                        Log.d(TAG, "onDataChange: Success");

                        txt_event_details.setText(snapshot.child("eventDesc").getValue().toString());
                        txt_event_fee.setText(snapshot.child("eventFee").getValue().toString());
                        txt_event_date.setText(convertDate(snapshot.child("eventDate").getValue().toString()));
                        txt_event_loc.setText(snapshot.child("eventLocation").getValue().toString());
                        txt_event_time.setText(snapshot.child("eventTime").getValue().toString());
                        txt_event_type.setText(snapshot.child("eventType").getValue().toString());


                        final Uri image=Uri.parse(snapshot.child("eventImage").getValue().toString());


                        Picasso
                                .with(SingleEventActivity.this)
                                .load(image)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .into(eventImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(SingleEventActivity.this).load(image).into(eventImage);
                                    }
                                });


                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    setBottomNav();


    }



    private void initViews() {
        txt_event_date=(TextView)findViewById(R.id.txt_event_date);
        txt_event_type=(TextView)findViewById(R.id.txt_event_type);
        txt_event_loc=(TextView)findViewById(R.id.txt_event_loc);
        txt_event_name=(TextView)findViewById(R.id.txt_event_name);
        txt_event_fee=(TextView)findViewById(R.id.txt_entry_fee);
        txt_event_time=(TextView)findViewById(R.id.txt_event_time);
        txt_event_details=(TextView)findViewById(R.id.txt_event_details);

        btn_interested=(Button)findViewById(R.id.btnInterested);
        btn_notInterested=(Button)findViewById(R.id.btnNotInterested);

    }

    private void setBottomNav()
    {
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
                        startActivity(new Intent(SingleEventActivity.this,EventsActivity.class));
                        break;


                    case R.id.ic_friends:
                        startActivity(new Intent(SingleEventActivity.this,FriendsActivity.class));

                        break;



                    case R.id.ic_stats:
                        startActivity(new Intent(SingleEventActivity.this,StatsActivity.class));

                        break;
                    case R.id.ic_run:
                        startActivity(new Intent(SingleEventActivity.this,MainActivity.class));

                        break;


                }
                return false;
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private String convertDate(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date firstDate = sdf.parse(date);
            SimpleDateFormat parseFormat = new SimpleDateFormat("E MMMM dd,yyyy");
            String eventDate=parseFormat.format(firstDate);

            date=eventDate;


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}
