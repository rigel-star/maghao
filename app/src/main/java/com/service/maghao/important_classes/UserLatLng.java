package com.service.maghao.important_classes;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserLatLng {

    UserLatLng(){}

    UserAccountId userAccountId = new UserAccountId();

    LatLng latLng = new LatLng(0.0, 0.0);
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public int size = 0;
    private Context context;

    public UserLatLng(Context context){

        this.context = context;

        loadData();

        reference.child("dataOnCart").child(userAccountId.getUserId())
                .child("profile")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String lat = dataSnapshot.child("latitude").getValue(String.class);
                                String lng = dataSnapshot.child("longitude").getValue(String.class);

                                if (lat != null && lng != null) {
                                    Double lt = Double.parseDouble(lat), lg = Double.parseDouble(lng);
                                    latLng = new LatLng(lt, lg);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );

    }

    public LatLng getUserLatLng(){
        return latLng;
    }

    public void loadData(){

        List<String> list = new ArrayList<>();

        reference.child("dataOnMap").child(userAccountId.getUserId())
                .child("orders")
                .addChildEventListener(
                        new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                String name = dataSnapshot.child("name").getValue(String.class);
                                list.add(name);
                                size = list.size();
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
    }


}
