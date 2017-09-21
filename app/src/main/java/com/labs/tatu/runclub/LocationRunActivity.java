package com.labs.tatu.runclub;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.labs.tatu.runclub.helpers.BottomNavigationViewHelper;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationRunActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {

    //Our Map
    private GoogleMap mMap;

    //To store longitude and latitude from map
    private double longitude;
    private double latitude;

    //From -> the first coordinate from where we need to calculate the distance
    private double fromLongitude;
    private double fromLatitude;

    //To -> the second coordinate to where we need to calculate the distance
    private double toLongitude;
    private double toLatitude;

    //Google ApiClient
    private GoogleApiClient googleApiClient;

    private static final String TAG = "LocationRunActivity";

    private static final int LOCATION_REQUEST = 1;

    TextView txtTitle, txtDistance,txtGoal;
    Toolbar toolbar;
    Button btn_solo_run, btn_challenge;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_run);

        toolbar = (Toolbar) findViewById(R.id.toolbar);



        //        Set No Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//


        initViews();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.loc_map);
        mapFragment.getMapAsync(this);

        //Initializing googleapi client
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        final Bundle locData = getIntent().getExtras();
        final String locName = locData.get("locName").toString();
        String locDistance = locData.get("locDistance").toString();
        String locGoal=locData.get("locGoal").toString();


        Log.d(TAG, "fromLat: " + locData.get("fromLat"));
        Log.d(TAG, "fromLong: " + fromLatitude);
        Log.d(TAG, "toLat: " + toLatitude);
        Log.d(TAG, "toLong: " + toLongitude);

        txtTitle.setText(locName);
        txtGoal.setText(locGoal);
        if (Integer.parseInt(locDistance) > 1000) {
            Double locDist = Double.parseDouble(locDistance);
            locDist = locDist / 1000;

            txtDistance.setText(String.valueOf(locDist) + " km");
        } else {
            txtDistance.setText(locDistance + " m");
        }


        mDatabase = FirebaseDatabase.getInstance().getReference("Locations");


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    Log.d(TAG, "Text Title: " + txtTitle.getText().toString().trim());

                    if (snapshot.child("locationName").getValue().equals(txtTitle.getText().toString().trim())) {
                        Log.d(TAG, "onDataChange: Success");

                        fromLatitude = Double.valueOf(String.valueOf(snapshot.child("fromLat").getValue()));
                        fromLongitude = Double.valueOf(String.valueOf(snapshot.child("fromLong").getValue()));
                        toLatitude = Double.valueOf(String.valueOf(snapshot.child("toLat").getValue()));
                        toLongitude = Double.valueOf(String.valueOf(snapshot.child("toLong").getValue()));

                        Log.d(TAG, "onDataChange: fromLat" + fromLatitude);
                        Log.d(TAG, "onDataChange: fromLong" + toLongitude);
                        Log.d(TAG, "onDataChange: toLat" + toLatitude);
                        Log.d(TAG, "onDataChange: toLong" + toLongitude);


                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btn_solo_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(new Intent(LocationRunActivity.this, WorkoutActivity.class));
                intent.putExtra("locName",txtTitle.getText().toString().trim());
                intent.putExtra("locGoal",txtTitle.getText().toString().trim());
                intent.putExtra("fromLat",fromLatitude);
                intent.putExtra("fromLong",fromLongitude);
                intent.putExtra("toLat",toLatitude);
                intent.putExtra("toLong",toLongitude);

                startActivity(intent);
            }
        });
        btn_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
                        startActivity(new Intent(LocationRunActivity.this, EventsActivity.class));
                        break;


                    case R.id.ic_friends:
                        startActivity(new Intent(LocationRunActivity.this, FriendsActivity.class));

                        break;

                    case R.id.ic_inbox:
                        startActivity(new Intent(LocationRunActivity.this, InboxActivity.class));

                        break;

                    case R.id.ic_stats:
                        startActivity(new Intent(LocationRunActivity.this, StatsActivity.class));

                        break;
                    case R.id.ic_run:
                        startActivity(new Intent(LocationRunActivity.this, MainActivity.class));

                        break;


                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();

    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();

    }

    //Getting current location
    private void getCurrentLocation() {
        mMap.clear();
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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
        LatLng latLng = new LatLng(fromLatitude, fromLongitude);

        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Current Location")); //Adding a title

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void getDirection() {
        //Getting the URL
        String url = makeURL(fromLatitude, fromLongitude, toLatitude, toLongitude);
        Log.d(TAG, "getDirection: " + url);

        //Showing a dialog till we get the route
        final ProgressDialog loading = ProgressDialog.show(this, "Getting Route", "Please wait...", false, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        //Calling the method drawPath to draw the path
                        drawPath(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                    }
                });

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(sourcelat);
        urlString.append(",");
        urlString
                .append(sourcelog);
        urlString.append("&destination=");// to
        urlString
                .append(destlat);
        urlString.append(",");
        urlString.append(destlog);
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyB3fXKLvCLoS0dxCfR6ti1Ic7QgrViFl28");
        return urlString.toString();
    }


    //The parameter is the server response
    public void drawPath(String result) {
        //Getting both the coordinates
        LatLng from = new LatLng(fromLatitude, fromLongitude);
        LatLng to = new LatLng(toLatitude, toLongitude);

        //Calculating the distance in meters
        final Double distance = SphericalUtil.computeDistanceBetween(from, to);

        //Displaying the distance
        //   Toast.makeText(this, String.valueOf(distance + " Meters"), Toast.LENGTH_SHORT).show();


        try {
            //Parsing json
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(20)
                    .color(Color.RED)
                    .geodesic(true)
            );


        } catch (JSONException e) {

        }


    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);

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
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST);


            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(Bundle bundle) {

        getCurrentLocation();

        getDirection();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }


    private void initViews() {
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtDistance = (TextView) findViewById(R.id.txt_distance);
        txtGoal = (TextView) findViewById(R.id.txt_goal);
        btn_challenge = (Button) findViewById(R.id.btn_challenge_friend);
        btn_solo_run = (Button) findViewById(R.id.btn_solo_run);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }


}


