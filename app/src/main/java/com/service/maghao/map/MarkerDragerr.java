package com.service.maghao.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.maghao.R;
import com.service.maghao.important_classes.UserAccountId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkerDragerr {

    MarkerDragerr(){}

    //map class init
    private static GoogleMap googleMap;
    private static Context context;
    static boolean confirmLoc = false;
    static UserAccountId userAccountId = new UserAccountId();
    private static Address finalAddress;
    static Marker dragMarker;
    static DatabaseReference reference = FirebaseDatabase.getInstance().getReference("dataOnMap");

    //constuctor to get actual map
    public MarkerDragerr(Context context, GoogleMap googleMap){
        this.googleMap = googleMap;
        this.context = context;
    }

    public void onMarkerDrag(Marker targetMarker){

        googleMap.setOnMarkerDragListener(
                new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker destMarker) {
                        Toast.makeText(context, "Drag marker to your preferred location.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {

                        Geocoder geocoder = new Geocoder(context);
                        LatLng latLng = marker.getPosition();

                        List<Address> addressList;

                        try{
                            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            setFinalLocation(addressList, latLng);
                        }
                        catch (Exception ex){

                            System.out.println(ex.getMessage());
                        }

                    }
                }
        );
    }

    private static void setFinalLocation(List<Address> addressList, LatLng ltlg){

        if (addressList.size() > 0) {

            finalAddress = addressList.get(0);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(finalAddress.getAddressLine(0)).setTitle("Confirm this location?")

            .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    confirmLoc = true;

                                    profileInfo(ltlg);
                                    Toast.makeText(context, "New location set!", Toast.LENGTH_SHORT).show();

                                    Activity activity = (Activity) context;
                                    activity.recreate();

                                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(ltlg));
                                }
                            });

            builder.create().show();



        }
    }

    private static void profileInfo(LatLng latLng){

        Map<String, String> map = new HashMap<>();
        map.put("latitude", String.valueOf(latLng.latitude));
        map.put("longitude", String.valueOf(latLng.longitude));

        Map<String, Object> data = new HashMap<>();
        data.put("profile", map);

        //Toast.makeText(context, map.get("latitude"), Toast.LENGTH_SHORT).show();

        //reference.child(userAccountId.getUserId()).child("ordered").updateChildren(data);

        reference.child(
                userAccountId.getUserId()
        ).child("profile")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("name").getValue(String.class);
                                String contact = dataSnapshot.child("contact").getValue(String.class);
                                map.put("name", name);
                                map.put("contact", contact);
                                updateLocToFirebase(map);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );

    }

    private static void updateLocToFirebase(Map<String, String> data){

        reference.child(userAccountId.getUserId())
                .child("profile")
                .setValue(data);

    }
}
