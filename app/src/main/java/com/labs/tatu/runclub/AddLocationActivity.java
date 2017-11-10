package com.labs.tatu.runclub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddLocationActivity extends AppCompatActivity {
    private static final String TAG = "AddLocationActivity";

    private String fromLat,fromLong,toLat,toLong,distance="0";

    private Button plot_loc,btn_add_loc;

    private TextView add_loc_distance,start_loc,end_loc;

    private EditText add_loc_name,add_loc_goal;

    private ImageButton add_loc_image;
    private Uri mImageUri=null;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    private static final int GALLERY_REQUEST=1;


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
        setContentView(R.layout.activity_add_location);

        plot_loc=(Button)findViewById(R.id.plot_loc);
        btn_add_loc=(Button)findViewById(R.id.btn_add_loc);
        add_loc_distance=(TextView)findViewById(R.id.add_loc_distance);
        start_loc=(TextView)findViewById(R.id.start_loc);
        end_loc=(TextView)findViewById(R.id.end_loc);
        add_loc_name=(EditText) findViewById(R.id.add_loc_name);
        add_loc_goal=(EditText) findViewById(R.id.add_loc_goal);
        add_loc_image=(ImageButton)findViewById(R.id.add_loc_image);
        mProgress=new ProgressDialog(this);

        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Locations");


        add_loc_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });


        plot_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddLocationActivity.this,MapsActivity.class));
            }
        });

        btn_add_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_location();
            }
        });


        if(getIntent().getExtras()!=null)
        {
            Bundle locData= getIntent().getExtras();
            String distance=locData.get("LocDistance").toString();
            String fromLat=locData.get("fromLat").toString();
            String fromLong=locData.get("fromLong").toString();
            String toLat=locData.get("toLat").toString();
            String toLong=locData.get("toLong").toString();

            add_loc_distance.setText(distance+" m");
            start_loc.setText(fromLat+" "+fromLong);
            end_loc.setText(toLat+" "+toLong);
        }

    }

    private void add_location() {
        mProgress.setMessage("Adding Location");
        mProgress.setCancelable(false);
        mProgress.show();
        final String name=add_loc_name.getText().toString().trim();
        final String goal=add_loc_goal.getText().toString().trim();
        final String distance=add_loc_distance.getText().toString().trim();

        if(!TextUtils.isEmpty(name) &&  !TextUtils.isEmpty(goal))
        {
            StorageReference filepath=mStorage.child("Loc_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTesting") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "onSuccess: "+downloadUrl);
                    DatabaseReference newLoc=mDatabase.push();
                    newLoc.child("locationName").setValue(name);
                    newLoc.child("locationGoal").setValue(goal);
                   // newLoc.child("photoUrl").setValue(downloadUrl);

                        newLoc.child("locationDistance").setValue(getIntent().getExtras().get("LocDistance"));
                    newLoc.child("locationPhotoUrl").setValue(downloadUrl.toString());
                    Log.d(TAG, "onSuccess: "+add_loc_distance.getText().toString());
                        newLoc.child("fromLat").setValue(getIntent().getExtras().get("fromLat"));
                        newLoc.child("fromLong").setValue(getIntent().getExtras().get("fromLong"));
                        newLoc.child("toLat").setValue(getIntent().getExtras().get("toLat"));
                        newLoc.child("toLong").setValue(getIntent().getExtras().get("toLong"));



                    mProgress.dismiss();

                    startActivity(new Intent(AddLocationActivity.this, MainActivity.class));

                }
            });



        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {
            mImageUri=data.getData();
            add_loc_image.setImageURI(mImageUri);
        }
    }


}
