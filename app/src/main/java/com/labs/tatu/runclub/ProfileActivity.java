package com.labs.tatu.runclub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    private static final int GALLERY_REQUEST=1;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    private StorageReference mStorage;

    String phoneNo="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        initViews();

        mProgress=new ProgressDialog(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth=FirebaseAuth.getInstance();


        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

        mProgress.setMessage("Loading Profile");
        mProgress.setCancelable(false);
        mProgress.show();
        final String user_id=mAuth.getCurrentUser().getUid();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.child("userName").exists() && dataSnapshot.child("userPhotoUrl").exists())
               {
                   Log.d(TAG, "Details Available!");
                   txtName.setText(dataSnapshot.child("userName").getValue().toString());
                   final Uri photoUrl=Uri.parse(dataSnapshot.child("userPhotoUrl").getValue().toString());
                   if(dataSnapshot.child("userPhone").exists())
                   {
                       Log.d(TAG, "User Phone No "+dataSnapshot.child("userPhoneNo").getValue().toString());
                       txtPhoneNo.setText(dataSnapshot.child("userPhoneNo").getValue().toString());
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
                   if(dataSnapshot.child("userPhone").exists())
                   {
                       txtPhoneNo.setText(dataSnapshot.child("userPhoneNo").getValue().toString());
                   }


                   mProgress.dismiss();


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


                if(mImageUri!=null) {
                    Log.d(TAG, "onClick: Image URI not Null");


//                                        Log.d(TAG, "onSuccess: "+downloadUrl);
                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
                    ref.child("userName").setValue(name);
                    //  ref.child("userPhotoUrl").setValue(downloadUrl.toString());
                    ref.child("userBio").setValue(bio);
                    ref.child("userPhoneNo").setValue(phoneNo);
                    Toast.makeText(ProfileActivity.this, "Details Saved", Toast.LENGTH_SHORT).show();



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

        mStorage= FirebaseStorage.getInstance().getReference();
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
