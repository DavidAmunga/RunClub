package com.labs.tatu.runclub;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labs.tatu.runclub.helpers.BottomNavigationViewHelper;
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
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

public class InboxActivity extends AppCompatActivity {
    private static final String TAG = "InboxActivity";

    private FirebaseAuth mAuth;
    private Uri photo_url=null;
    private Toolbar toolbar;
    private boolean isLoggingOut=false;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        toolbar = (Toolbar) findViewById(R.id.friendsToolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        //Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(InboxActivity.this, "Something went wrong....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mAuth=FirebaseAuth.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(4);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.ic_events:
                        startActivity(new Intent(InboxActivity.this,EventsActivity.class));
                        break;


                    case R.id.ic_friends:
                        startActivity(new Intent(InboxActivity.this,FriendsActivity.class));

                        break;

                    case R.id.ic_inbox:
                        startActivity(new Intent(InboxActivity.this,InboxActivity.class));

                        break;

                    case R.id.ic_stats:
                        startActivity(new Intent(InboxActivity.this,StatsActivity.class));

                        break;
                    case R.id.ic_run:
                        startActivity(new Intent(InboxActivity.this,MainActivity.class));

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

    public void drawer() {
        final String name=mAuth.getCurrentUser().getDisplayName();
        final String email=mAuth.getCurrentUser().getEmail();
        String user_id=mAuth.getCurrentUser().getUid();


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("userPhotoUrl").exists())
                {

                    String url=dataSnapshot.child("userPhotoUrl").getValue().toString();
                    photo_url= Uri.parse(url);
                    Log.d(TAG, "Photo Url Exists "+photo_url);

                    drawerLogic(name,email);

                }
                else
                {
                    photo_url=mAuth.getCurrentUser().getPhotoUrl();
                    drawerLogic(name,email);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }
    public void drawerLogic(String name,String email)
    {
        //initialize and create the image loader logic
        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {

                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);


            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx) {
                return null;
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                return null;
            }
        });


        Log.d(TAG, "Foto Url "+photo_url);

        // Create a few sample profile
        final IProfile profile = new ProfileDrawerItem().withName(name)
                .withEmail(email)
                .withIcon(photo_url);

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile
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
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(1).withName("Add Awards").withTag("AddAward");
        PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(1).withName("Run Activity").withTag("RunActivity");
        SecondaryDrawerItem item7 = new SecondaryDrawerItem().withIdentifier(2).withName("Log Out").withTag("LogOut");

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
                        item6.withIcon(R.drawable.ic_directions_run_black_24dp),


                        new DividerDrawerItem(),
                        item7.withIcon(R.drawable.ic_log_out_black_24dp)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (drawerItem.getTag().toString()) {
                            case "AddLoc":
                                startActivity(new Intent(InboxActivity.this, AddLocationActivity.class));
                                break;
                            case "AddEvent":
                                startActivity(new Intent(InboxActivity.this, AddEventActivity.class));
                                break;
                            case "LogOut":
                                logOut();
                                break;
                            case "RunActivity":
                                startActivity(new Intent(InboxActivity.this,MyRunActivity.class));
                                break;
                            case "Profile":
                                startActivity(new Intent(InboxActivity.this,ProfileActivity.class));
                                break;
                            case "AddAward":
                                startActivity(new Intent(InboxActivity.this,AddAwardsActivity.class));
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

    private void logOut() {
        Log.d(TAG, "logOut: Is Logging out");
        if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("facebook.com")) {
            isLoggingOut = true;
            Log.d(TAG, "logOut: Facebook");
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(InboxActivity.this, LoginActivity.class));
            finish();


        } else if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("google.com")) {
            Log.d(TAG, "logOut: Google");
            FirebaseAuth.getInstance().signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            startActivity(new Intent(InboxActivity.this, LoginActivity.class));
            finish();

        }
        else
        {
            FirebaseAuth.getInstance().signOut();
        }
    }
}
