package com.service.maghao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.maghao.important_classes.UserAccountId;
import com.service.maghao.important_classes.UserLatLng;
import com.service.maghao.model_classes.OrderedItems;
import com.service.maghao.map.MarkerDragerr;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    public MapsLocationActivity(){}

    //changing loc of marker
    private MarkerDragerr markerDragerr;

    //testing purpose
    List<OrderedItems> orderedItemsList;

    //id class
    UserAccountId userAccountId = new UserAccountId();

    //location class: lat lng
    UserLatLng userLatLng;

    private static final String TAG = "MapActivity";

    //const variables
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final float DEFAULT_ZOOM = 17f;


    public double setLocationLat = 0.0, setLocationLng = 0.0;

    //database to store cart location
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


    //image view for buttons
    private ImageView myLocationButton, publishCartButton, goToHomeButton;

    //current location variable
    private FusedLocationProviderClient fusedlocationProviderClient;

    boolean locationPermissionGranted = false;

    //marker
    private Marker marker;

    private GoogleMap myGoogleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_location);

        myLocationButton = findViewById(R.id.my_location_button);
        publishCartButton = findViewById(R.id.publish_products_to_map_button);
        goToHomeButton = findViewById(R.id.go_to_home_button);

        //list //testing purpose
        orderedItemsList = new ArrayList<>();

        userLatLng = new UserLatLng(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        onButtonClicks();

        getLocationsPermission();
    }

    //button click events
    private void onButtonClicks(){

        myLocationButton.setOnClickListener(
                view -> getDeviceLocation()
        );

        publishCartButton.setOnClickListener(
                view -> startActivity(new Intent(MapsLocationActivity.this, MyCartActivity.class))
        );

        goToHomeButton.setOnClickListener(
                view -> {
                    startActivity(new Intent(MapsLocationActivity.this, MainActivity.class));
                    finish();
                }
        );

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        myGoogleMap = googleMap;

        markerDragerr = new MarkerDragerr(this, myGoogleMap);

        if (locationPermissionGranted){

            myGoogleMap.setMyLocationEnabled(true);
            myGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

            getDeviceLocation();

        }

        Log.d(TAG, "userLocation: " + setLocationLat + " " + setLocationLng);

    }

    //load user data on users current location position
    private void loadUserDataOnUsersCurrentLocation(double lat, double lng){

        if (lat != 0 && lng != 0) {

            if (marker != null)
                marker.remove();

            reference.child("dataOnMap")
                    .child(userAccountId.getUserId())
                    .child("profile")
                    .addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String name = dataSnapshot.child("name").getValue(String.class);
                                    String contact = dataSnapshot.child("contact").getValue(String.class);

                                    //if user doesn't have any grocery item order the he will get this msg
                                    if (userLatLng.size < 1)
                                        Toast.makeText(MapsLocationActivity.this, "You don't have any grocery orders.", Toast.LENGTH_SHORT).show();

                                    //if have a marker will be added to his/her map
                                    else {

                                        //he / she can drag icon to change its location
                                        marker = myGoogleMap.addMarker(new MarkerOptions().position(new LatLng(userLatLng.getUserLatLng().latitude,
                                                userLatLng.getUserLatLng().longitude)).title(name)
                                                .snippet(contact).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                                        marker.setDraggable(true);

                                        //when marker is dragging call custom method of marker dragger
                                        markerDragerr.onMarkerDrag(marker);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );



            loadMarkersForAllRequests();

        }

    }

    //check every permission we needed for our app
    private void getLocationsPermission() {

        String[] permissions = {

                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };


        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                getDeviceLocation();
                locationPermissionGranted = true;

            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //device current location
    public void getDeviceLocation(){

        fusedlocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            if (locationPermissionGranted){

                final Task location = fusedlocationProviderClient.getLastLocation();

                //if we got the device current location
                location.addOnCompleteListener(
                        new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if (task.isSuccessful()){

                                    Log.d(TAG, "onMapReady: location found");

                                    Location currentLocation = (Location) task.getResult();

                                    try {
                                        setLocationLat = currentLocation.getLatitude();
                                        setLocationLng = currentLocation.getLongitude();

                                        loadUserDataOnUsersCurrentLocation(setLocationLat, setLocationLng);

                                        Log.d(TAG, "userLocation: " + setLocationLat + " " + setLocationLng);

                                    } catch (NullPointerException ex){
                                        Log.d(TAG, "onMapReady: message: " + ex.getMessage());
                                    }

                                    if (currentLocation != null) {
                                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                                DEFAULT_ZOOM
                                        );

                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder =
                                                new AlertDialog.Builder(MapsLocationActivity.this);

                                        builder.setTitle("Enable location")
                                                .setMessage("We aren't able to locate you so please turn on your location and " +
                                                        "try again.")
                                                .create()
                                                .show();
                                    }

                                }
                                else {

                                    Toast.makeText(MapsLocationActivity.this, "Unable to detect your location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            }


        } catch (Exception ex){

            Log.d(TAG, ex.getMessage());
        }
    }

    //move camera to the current location
    private void moveCamera(LatLng latLng, float zoom) {

        //LatLng location = new LatLng(27.6727, 85.3253);

        Log.d(TAG, "onMapReady: moving camera to the current location: lat: " + latLng.latitude + ", long: " + latLng.longitude);

        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    //load all requests
    private void loadMarkersForAllRequests(){

        Map<String, Integer> icon_data = new HashMap<>();

        //icons for different requests by their type...
        icon_data.put("Barber", R.raw.barber_map_icon);
        icon_data.put("Plumber", R.raw.plumber_map_icon);
        icon_data.put("Doctor", R.raw.first_aid_kit);
        icon_data.put("Electrician", R.raw.icon_electrician);
        icon_data.put("Tailor", R.raw.icon_tailor);
        icon_data.put("Teacher", R.raw.icon_teacher);
        icon_data.put("Mechanic", R.raw.icon_mechanics);
        icon_data.put("Engineer", R.raw.icon_engineer);
        icon_data.put("Pinter", R.raw.icon_painter);

        //requests child from db
        reference.child(userAccountId.getUserId()).child("requests").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        String request_type = dataSnapshot.child("requestType").getValue(String.class);

                        String lat = dataSnapshot.child("latitude").getValue(String.class);
                        String lng = dataSnapshot.child("longitude").getValue(String.class);

                        double lt = Double.parseDouble(lat);
                        double lg = Double.parseDouble(lng);

                        Log.d(TAG, "userRequests: " + request_type + " " + lat + " " + lng);

                        myGoogleMap.addMarker(
                                new MarkerOptions().position(new LatLng(lt, lg))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                .draggable(true));
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
