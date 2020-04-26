package com.service.maghao.adapter_classes;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.service.maghao.R;
import com.service.maghao.extras.GpsAndNetworkChecker;
import com.service.maghao.important_classes.UserAccountId;
import com.service.maghao.model_classes.CategoryItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesItemsAdapter extends RecyclerView.Adapter<ServicesItemsAdapter.MyViewHolder> {

    private static final String TAG = "ServiceClass";

    private String[] categorySet = {
            "Doctor", "Electrician", "Tailor", "Plumber", "Barber", "Teacher",
            "Mechanic", "Engineer", "Painter"
    };

    //user id
    private UserAccountId userAccountId = new UserAccountId();

    private String[] serviceIcons = {
      "https://firebasestorage.googleapis.com/v0/b/maghao-service.appspot.com/o/Services%20Icons%2Finjection.png?alt=media&token=58d4bd6d-1d66-4b28-9386-aabd56c42694",
      "https://firebasestorage.googleapis.com/v0/b/maghao-service.appspot.com/o/Services%20Icons%2Fsoldering-iron.png?alt=media&token=89e91063-e959-4f17-92a5-a34d017ff863",
      "https://firebasestorage.googleapis.com/v0/b/maghao-service.appspot.com/o/Services%20Icons%2Fneedle.png?alt=media&token=fdbd23a5-5a64-4a76-912f-a3fb7d311adf",
      "https://firebasestorage.googleapis.com/v0/b/maghao-service.appspot.com/o/Services%20Icons%2Fplunger.png?alt=media&token=933361e8-ab56-49bd-a62e-1bcf364f3ecd",
      "https://firebasestorage.googleapis.com/v0/b/maghao-service.appspot.com/o/Services%20Icons%2Fsalon.png?alt=media&token=91ef8e74-5fc8-46c0-b00e-f54bb928115e",
      "https://firebasestorage.googleapis.com/v0/b/maghao-service.appspot.com/o/Services%20Icons%2Fpencil.png?alt=media&token=c076cc85-c041-4694-9b81-94efbc074350",
      "https://firebasestorage.googleapis.com/v0/b/maghao-service.appspot.com/o/Services%20Icons%2Fmechanics.png?alt=media&token=f38a0649-ac43-48a0-9069-4386fec1a1a5",
      "https://firebasestorage.googleapis.com/v0/b/maghao-service.appspot.com/o/Services%20Icons%2Fhelmet.png?alt=media&token=431bebb6-f29b-49c0-9ceb-f50deedbc553",
      "https://firebasestorage.googleapis.com/v0/b/maghao-service.appspot.com/o/Services%20Icons%2Fpaint-roller.png?alt=media&token=0b868632-1723-41a2-9d28-9a117b909ae5"
    };

    private List<CategoryItem> categoryItemList;
    private Context context;

    //animation
    Animation fadeInAnimation;

    //private and final database reference
    private DatabaseReference reference =
            FirebaseDatabase.getInstance().getReference();

    //gps checker class checks if location enabled or not
    private GpsAndNetworkChecker gpsAndNetworkChecker;

    ServicesItemsAdapter(){}

    public ServicesItemsAdapter(Context context, List<CategoryItem> categoryItemList){

        this.context = context;
        this.categoryItemList = categoryItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.category_view_main_screen, null);

        gpsAndNetworkChecker = new GpsAndNetworkChecker(context);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.textView.setText(categoryItemList.get(position).getCategoryName());
        holder.imageView.setImageResource(categoryItemList.get(position).getCategoryImage());


        holder.layout.setOnClickListener(
                view -> {
                    contactServiceDialog(categorySet[position], categoryItemList.get(position).getCategoryName(),
                            categoryItemList.get(position).getCategoryImage(), serviceIcons[position]);
                }
        );
    }

    @Override
    public int getItemCount() {
        return categoryItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{


        ImageView imageView;
        TextView textView;


        LinearLayout layout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.category_image);
            textView = itemView.findViewById(R.id.category_name);

            layout = itemView.findViewById(R.id.particular_items_linear_layout);
        }
    }

    //pop up
    private void contactServiceDialog(String category, String name, int requestImg ,String image){

        fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.dialog_fade_in);

        Map<String, String> data = new HashMap<>();

        View view = LayoutInflater.from(context).inflate(R.layout.contact_service_layout, null);

        Button sendRequestButton = view.findViewById(R.id.send_service_request_button);
        Button cancelButton = view.findViewById(R.id.request_cancel_button);

        //request type icon
        ImageView requestIcon = view.findViewById(R.id.request_type_icon);
        TextView requestName = view.findViewById(R.id.request_type_name);

        TextView serviceMsg = view.findViewById(R.id.service_type_msg);

        requestIcon.setImageResource(requestImg);
        requestName.setText(name);

        serviceMsg.setText("You are about to inform nearby " + name + "'s that you need " + name + " service at your home.");

        //setting fade in animation for request type icon
        requestIcon.startAnimation(fadeInAnimation);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        sendRequestButton.setOnClickListener(
                view1 -> {
                        if (gpsAndNetworkChecker.isLocationPermissionGranted()) {
                            if (gpsAndNetworkChecker.isGpsEnabled()) {

                                gpsAndNetworkChecker.find_user_location();

                                data.put("name", name);
                                data.put("image", image);
                                data.put("latitude", String.valueOf(gpsAndNetworkChecker.getUserLat()));
                                data.put("longitude", String.valueOf(gpsAndNetworkChecker.getUserLng()));
                                data.put("requestType", name);

                                Log.d(TAG, "userLatLng: " + gpsAndNetworkChecker.getUserLat()
                                        + " " + gpsAndNetworkChecker.getUserLng());

                                if (data.get("latitude").equals("0.0") | data.get("longitude").equals("0.0"))
                                    Toast.makeText(context, "Please turn off your location once and turn it on again.", Toast.LENGTH_SHORT).show();
                                else {

                                    reference.child("dataOnMap").child(userAccountId.getUserId())
                                            .child("requests")
                                            .child(category)
                                            .setValue(data);

                                    reference.child(category).child(userAccountId.getUserId()).setValue(data);

                                    Log.d(TAG, "userData: " + data.get("name") + " " + data.get("requestType") + " " +
                                            data.get("image"));

                                    }
                                }
                            }
                        });

        builder.setView(view);

        AlertDialog dialog = builder.create();

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlide;

        dialog.show();

        cancelButton.setOnClickListener(
                (bttn) -> {
                        dialog.dismiss();

                }
        );
    }
}
