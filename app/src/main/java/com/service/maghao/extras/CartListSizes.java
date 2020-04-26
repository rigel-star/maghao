package com.service.maghao.extras;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.service.maghao.important_classes.UserAccountId;

import java.util.ArrayList;
import java.util.List;

public class CartListSizes {


    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("dataOnCart");
    public List<String> list = new ArrayList<>();
    private UserAccountId userAccountId = new UserAccountId();
    private TextView sizeTextView;

    public CartListSizes(){
    }

    public void loadOrderList(){

        reference.child(userAccountId.getUserId())
                .child("orders")
                .addChildEventListener(
                        new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                String name = dataSnapshot.child("name").getValue(String.class);
                                Log.d("CartListSizes", "nameLoaded: "+ name);
                                list.add(name);
                                sizeTextView.setText(String.valueOf(list.size()));
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

    public void setSizeTextView(TextView sizeView){this.sizeTextView = sizeView;}

}
