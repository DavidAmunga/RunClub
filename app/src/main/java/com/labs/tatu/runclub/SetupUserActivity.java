package com.labs.tatu.runclub;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupUserActivity extends AppCompatActivity {
    private static final String TAG = "SetupUserActivity";

    private TextView txtName;
    private EditText txtBio;
    private CircleImageView userImage;
    private Button save;

    private Uri mImageUri=null;

    FirebaseAuth mAuth;
    StorageReference mStorage;

    private static final int GALLERY_REQUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_user);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth=FirebaseAuth.getInstance();
        mStorage=FirebaseStorage.getInstance().getReference();
        initViews();



        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle name=getIntent().getExtras();

                final String userName=name.get("userName").toString();
                final String userBio=txtBio.getText().toString();

                Log.d(TAG, "Name: " + name);
                if (!TextUtils.isEmpty(userName)) {
//                                            Get User
                    final DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users");
                    refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                if (snapshot.child("userName").getValue().equals(userName)) {
                                    Log.d(TAG, "onDataChange: User Found!");
                                    final String userID = snapshot.getKey();
                                    Log.d(TAG, "Key: " + userID);

                                    StorageReference filepath=mStorage.child("User_Images").child(userID).child(mImageUri.getLastPathSegment());

                                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            Uri downloadUrl=taskSnapshot.getDownloadUrl();

                                            DatabaseReference refID = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                            refID.child("userBio").setValue(userBio);
                                            refID.child("userPhotoUrl").setValue(downloadUrl.toString());
                                            startActivity(new Intent(SetupUserActivity.this,MainActivity.class));


                                        }
                                    });


                                }

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    Toast.makeText(SetupUserActivity.this, "Enter Challenge Name!", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void initViews() {
        txtBio=(EditText)findViewById(R.id.txt_bio);
        txtName=(TextView) findViewById(R.id.textView4);
        userImage=(CircleImageView)findViewById(R.id.imgUser);
        save=(Button)findViewById(R.id.btnSave);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {
            mImageUri=data.getData();
            userImage.setImageURI(mImageUri);
        }
    }
}
