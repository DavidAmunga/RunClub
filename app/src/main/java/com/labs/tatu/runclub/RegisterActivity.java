package com.labs.tatu.runclub;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    Button btnRegister;
    EditText txtEmail,txtPass,txtConfirmPass,txtUserName;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        intiViews();

        mAuth=FirebaseAuth.getInstance();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Register User

                signUp();


            }
        });
    }

    private void signUp() {
        final String email=txtEmail.getText().toString().trim();
        String password=txtPass.getText().toString().trim();
        final String userName=txtUserName.getText().toString().trim();
        String confirmPass=txtConfirmPass.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(confirmPass))
        {
            if(!confirmPass.equals(password))
            {
                Toast.makeText(this, "Please Confirm your Correct Password", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, R.string.auth_failed,
                                            Toast.LENGTH_SHORT).show();
                                }

                                else
                                {

                                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").push();
                                    ref.child("userEmail").setValue(email);
                                    ref.child("userName").setValue(userName);
                                    ref.child("userPhotoUrl").setValue("");
                                    ref.child("userLevel").setValue("Tera");
                                    ref.child("userBio").setValue("");
                                    ref.child("userPhoneNo").setValue("");
                                    ref.child("userPoints").setValue("0");



                                    StyleableToast st=new StyleableToast(getApplicationContext(),"Good! Now Log In!",Toast.LENGTH_SHORT);
                                    st.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                    st.setTextColor(Color.WHITE);
                                    st.setIcon(R.drawable.ic_account_box_black_24dp);
                                    st.show();
                                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));

                                }

                                // ...
                            }
                        });



            }


        }
        else
        {
            Toast.makeText(this, "Please fill out all the Fields!", Toast.LENGTH_SHORT).show();
        }





        }





    private void intiViews() {
        btnRegister=(Button)findViewById(R.id.btnRegister);
        txtEmail=(EditText) findViewById(R.id.txt_email);
        txtUserName=(EditText) findViewById(R.id.txt_userName);
        txtPass=(EditText) findViewById(R.id.txt_pass);
        txtConfirmPass=(EditText) findViewById(R.id.txt_confirm_pass);
    }
}
