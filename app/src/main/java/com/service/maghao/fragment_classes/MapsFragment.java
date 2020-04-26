package com.service.maghao.fragment_classes;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.service.maghao.MapsLocationActivity;
import com.service.maghao.MyCartActivity;
import com.service.maghao.R;


public class MapsFragment extends Fragment{


    //widgets
    private Button exploreButton;
    private Button exploreCart;

    public MapsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        exploreButton = view.findViewById(R.id.explore_map_button);
        exploreCart = view.findViewById(R.id.explore_cart_button);

        exploreButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().startActivity(new Intent(getActivity(), MapsLocationActivity.class));
                    }
                });

        exploreCart.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().startActivity(new Intent(getActivity(), MyCartActivity.class));
                    }
                });

        return view;
    }


}
