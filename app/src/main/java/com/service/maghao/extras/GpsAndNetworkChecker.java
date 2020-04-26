package com.service.maghao.extras;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.service.maghao.R;


public class GpsAndNetworkChecker {

    private Context context;

    private double userLat, userLng;

    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    //lat lng of user
    private LatLng latLng;

    public GpsAndNetworkChecker(Context context){
        this.context = context;
    }

    public boolean isLocationPermissionGranted(){

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        else {
            Toast.makeText(context, "Location Permission Required", Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions((Activity) context, permissions, 1001);
        }
        return false;
    }

    public boolean isGpsEnabled(){

        //check if location not enabled
        //LocationManager gives all the method required to check location enabled or not
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled;
        boolean network_enabled;

        //gps checker
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //network access
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gps_enabled && !network_enabled){
            gpsNotEnabledDialog();
            return false;
        }
        else
            find_user_location();

        return true;
    }

    public LatLng find_user_location(){

        boolean locationServicesOk = false;

        //will get user last location
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        //location variable
        Task location;

        if (!isLocationPermissionGranted()){
            if (!isGpsEnabled()){
                return new LatLng(0.0, 0.0);
            }
        }
        else
            locationServicesOk = true;

        if (locationServicesOk){

            location = locationProviderClient.getLastLocation();

            location.addOnCompleteListener(
                    new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful()){

                                Location currentLocation = (Location) task.getResult();

                                if (currentLocation != null) {
                                    userLat = currentLocation.getLatitude();
                                    userLng = currentLocation.getLongitude();

                                    latLng = new LatLng(userLat, userLng);
                                }

                            }
                        }
                    }
            );

        }

        return latLng;

    }

    public double getUserLat(){
        return userLat;
    }
    public double getUserLng(){
        return userLng;
    }

    public void gpsNotEnabledDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Location not available").setMessage("Please turn on your location, we need your location " +
                "to set your order list in map.");

        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();

    }

    public void shoNoLocationMsg(){
        View view = LayoutInflater.from(context).inflate(R.layout.warning_layout, null);

        ImageView icon = view.findViewById(R.id.warning_icon);
        TextView msg = view.findViewById(R.id.warning_msg);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        icon.setImageResource(R.raw.no_location);
        msg.setText("Make sure your location is enabled");

        builder.setView(view);
        builder.create();

        AlertDialog dialog = builder.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}
