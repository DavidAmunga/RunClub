package com.labs.tatu.runforce;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by amush on 17-Sep-17.
 */

public class MyRunClub extends android.app.Application
{
        @Override
        public void onCreate() {
            super.onCreate();
    /* Enable disk persistence  */
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
}
