package com.labs.tatu.runclub;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //        Set No Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//

        EasySplashScreen config=new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(LoginActivity.class)
                .withSplashTimeOut(3000)
                .withBackgroundColor(Color.parseColor("#000000"))
                .withLogo(R.drawable.logo)

                .withBeforeLogoText("RunClub")
                .withAfterLogoText("Running together");

        config.getLogo().setMinimumHeight(250);
        config.getLogo().setMinimumWidth(250);



        //Set text color

        config.getAfterLogoTextView().setTextColor(Color.WHITE);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);

        View view=config.create();

        setContentView(view);
    }
}
