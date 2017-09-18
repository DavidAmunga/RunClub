package com.labs.tatu.runclub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView user_image;
    Button btnMyRunActivity;
    TextView txtPhoneNo,txtBio,txtEmail,txtName;


    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        mAuth=FirebaseAuth.getInstance();

        txtName.setText(mAuth.getCurrentUser().getDisplayName());
        txtEmail.setText(mAuth.getCurrentUser().getEmail());
        Picasso
                .with(this)
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

        btnMyRunActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MyRunActivity.class));
            }
        });


    }

    private void initViews() {
        txtPhoneNo=(TextView)findViewById(R.id.txt_phoneNo);
        txtName=(TextView)findViewById(R.id.txtUserName);
        txtEmail=(TextView)findViewById(R.id.txtUserEmail);
        txtBio=(TextView)findViewById(R.id.txt_bio);
        btnMyRunActivity=(Button)findViewById(R.id.btnMyRunActivity);
        user_image=(CircleImageView)findViewById(R.id.txtUserImage);
    }
}
