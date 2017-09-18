package com.labs.tatu.runclub;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labs.tatu.runclub.helpers.BottomNavigationViewHelper;
import com.tapadoo.alerter.Alerter;

public class WorkoutActivity extends AppCompatActivity {
    private static final String TAG = "WorkoutActivity";

    TextView textView,txt_loc_name;

    private Button start, pause, reset;

    private Toolbar toolbar;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;

    Handler handler;

    int Seconds, Minutes, MilliSeconds,Hours;

    String locGoal="";
    public boolean isStart;
    FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        mAuth=FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //        Set No Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//

        textView = (TextView) findViewById(R.id.textView);
        start = (Button) findViewById(R.id.btnStart);
        pause = (Button) findViewById(R.id.buttonPause);
        reset = (Button) findViewById(R.id.buttonReset);
        txt_loc_name = (TextView) findViewById(R.id.txt_loc_name);


        isStart=true;


        handler = new Handler();

        if(getIntent().getExtras()!=null)
        {
            Bundle locData=getIntent().getExtras();
            txt_loc_name.setText(locData.get("locName").toString().trim());

            locGoal=locData.get("locGoal").toString().trim();
        }



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);

                reset.setEnabled(false);
                if(isStart=true)
                {
                    if(locGoal.equals(""))
                    {
                        Alerter.create(WorkoutActivity.this)
                                .setTitle("First Start")
                                .setText("See through the Infinity workout")
                                .setDuration(2000)
                                .setIcon(R.drawable.ic_directions_run)
                                .setBackgroundColor(android.R.color.black)
                                .show();
                    }
                    else
                    {
                        String user_id=mAuth.getCurrentUser().getUid();
                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Locations");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.child("locationName").getValue().equals(txt_loc_name.getText().toString().trim())) {
                                    Log.d(TAG, "onDataChange: Success");
                                    String locationName=snapshot.child("locationName").getValue().toString();
                                    int locationDistance=Integer.parseInt(snapshot.child("locationDistance").getValue().toString());
                                    String locationPhotoUrl=snapshot.child("locationPhotoUrl").getValue().toString();

                                    String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("userLocations");
                                    ref.child("locationName").setValue(locationName);
                                    ref.child("locationDistance").setValue(locationDistance);
                                    ref.child("locationPhotoUrl").setValue(locationPhotoUrl);



                                }


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





                        Alerter.create(WorkoutActivity.this)
                                .setTitle(txt_loc_name.getText().toString() + "Challenge")
                                .setText("See through the whole distance")
                                .setDuration(2000)
                                .setIcon(R.drawable.ic_directions_run)
                                .setBackgroundColor(android.R.color.black)
                                .show();
                    }



                    isStart=false;
                }



            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeBuff += MillisecondTime;

                handler.removeCallbacks(runnable);

                reset.setEnabled(true);
                reset.setText("RESET");

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MillisecondTime = 0L;
                StartTime = 0L;
                TimeBuff = 0L;
                UpdateTime = 0L;
                Seconds = 0;
                Minutes = 0;
                MilliSeconds = 0;
                Hours=0;

                textView.setText("00:00:00");


            }
        });

        setBottomNav();

    }



    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);
            Hours=Minutes/60;
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);


            textView.setText(String.format("%02d",Hours)+ ":" + String.format("%02d",Minutes) + ":"
                    + String.format("%02d", Seconds));

            handler.postDelayed(this, 0);
        }

    };



    private void setBottomNav()
    {
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
                        startActivity(new Intent(WorkoutActivity.this,EventsActivity.class));
                        break;


                    case R.id.ic_friends:
                        startActivity(new Intent(WorkoutActivity.this,FriendsActivity.class));

                        break;

                    case R.id.ic_inbox:
                        startActivity(new Intent(WorkoutActivity.this,InboxActivity.class));

                        break;

                    case R.id.ic_stats:
                        startActivity(new Intent(WorkoutActivity.this,StatsActivity.class));

                        break;
                    case R.id.ic_run:
                        startActivity(new Intent(WorkoutActivity.this,MainActivity.class));

                        break;


                }
                return false;
            }
        });
    }
}
