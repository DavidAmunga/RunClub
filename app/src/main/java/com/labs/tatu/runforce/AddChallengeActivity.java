package com.labs.tatu.runforce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddChallengeActivity extends AppCompatActivity {

    private ImageButton mSelectImage;

    private EditText mChallengeTitle;
    private EditText mChallengeDescr;
    private EditText mChallengeLoc;

    private Button submit_btn;

    private Uri mImageUri = null;

    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

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
        setContentView(R.layout.activity_add_challenge);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Challenges");

        mSelectImage = (ImageButton) findViewById(R.id.add_challenge_image);


        mProgress = new ProgressDialog(this);
        mChallengeTitle = (EditText) findViewById(R.id.challenge_name);
        mChallengeDescr = (EditText) findViewById(R.id.challenge_descr);
        mChallengeLoc = (EditText) findViewById(R.id.challenge_location);

        submit_btn = (Button) findViewById(R.id.btn_submit);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postChallenge();
            }
        });

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
    }

    private void postChallenge() {

        mProgress.setMessage("Posting Challenge");
        mProgress.setCancelable(false);
        mProgress.show();
        final String title_val = mChallengeTitle.getText().toString().trim();
        final String desc_val = mChallengeDescr.getText().toString().trim();
        final String loc_val = mChallengeLoc.getText().toString().trim();

        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && !TextUtils.isEmpty(loc_val) && mImageUri != null) {
            StorageReference filepath = mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference newChallenge = mDatabase.push();
                    newChallenge.child("challengeName").setValue(title_val);
                    newChallenge.child("challengeDesc").setValue(desc_val);
                    newChallenge.child("challengeLoc").setValue(loc_val);
                    newChallenge.child("challengeImage").setValue(downloadUrl.toString());
                    newChallenge.child("challenger").setValue("RunClub");


                    mProgress.dismiss();

                    Toast.makeText(AddChallengeActivity.this, "Challenge Added", Toast.LENGTH_SHORT).show();


                }
            });

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);
        }
    }
}
