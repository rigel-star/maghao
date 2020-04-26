package com.service.maghao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.service.maghao.my_cart.FavoritesCartFragment;
import com.service.maghao.my_cart.OrderedCartFragment;
import com.service.maghao.my_cart.RequestsCartFragment;

public class MyCartActivity extends AppCompatActivity {

    BottomNavigationView navigationView;

    public MyCartActivity() {
    }

    public static final String TAG = "CartActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

        navigationView = findViewById(R.id.cart_nav_bar);

        navigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.cart_frame_layout,
                new FavoritesCartFragment()).commit();

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.cart_nav_bar_drafts:
                            selectedFragment = new FavoritesCartFragment();
                            break;

                        case R.id.cart_nav_bar_ordered:
                            selectedFragment = new OrderedCartFragment();
                            break;

                        case R.id.cart_nav_bar_requests:
                            selectedFragment = new RequestsCartFragment();
                            break;

                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.cart_frame_layout,
                            selectedFragment).commit();

                    return true;
                }
            };

}

