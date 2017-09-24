package com.labs.tatu.runclub;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.labs.tatu.runclub.fragments.ChallengesFragment;
import com.labs.tatu.runclub.helpers.BottomNavigationViewHelper;
import com.labs.tatu.runclub.model.Challenge;
import com.labs.tatu.runclub.model.User;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

public class FriendsActivity extends AppCompatActivity {
    private static final String TAG = "FriendsActivity";
    private static final int REQUEST_INVITE = 1;
    private Toolbar toolbar;

    FirebaseAuth mAuth;
    private boolean isLoggingOut = false;
    private Uri photo_url = null;
    private GoogleApiClient mGoogleApiClient;




    private RecyclerView mUserList;

    private DatabaseReference mDatabase;

    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        toolbar = (Toolbar) findViewById(R.id.friendsToolbar);

        setSupportActionBar(toolbar);

        initViews();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mUserList = (RecyclerView) findViewById(R.id.user_lists);
        //mChallengeList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

//        Set No Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//

        mAuth = FirebaseAuth.getInstance();
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

    public boolean isFbLoggedIn() {

        if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("facebook.com")) {
            Profile profile = Profile.getCurrentProfile();
            profile.getFirstName();
            id = profile.getId();

            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.user_row,
                UserViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(final UserViewHolder viewHolder, final User model, final int position) {
                viewHolder.setUserName(model.getUserName());
                viewHolder.setUserImage(FriendsActivity.this, model.getUserPhotoUrl());

                viewHolder.itemView.findViewById(R.id.btn_challenge_user).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(model, position);
                    }
                });

            }


        };

        mUserList.setAdapter(firebaseRecyclerAdapter);

    }

    private void showDialog(final User model, final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.challenge_item, null);
        dialogBuilder.setView(dialogView);

        final EditText challengeName = (EditText) dialogView.findViewById(R.id.txt_challengeName);
        Button btnAdd = (Button) dialogView.findViewById(R.id.buttonChallenge);

        Log.d(TAG, "onClick: " + challengeName.getText().toString());


        dialogBuilder.setTitle("Challenge " + model.getUserName());
        dialogBuilder.setMessage("Challenge Name");
        final AlertDialog b = dialogBuilder.create();
        b.show();


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = challengeName.getText().toString();
                Log.d(TAG, "Name: " + name);
                if (!TextUtils.isEmpty(name)) {
//                                            Get User
                    final DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users");
                    refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                if (snapshot.child("userName").getValue().equals(model.getUserName().trim())) {
                                    Log.d(TAG, "onDataChange: User Found!");
                                    String userID = snapshot.getKey();
                                    Log.d(TAG, "Key: " + userID);

                                    DatabaseReference newChallenge = refUser.child(userID).child("userChallenges").push();
                                    newChallenge.child("challengeName").setValue(name);
                                    newChallenge.child("challengePoints").setValue("10");
                                    newChallenge.child("challengeImage").setValue("R.drawable.challengeHeader");
                                    newChallenge.child("challenger").setValue(mAuth.getCurrentUser().getDisplayName());

                                    Toast.makeText(FriendsActivity.this, model.getUserName() + " challenged", Toast.LENGTH_SHORT).show();
                                    b.dismiss();
                                }

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    Toast.makeText(FriendsActivity.this, "Enter Challenge Name!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        Button btnChallenge;


        public UserViewHolder(View itemView) {
            super(itemView);

            btnChallenge = (Button) itemView.findViewById(R.id.btn_challenge_user);
        }

        public void setUserName(String name) {
            TextView userName = (TextView) itemView.findViewById(R.id.txtUserName);
            userName.setText(name);
        }

        public void setUserImage(final Context ctx, final String image) {
            final ImageView user_image = (ImageView) itemView.findViewById(R.id.userImage);


            Picasso
                    .with(ctx)
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(user_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(image).into(user_image);
                        }
                    });


        }
    }


    private void initViews() {
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
        final String name = mAuth.getCurrentUser().getDisplayName();
        final String email = mAuth.getCurrentUser().getEmail();
        String user_id = mAuth.getCurrentUser().getUid();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("userPhotoUrl").exists()) {

                    String url = dataSnapshot.child("userPhotoUrl").getValue().toString();
                    photo_url = Uri.parse(url);
                    Log.d(TAG, "Photo Url Exists " + photo_url);

                    drawerLogic(name, email);

                } else {
                    photo_url = mAuth.getCurrentUser().getPhotoUrl();
                    drawerLogic(name, email);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void drawerLogic(String name, String email) {
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


        Log.d(TAG, "Foto Url " + photo_url);

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
        PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(1).withName("Add Challenge").withTag("AddChallenge");

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
                                startActivity(new Intent(FriendsActivity.this, AddLocationActivity.class));
                                break;
                            case "AddChallenge":
                                startActivity(new Intent(FriendsActivity.this, AddChallengeActivity.class));
                                break;
                            case "AddEvent":
                                startActivity(new Intent(FriendsActivity.this, AddEventActivity.class));
                                break;
                            case "LogOut":
                                logOut();
                                break;
                            case "RunActivity":
                                startActivity(new Intent(FriendsActivity.this, MyRunActivity.class));
                                break;
                            case "Profile":
                                startActivity(new Intent(FriendsActivity.this, ProfileActivity.class));
                                break;
                            case "AddAward":
                                startActivity(new Intent(FriendsActivity.this, AddAwardsActivity.class));
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

        } else {
            FirebaseAuth.getInstance().signOut();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.friends_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_share:
                if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("google.com")) {
                    Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                            .setMessage(getString(R.string.invitation_message))
                            .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                            .setCustomImage(Uri.parse("https://drive.google.com/open?id=0B7ppRoKalXOdUWxCWHRLTmdwaUk"))
                            .setCallToActionText(getString(R.string.invitation_cta))
                            .build();
                    startActivityForResult(intent, REQUEST_INVITE);
                } else if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("facebook.com")) {

                    String appLinkUrl, previewImageUrl;

                    appLinkUrl = "https://www.runclub.com";
                    previewImageUrl = "https://drive.google.com/open?id=0B7ppRoKalXOdUWxCWHRLTmdwaUk";

                    if (AppInviteDialog.canShow()) {
                        AppInviteContent content = new AppInviteContent.Builder()
                                .setApplinkUrl(appLinkUrl)
                                .setPreviewImageUrl(previewImageUrl)
                                .build();
                        AppInviteDialog.show(FriendsActivity.this, content);
                    }
                } else {

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
