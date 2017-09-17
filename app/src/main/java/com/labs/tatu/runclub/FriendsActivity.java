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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.appinvite.AppInviteInvitation;
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
import com.labs.tatu.runclub.model.MyRunActivity;
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

public class FriendsActivity extends AppCompatActivity {
    private static final String TAG = "FriendsActivity";
    private static final int REQUEST_INVITE=1;
    private Toolbar toolbar;

    FirebaseAuth mAuth;
    private boolean isLoggingOut = false;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        toolbar = (Toolbar) findViewById(R.id.friendsToolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

//        Set No Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//

        mAuth=FirebaseAuth.getInstance();
        //        Set Google Log In

        //Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(FriendsActivity.this, "Something went wrong....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        TextView invite = (TextView) findViewById(R.id.invite);

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                        .setCustomImage(Uri.parse("https://drive.google.com/open?id=0B7ppRoKalXOdUWxCWHRLTmdwaUk"))
                        .setCallToActionText(getString(R.string.invitation_cta))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);
            }
        });



        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_events:
                        startActivity(new Intent(FriendsActivity.this, EventsActivity.class));
                        break;


                    case R.id.ic_friends:
                        startActivity(new Intent(FriendsActivity.this, FriendsActivity.class));

                        break;

                    case R.id.ic_inbox:
                        startActivity(new Intent(FriendsActivity.this, InboxActivity.class));

                        break;

                    case R.id.ic_stats:
                        startActivity(new Intent(FriendsActivity.this, StatsActivity.class));

                        break;
                    case R.id.ic_run:
                        startActivity(new Intent(FriendsActivity.this, MainActivity.class));

                        break;


                }
                return false;
            }
        });

        drawer();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void drawer() {
        String name=mAuth.getCurrentUser().getDisplayName();
        String email=mAuth.getCurrentUser().getEmail();
        String user_id=mAuth.getCurrentUser().getUid();
        Uri photo_url=mAuth.getCurrentUser().getPhotoUrl();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("photo_url").exists())
                {
                    String url=dataSnapshot.child("photo_url").getValue().toString();
                    Uri photo_url=Uri.parse(url);
                    Log.d(TAG, "Photo Url Exists "+photo_url.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





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


        Log.d(TAG, "Foto Url"+photo_url);

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
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(1).withName("Run Activity").withTag("RunActivity");
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
                        item5.withIcon(R.drawable.ic_directions_run_black_24dp),


                        new DividerDrawerItem(),
                        item6.withIcon(R.drawable.ic_log_out_black_24dp)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (drawerItem.getTag().toString()) {
                            case "AddLoc":
                                startActivity(new Intent(FriendsActivity.this, AddLocationActivity.class));
                                break;
                            case "AddEvent":

                                startActivity(new Intent(FriendsActivity.this, LoginActivity.class));
                                break;
                            case "LogOut":
                                logOut();
                                break;
                            case "RunActivity":
                                startActivity(new Intent(FriendsActivity.this,MyRunActivity.class));
                                break;
                            case "Profile":
                                startActivity(new Intent(FriendsActivity.this,ProfileActivity.class));
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
            startActivity(new Intent(FriendsActivity.this, LoginActivity.class));
            finish();


        } else if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("google.com")) {
            Log.d(TAG, "logOut: Google");
            FirebaseAuth.getInstance().signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            startActivity(new Intent(FriendsActivity.this, LoginActivity.class));
            finish();


        }
    }
}
