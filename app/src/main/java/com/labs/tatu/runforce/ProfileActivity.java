package com.labs.tatu.runforce;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    CircleImageView user_image;
    Button btnSave;
    TextView txtEmail,txtName;
    EditText txtPhoneNo,txtBio,txtUserName;
    private Toolbar toolbar;

    private Uri mImageUri=null;


    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;



    String email;

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
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        initViews();

        email=getIntent().getExtras().get("userEmail").toString();

        mProgress=new ProgressDialog(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth=FirebaseAuth.getInstance();




        mProgress.setMessage("Loading Profile");
        mProgress.setCancelable(false);
        mProgress.show();



        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        Log.d(TAG, "Email "+email);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Snap "+snapshot.child("userEmail").getValue());
                    if (snapshot.child("userEmail").getValue().equals(email))
                    {
                        if(!FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("facebook.com") && !FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("google.com"))
                        {
                            Log.d(TAG, "Details Available!");
                            Log.d(TAG, "DataSnapshot "+dataSnapshot.getValue());
                            txtName.setText(snapshot.child("userName").getValue().toString());
                            txtBio.setText(snapshot.child("userBio").getValue().toString());
                            final Uri photoUrl=Uri.parse(snapshot.child("userPhotoUrl").getValue().toString());
                            if(snapshot.child("userPhoneNo").exists())
                            {
                                Log.d(TAG, "User Phone No "+snapshot.child("userPhoneNo").getValue().toString());
                                txtPhoneNo.setText(snapshot.child("userPhoneNo").getValue().toString());
                            }




                            Picasso
                                    .with(ProfileActivity.this)
                                    .load(photoUrl)
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(user_image, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(ProfileActivity.this).load(photoUrl).into(user_image);
                                        }
                                    });
                            mProgress.dismiss();

                        }
                        else
                        {
                            txtName.setText(mAuth.getCurrentUser().getDisplayName());
                            txtName.setEnabled(false);
                            txtBio.setText(snapshot.child("userBio").getValue().toString());
                            Picasso
                                    .with(ProfileActivity.this)
                                    .load(mAuth.getCurrentUser().getPhotoUrl())
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(user_image, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(ProfileActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).into(user_image);
                                        }
                                    });
                            if(dataSnapshot.child("userPhoneNo").exists())
                            {
                                txtPhoneNo.setText(snapshot.child("userPhoneNo").getValue().toString());
                            }


                            mProgress.dismiss();


                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgress.dismiss();
            }
        });


        txtEmail.setText(mAuth.getCurrentUser().getEmail());


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String name=txtUserName.getText().toString().trim();
                final String bio=txtBio.getText().toString().trim();
                final String phoneNo=txtPhoneNo.getText().toString().trim();


                if(!FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("facebook.com") && !FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("google.com")) {
                    Log.d(TAG, "onClick: Image URI not Null");

                    final DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users");
                    refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.child("userEmail").getValue().equals(email)) {
                                    Log.d(TAG, "onDataChange: User Found!");
                                    String userID = snapshot.getKey();
                                    Log.d(TAG, "Key: " + userID);


                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                                    ref.child("userName").setValue(name);
                                    ref.child("userBio").setValue(bio);
                                    ref.child("userPhoneNo").setValue(phoneNo);

                                    Toast.makeText(ProfileActivity.this, "Details Saved", Toast.LENGTH_SHORT).show();




                                }

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });




                }
                else
                {
                    user_image.setImageURI(mAuth.getCurrentUser().getPhotoUrl());

                    String user_id=mAuth.getCurrentUser().getUid();
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(user_id);
                    ref.child("userName").setValue(name);
                    ref.child("userBio").setValue(bio);
                    ref.child("userPhoneNo").setValue(phoneNo);


                    Toast.makeText(ProfileActivity.this, "Details Saved", Toast.LENGTH_SHORT).show();

                }



            }
        });


    }

    private void initViews() {
        txtPhoneNo=(EditText)findViewById(R.id.txt_phoneNo);
        txtName=(TextView)findViewById(R.id.txtUserName);
        txtEmail=(TextView)findViewById(R.id.txtUserEmail);
        txtBio=(EditText) findViewById(R.id.txt_bio);
        txtUserName=(EditText)findViewById(R.id.txtUserName);
        btnSave=(Button)findViewById(R.id.btnSave);
        user_image=(CircleImageView)findViewById(R.id.txtUserImage);


        mProgress=new ProgressDialog(this);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
//        {
//            mImageUri=data.getData();
//            user_image.setImageURI(mImageUri);
//        }
//    }
}
