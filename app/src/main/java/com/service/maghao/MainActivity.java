package com.service.maghao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.service.maghao.fragment_classes.HomeFragment;
import com.service.maghao.fragment_classes.MapsFragment;
import com.service.maghao.fragment_classes.NotificationFragment;
import com.service.maghao.fragment_classes.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavBar;

    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subscribeToTopic();

        bottomNavBar = findViewById(R.id.home_bottom_nav_bar);

        bottomNavBar.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame_layout,
                new HomeFragment()).commit();

        Log.d(TAG, "onAppStarted: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.home_nav_bar:
                            selectedFragment = new HomeFragment();
                            break;

                        case R.id.notif_nav_bar:
                            selectedFragment = new NotificationFragment();
                            break;

                        case R.id.map_nav_bar:
                            selectedFragment = new MapsFragment();
                            break;

                        case R.id.settings_nav_bar:
                            selectedFragment = new SettingsFragment();
                            break;

                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame_layout,
                            selectedFragment).commit();

                    return true;
                }
            };


    private void subscribeToTopic(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel =
                    new NotificationChannel("notificationForOsOverEight",
                            "notificationForOsOverEight", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("maghao_user");
    }

}
