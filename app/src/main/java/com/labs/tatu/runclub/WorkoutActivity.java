package com.labs.tatu.runclub;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.labs.tatu.runclub.helpers.BottomNavigationViewHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;
import com.xw.repo.BubbleSeekBar;

import static com.labs.tatu.runclub.R.id.loc_image;

public class WorkoutActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String TAG = "WorkoutActivity";

    private TextView textView, txt_loc_name;
    private Toolbar toolbar;
    private Button start, pause, reset;
    private ImageView locHeader;
    private MediaPlayer mp;
    private GoogleMap mMap;


    //To store longitude and latitude from map
    private double longitude;
    private double latitude;

    //From -> the first coordinate from where we need to calculate the distance
    private double fromLongitude=0;
    private double fromLatitude=0;

    //To -> the second coordinate to where we need to calculate the distance
    private double toLongitude=0;
    private double toLatitude=0;

    //    To -> Store the LatLng To and From


    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;

    Handler handler;

    int Seconds, Minutes, MilliSeconds, Hours;

    String locGoal = "";
    public boolean isStart;
    FirebaseAuth mAuth;

    private GoogleApiClient googleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private static final int LOCATION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
//     Set Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//
        //Initializing googleapi client
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


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
        locHeader=(ImageView)findViewById(R.id.locHeader);



        isStart = true;


        handler = new Handler();

        if (getIntent().getExtras() != null) {
            Bundle locData = getIntent().getExtras();
            txt_loc_name.setText(locData.get("locName").toString().trim());
            locGoal = locData.get("locGoal").toString().trim();
            fromLatitude =Double.valueOf(locData.get("fromLat").toString().trim());
            fromLongitude =Double.valueOf(locData.get("fromLong").toString().trim());
            toLatitude =Double.valueOf(locData.get("toLat").toString().trim());
            toLongitude =Double.valueOf(locData.get("toLong").toString().trim());


            Log.d(TAG, "fromLat "+locData.get("fromLat").toString().trim());
            Log.d(TAG, "fromLong "+Double.valueOf(fromLongitude));
            Log.d(TAG, "toLat "+  locData.get("toLat").toString());
            Log.d(TAG, "toLong "+Double.valueOf(toLongitude));


            LatLng fromLatLng = new LatLng(fromLatitude, fromLongitude);


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Locations");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("locationName").getValue().equals(txt_loc_name.getText().toString().trim())) {
                            Log.d(TAG, "onDataChange: Success");

                            final String locationPhotoUrl = snapshot.child("locationPhotoUrl").getValue().toString();

                            Picasso
                                    .with(WorkoutActivity.this)
                                    .load(locationPhotoUrl)
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(locHeader, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(WorkoutActivity.this).load(locationPhotoUrl).into(locHeader);
                                        }
                                    });


                        } else {
                            Log.d(TAG, "No Location");
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        //    setMarkers(fromLatitude,fromLongitude,toLatitude,toLongitude);
        }





        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
                mp=MediaPlayer.create(WorkoutActivity.this, R.raw.start);
                mp.start();
                reset.setEnabled(false);
                if (isStart = true) {
                    if (locGoal.equals("")) {
                        Alerter.create(WorkoutActivity.this)
                                .setTitle("First Start")
                                .setText("See through the Infinity workout")
                                .setDuration(2000)
                                .setIcon(R.drawable.ic_directions_run)
                                .setBackgroundColor(android.R.color.black)
                                .show();
                    } else {
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Locations");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.child("locationName").getValue().equals(txt_loc_name.getText().toString().trim())) {
                                        Log.d(TAG, "onDataChange: Success");
                                        String locationName = snapshot.child("locationName").getValue().toString();
                                        int locationDistance = Integer.parseInt(snapshot.child("locationDistance").getValue().toString());
                                        String locationPhotoUrl = snapshot.child("locationPhotoUrl").getValue().toString();



                                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("userLocations").push();
                                        ref.child("locationName").setValue(locationName);
                                        ref.child("locationDistance").setValue(locationDistance);
                                        ref.child("locationPhotoUrl").setValue(locationPhotoUrl);


//


//                                  Add Points for Working out on Activity
                                        DatabaseReference userPoints = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
                                        final String finalUser_id = user_id;
                                        userPoints.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String userPoints = dataSnapshot.child("userPoints").getValue().toString();
                                                int pts = 10;
                                                int userPts = Integer.parseInt(userPoints);
                                                userPts = userPts + pts;

                                                DatabaseReference addPts = FirebaseDatabase.getInstance().getReference("Users").child(finalUser_id).child("userPoints");
                                                addPts.setValue(String.valueOf(userPts));
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    } else {
                                        Log.d(TAG, "No Location");
                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        Alerter.create(WorkoutActivity.this)
                                .setTitle(txt_loc_name.getText().toString() + " Challenge")
                                .setText("See through the whole distance")
                                .setDuration(2000)
                                .setIcon(R.drawable.ic_directions_run)
                                .setBackgroundColor(android.R.color.black)
                                .show();
                    }


                    isStart = false;
                }


            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp=MediaPlayer.create(WorkoutActivity.this, R.raw.stop);
                mp.start();
                TimeBuff += MillisecondTime;

                handler.removeCallbacks(runnable);

                reset.setEnabled(true);
                reset.setText("RESET");

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp=MediaPlayer.create(WorkoutActivity.this, R.raw.stop);
                mp.start();

                MillisecondTime = 0L;
                StartTime = 0L;
                TimeBuff = 0L;
                UpdateTime = 0L;
                Seconds = 0;
                Minutes = 0;
                MilliSeconds = 0;
                Hours = 0;

                textView.setText("00:00:00");


            }
        });

        setBottomNav();

    }

    private void setMarkers(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {
        LatLng fromLatLng = new LatLng(fromLatitude, fromLongitude);
        mMap.addMarker(new MarkerOptions().position(fromLatLng).draggable(true).title("From Here"));


        LatLng toLatLng = new LatLng(toLatitude, toLongitude);
        mMap.addMarker(new MarkerOptions().position(toLatLng).draggable(true).title("To Here"));


    }


    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);
            Hours = Minutes / 60;
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);


            textView.setText(String.format("%02d", Hours) + ":" + String.format("%02d", Minutes) + ":"
                    + String.format("%02d", Seconds));

            handler.postDelayed(this, 0);
        }

    };


    private void setBottomNav() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_events:
                        startActivity(new Intent(WorkoutActivity.this, EventsActivity.class));
                        break;


                    case R.id.ic_friends:
                        startActivity(new Intent(WorkoutActivity.this, FriendsActivity.class));

                        break;

                    case R.id.ic_inbox:
                        startActivity(new Intent(WorkoutActivity.this, InboxActivity.class));

                        break;

                    case R.id.ic_stats:
                        startActivity(new Intent(WorkoutActivity.this, StatsActivity.class));

                        break;
                    case R.id.ic_run:
                        startActivity(new Intent(WorkoutActivity.this, MainActivity.class));

                        break;


                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        TimeBuff += MillisecondTime;

        handler.removeCallbacks(runnable);

        reset.setEnabled(true);
        reset.setText("RESET");


    }

    @Override
    protected void onStop() {
        super.onStop();

        googleApiClient.disconnect();
        super.onStop();

        MillisecondTime = 0L;
        StartTime = 0L;
        TimeBuff = 0L;
        UpdateTime = 0L;
        Seconds = 0;
        Minutes = 0;
        MilliSeconds = 0;
        Hours = 0;

        textView.setText("00:00:00");
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();

    }

    //Getting current location
    private void getCurrentLocation() {
        mMap.clear();
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST);

            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();
        }
    }


    //Function to move the map
    private void moveMap() {
        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);


        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(-34, 151);
        LatLng from=new LatLng(fromLatitude,fromLongitude);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Me"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST);


            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onLocationChanged(Location location) {
        if (getApplicationContext() != null) {

            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            Log.e("err", mLastLocation.toString());
            //Calculating the distance in meters
            LatLng toLatLng = new LatLng(toLatitude, toLongitude);
            LatLng fromLatLng = new LatLng(fromLatitude, fromLongitude);
            Double distance = SphericalUtil.computeDistanceBetween(latLng, toLatLng);
            Double totalDistance = SphericalUtil.computeDistanceBetween(fromLatLng, toLatLng);
            Log.d(TAG, "toLatLng " + toLatLng);


            Log.d(TAG, "Location Total Distance "+totalDistance);
            Log.d(TAG, "Location  Distance "+distance);



        }

    }

    public void getMarkers() {





    }
}
