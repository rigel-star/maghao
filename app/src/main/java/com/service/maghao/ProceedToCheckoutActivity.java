package com.service.maghao;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.service.maghao.extras.GpsAndNetworkChecker;
import com.service.maghao.important_classes.UserAccountId;
import com.service.maghao.model_classes.DraftsItems;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

public class ProceedToCheckoutActivity extends AppCompatActivity implements Runnable{

    TextView changeTime;
    TextView deliveryTime;
    TextView userName, userLoc, userPh, userMail, oSummary;
    String expense;

    //user lat lng
    private LatLng latLng = new LatLng(0, 0);
    String userLat, userLng;

    boolean userLocAvailable = false;

    //user id
    UserAccountId userAccountId = new UserAccountId();
    //db reference
    FirebaseFirestore fDatabase = FirebaseFirestore.getInstance();
    CollectionReference cReference = fDatabase.collection("Users");
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    GpsAndNetworkChecker gpsAndNetworkChecker;
    //order list
    List<DraftsItems> itemsList;

    Button publishOrder;

    //time
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MM yyyy, ");
    Date date = new Date();
    String finalDate = simpleDateFormat.format(date);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proceed_to_checkout);

        //init widgets, nothing much
        changeTime = findViewById(R.id.change_time);
        deliveryTime = findViewById(R.id.delivery_time);
        userName = findViewById(R.id.checkout_p_name);
        userPh = findViewById(R.id.checkout_p_ph);
        userLoc = findViewById(R.id.checkout_p_loc);
        userMail = findViewById(R.id.checkout_p_mail);
        oSummary = findViewById(R.id.order_summary);
        publishOrder = findViewById(R.id.finally_pub_products);

        gpsAndNetworkChecker = new GpsAndNetworkChecker(this);
        expense = getIntent().getStringExtra("order_sum").trim();

        itemsList = new ArrayList<>();

        loadUserData();

        Thread thread = new Thread(this);
        thread.start();

        //cost of user on order
        oSummary.setText(expense);

        changeTime.setOnClickListener(
                view -> {
                    pickTime();
                }
        );

        publishOrder.setOnClickListener(
                view -> {
                    confirmation();
                }
        );

    }

    private void pickTime(){

        View view = LayoutInflater.from(this).inflate(R.layout.time_picker_layout, null);

        TimePicker timePicker = view.findViewById(R.id.time_picker);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    deliveryTime.setText(finalDate + timePicker.getHour() + ":" + timePicker.getMinute() + " " + timePicker.getTransitionName());
                });

        builder.create().show();

    }


    //loading data from realtime db
    private void userProfileData(){

        cReference.document(userAccountId.getUserId())
                .collection("profileData").get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()){

                                    for (QueryDocumentSnapshot qSnapshot: task.getResult()){

                                        String name = qSnapshot.getString("name");
                                        String number = qSnapshot.getString("number");
                                        String email = qSnapshot.getString("email");

                                        userName.setText(name);
                                        userPh.setText(number);
                                        userMail.setText(email);
                                    }
                                }
                            }
                        }
                );
    }


    @Override
    public void run() {
        //load data on specific text views
        userProfileData();
    }

    private void loadUserData(){
        reference.child("dataOnCart")
                .child(userAccountId.getUserId())
                .child("orders")
                .addChildEventListener(
                        new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                String name = dataSnapshot.child("name").getValue(String.class);
                                String image = dataSnapshot.child("image").getValue(String.class);
                                String price = dataSnapshot.child("price").getValue(String.class);
                                String qty = dataSnapshot.child("qty").getValue(String.class);

                                itemsList.add(new DraftsItems(name, image, qty, price));
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

    private void publishOrderList(){

        Map<String, String> data = new HashMap<>();

        for (DraftsItems item: itemsList) {

            data.put("name", item.getName());
            data.put("image", item.getImage());
            data.put("qty", item.getQty());
            data.put("price", item.getPrice());
            data.put("state", "ordered");

            reference.child("dataOnMap")
                    .child(userAccountId.getUserId())
                    .child("orders")
                    .push()
                    .setValue(data);

            data.clear();

        }

        generateNotif();
        Toast.makeText(this, "Your order list is public now, wait for call from business keepers.", Toast.LENGTH_SHORT).show();
    }


    private void generateNotif() {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "notificationForEveryOs")
                        .setContentTitle("Order Successful")
                        .setSmallIcon(R.drawable.app_logo)
                        .setAutoCancel(true)
                        .setContentText("You will receive call, after we receive your order.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(777, builder.build());
    }

    //some data on map of user to show it to shop keeper
    private void publishProfile(String name, String time, String total, String phone){

        if (time.equals("Time not set, click on change to set time.")) {
            Toast.makeText(this, "Please select time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.equals("Loading...")) {
            Toast.makeText(this, "Wait, information is loading.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("contact", phone);
        data.put("cost", total);
        data.put("d_time", time);

        reference.child("dataOnMap")
                .child(userAccountId.getUserId())
                .child("profile")
                .setValue(data);

        publishOrderList();

    }


    //confirm order
    private void confirmation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your order list will be published!")
                .setNegativeButton("No", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (!itemsList.isEmpty()) {
                            publishProfile(userName.getText().toString(), deliveryTime.getText().toString(),
                                    oSummary.getText().toString(), userPh.getText().toString());

                        } else {
                            Toast.makeText(ProceedToCheckoutActivity.this, "No orders!", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .create().show();
    }

}
