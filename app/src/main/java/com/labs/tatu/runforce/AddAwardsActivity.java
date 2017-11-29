package com.labs.tatu.runforce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddAwardsActivity extends AppCompatActivity {
    private static final String TAG = "AddAwardsActivity";

    private CircleImageView btnAddAwardImage;
    private Button btnAddAward;

    private TextView txtAwardTitle, txtAwardDetails;
    private Uri mImageUri = null;

    private static final int GALLERY_REQUEST = 1;

    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_awards);
        initViews();
        mProgress = new ProgressDialog(this);

        btnAddAwardImage = (CircleImageView) findViewById(R.id.add_awardImage);
        btnAddAwardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Awards");

        btnAddAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setMessage("Adding Award");
                mProgress.setCancelable(false);
                mProgress.show();

                final String awardName = txtAwardTitle.getText().toString().trim();
                final String awardDetail = txtAwardDetails.getText().toString().trim();

                if (!TextUtils.isEmpty(awardName) && !TextUtils.isEmpty(awardDetail)) {
                    Log.d(TAG, "Uploading Image ");
                    StorageReference filepath = mStorage.child("Award_Images").child(mImageUri.getLastPathSegment());

                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            @SuppressWarnings("VisibleForTesting") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d(TAG, "onSuccess: " + downloadUrl);
                            DatabaseReference newLoc = mDatabase.push();
                            newLoc.child("awardImage").setValue(downloadUrl.toString());
                            newLoc.child("awardName").setValue(awardName);
                            newLoc.child("awardDescription").setValue(awardDetail);


                            mProgress.dismiss();

                            startActivity(new Intent(AddAwardsActivity.this, MyRunActivity.class));

                        }
                    });


                }


            }
        });


    }

    private void initViews() {
        txtAwardTitle = (TextView) findViewById(R.id.add_award_name);
        txtAwardDetails = (TextView) findViewById(R.id.add_award_details);
        btnAddAward = (Button) findViewById(R.id.btn_add_award);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            btnAddAwardImage.setImageURI(mImageUri);
        }
    }
}
