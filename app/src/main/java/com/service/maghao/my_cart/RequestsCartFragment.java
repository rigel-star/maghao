package com.service.maghao.my_cart;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.service.maghao.R;
import com.service.maghao.adapter_classes.RequestsAdapter;
import com.service.maghao.important_classes.UserAccountId;
import com.service.maghao.model_classes.Request;

import java.util.ArrayList;
import java.util.List;


public class RequestsCartFragment extends Fragment {


    public RequestsCartFragment() {
        // Required empty public constructor
    }

    //recycler view
    private RecyclerView recyclerView;
    //request list
    private List<Request> requestList;
    //adapter
    private RequestsAdapter requestsAdapter;
    //db reference
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("dataOnMap");
    //user id
    UserAccountId userAccountId = new UserAccountId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests_cart, container, false);

        //init every thing
        init(view);

        loadDataToRecyclerView();

        return view;
    }

    private void init(View view){

        recyclerView = view.findViewById(R.id.requests_recycler_view);
        requestList = new ArrayList<>();
        requestsAdapter = new RequestsAdapter(getActivity(), requestList);
    }

    //load data to recycler view
    private void loadDataToRecyclerView(){

        reference.child(userAccountId.getUserId())
                .child("requests")
                .addChildEventListener(
                        new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                String type = dataSnapshot.child("name").getValue(String.class);
                                String img = dataSnapshot.child("image").getValue(String.class);

                                requestList.add(new Request(type, img));

                                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                                recyclerView.setAdapter(requestsAdapter);

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
