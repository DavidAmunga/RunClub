package com.labs.tatu.runclub;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.labs.tatu.runclub.helpers.BottomNavigationViewHelper;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //        Set No Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.ic_events:
                        startActivity(new Intent(StatsActivity.this,EventsActivity.class));
                        break;


                    case R.id.ic_friends:
                        startActivity(new Intent(StatsActivity.this,FriendsActivity.class));

                        break;

                    case R.id.ic_inbox:
                        startActivity(new Intent(StatsActivity.this,InboxActivity.class));

                        break;

                    case R.id.ic_stats:
                        startActivity(new Intent(StatsActivity.this,StatsActivity.class));

                        break;
                    case R.id.ic_run:
                        startActivity(new Intent(StatsActivity.this,MainActivity.class));

                        break;

                }
                return false;
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
