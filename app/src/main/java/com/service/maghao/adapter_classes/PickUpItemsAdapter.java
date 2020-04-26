package com.service.maghao.adapter_classes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.service.maghao.R;
import com.service.maghao.extras.CheckInternetConnection;
import com.service.maghao.extras.GpsAndNetworkChecker;
import com.service.maghao.important_classes.UserAccountId;
import com.service.maghao.model_classes.DraftsItems;
import com.service.maghao.model_classes.ProductDetails;
import com.service.maghao.sqlite_database.DatabaseHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickUpItemsAdapter extends RecyclerView.Adapter<PickUpItemsAdapter.MyViewHolder> implements Filterable{

    //internet connection checker
    private CheckInternetConnection checkConn;

    //database handler
    private DatabaseHandler dbHandler;

    private List<String> priceList = new ArrayList<>();

    //database references
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("dataOnCart")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private List<ProductDetails> categoryItemList;
    private List<ProductDetails> getCategoryItemList;

    private Context context;

    private Map<String, String>  profileData = new HashMap<>();
    private Map<String, String> data = new HashMap<>();

    private GpsAndNetworkChecker gpsAndNetworkChecker;
    private FirebaseFirestore database;

    UserAccountId userAccountId = new UserAccountId();

    //user exist
    private boolean userLocAvailable = false;

    PickUpItemsAdapter(){}

    PickUpItemsAdapter(Context context, List<ProductDetails> categoryItemList){

        this.context = context;
        this.categoryItemList = categoryItemList;
        this.getCategoryItemList = categoryItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.pickup_product_card, null);

        gpsAndNetworkChecker = new GpsAndNetworkChecker(context);
        checkConn = new CheckInternetConnection(context);
        dbHandler = new DatabaseHandler(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getUserProfileData();
                checkUserExistence();
            }
        }).start();


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        ProductDetails productDetails = categoryItemList.get(position);

        holder.textView.setText(productDetails.getProductName());
        holder.marketPriceTextView.setText("Market Price: Rs " + productDetails.getMarketPrice());
        holder.addToFav.setTag("n_fav");

        priceList.add(productDetails.getProductQuantity());

        holder.totalCostTextView.setText("Your cost: " + productDetails.getMarketPrice());

        //check if product is in user fav list
        for (int i=0;i<dbHandler.getAllItems().size();i++){
            if (productDetails.getProductId().equals(dbHandler.getAllItems().get(i).getId())){
                holder.addToFav.setImageResource(R.drawable.ic_fav_filled);
                holder.addToFav.setTag("fav");
            }

        }

        /*edit text on recycler does not
        * show the keyboard
        * so we have to manually make a dialog with edit text and
        * have to input quantity we want*/
        holder.productQtyTextView.setText(productDetails.getProductQuantity());

        //add to fav button
        holder.addToFav.setOnClickListener(
                v -> {
                    deleteOrAddToFav(holder.addToFav, productDetails);
                }
        );

        //finding final cost from the particular product of user when he clicks
        holder.increase.setOnClickListener(
                view -> {
                    incDecProductQty(00, holder.marketPriceTextView, holder.totalCostTextView , holder.productQtyTextView);
                });

        holder.decrease.setOnClickListener(
                view -> {
                    incDecProductQty(01, holder.marketPriceTextView, holder.totalCostTextView , holder.productQtyTextView);
                }
        );

        holder.button.setOnClickListener(
                view -> {

                    if (!checkConn.isNetworkConnected())
                        checkConn.showNoInternetMsg();
                    else
                        pushProductToDatabase(holder.productQtyTextView,
                                categoryItemList.get(position).getProductName(),
                                categoryItemList.get(position).getProductImage(),
                                holder.totalCostTextView.getText().toString());
                }
        );

        Picasso.get().load(categoryItemList.get(position).getProductImage()).into(holder.imageView);



    }

    @Override
    public int getItemCount() {
        return categoryItemList.size();
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults filterResults = new FilterResults();

                if (charSequence == null | charSequence.length() == 0){

                    filterResults.count = getCategoryItemList.size();
                    filterResults.values = getCategoryItemList;

                } else{

                    String search = charSequence.toString().toLowerCase();

                    List<ProductDetails> details = new ArrayList<>();

                    for (ProductDetails pd : getCategoryItemList){
                        if (pd.getProductName().toLowerCase().contains(search)){
                            details.add(pd);
                        }
                    }

                    filterResults.count = details.size();
                    filterResults.values = details;
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                categoryItemList = (List<ProductDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }


    class MyViewHolder extends RecyclerView.ViewHolder{


        ImageView imageView, addToFav;
        TextView textView, marketPriceTextView, totalCostTextView;
        Button button;
        Button increase, decrease;
        TextView productQtyTextView;
        ProgressBar progressBar;


        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.pickup_item_image);
            textView = itemView.findViewById(R.id.pickup_item_name);
            button = itemView.findViewById(R.id.pickup_item_button);

            marketPriceTextView = itemView.findViewById(R.id.market_price_text_view);
            totalCostTextView = itemView.findViewById(R.id.total_cost_text_view);

            increase = itemView.findViewById(R.id.pickup_item_qty_inc_bttn);
            decrease = itemView.findViewById(R.id.pickup_item_qty_dec_bttn);

            addToFav = itemView.findViewById(R.id.add_to_fav_bttn);

            productQtyTextView = itemView.findViewById(R.id.pickup_item_qty_inc_dec_view);

            progressBar = itemView.findViewById(R.id.add_to_cart_progress_bar);

        }
    }

    private void pushProductToDatabase(TextView qty, String name, String image, String price){

        data.put("name", name);
        data.put("image", image);
        data.put("qty", qty.getText().toString());
        data.put("price", price.split(" ")[2]);
        data.put("state", "Unordered");

        if (userLocAvailable){

            reference.child("orders").push().setValue(data);
            addedToCartMsg(name);
            return;
        }

        if (gpsAndNetworkChecker.isLocationPermissionGranted()){
            if (gpsAndNetworkChecker.isGpsEnabled()) {

                Log.d("CartActLoc", gpsAndNetworkChecker.getUserLat() + " " + gpsAndNetworkChecker.getUserLng());

                profileData.put("latitude", String.valueOf(gpsAndNetworkChecker.getUserLat()));
                profileData.put("longitude", String.valueOf(gpsAndNetworkChecker.getUserLng()));

                /*if user location is not already available in database
                check user lat and lng in realtime
                if available, push only data in database not profile info
                */
                if (!userLocAvailable) {
                    if (profileData.get("latitude").equals("0.0") |
                            profileData.get("longitude").equals("0.0")) {
                        Toast.makeText(context, "Please turn off your location once and turn it on again.", Toast.LENGTH_SHORT).show();
                    } else {

                        reference.child("profile").setValue(profileData);
                        reference.child("orders").push().setValue(data);

                        addedToCartMsg(name);

                    }
                }

            }

            else{
                gpsAndNetworkChecker.shoNoLocationMsg();
            }
        }

    }

    //when product is added to db or cart show the msg of added to cart so user
    //can know if product added...
    private void addedToCartMsg(String name){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.added_to_cart_msg, null);
        TextView item = layout.findViewById(R.id.added_to_cart_name);
        item.setText(name + " added to cart!");
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


    //load user data from database to show in map to shop keepers
    private void getUserProfileData(){

        database = FirebaseFirestore.getInstance();

        database.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("profileData")
                .get()
                .addOnCompleteListener(
                        task -> {

                            if (task.isSuccessful()){

                                for(QueryDocumentSnapshot qSnap: task.getResult()){

                                    profileData.put("name", qSnap.getString("name"));
                                    profileData.put("contact", qSnap.getString("number"));

                                }
                            }
                        }
                );
    }


    /*
    *check if user data exist on real time database
    * if user latitude and longitude already exist make userLocAvailable condition true
    * which i will check while posting user chosen item on database
    * and i will skip the part of getting user location
    */
    private void checkUserExistence(){

        reference.child("profile")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String lat = dataSnapshot.child("latitude").getValue(String.class);

                                Log.d("PickUp", "onLocAvb: lat" + lat);

//                                Toast.makeText(context, "You are here", Toast.LENGTH_SHORT).show();

                                if (lat != null){
                                    userLocAvailable = true;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
    }

    /*like dozen or kg
    this method returns quantity type of product
    by converting the edit text value in array and
    returning that array
    */
    private String[] findQtyType(String word){

        String[] words = word.split(" ");

        return words;

    }

    //inc dec
    private void incDecProductQty(int CLICK_CODE, TextView marketPrice, TextView costView, TextView qty){

        String[] splitMarketPrice = marketPrice.getText().toString().split(" ");
        String[] splitCostView = costView.getText().toString().split(" ");
        String[] splitQty = qty.getText().toString().split(" ");

        int actualQty = Integer.parseInt(splitQty[0]);

        //actual
        int actualPrice = Integer.parseInt(splitMarketPrice[3]);

        int decreasePrice = Integer.parseInt(splitCostView[2]) - actualPrice;
        int increasePrice = Integer.parseInt(splitCostView[2]) + actualPrice;

        switch (CLICK_CODE){

            case 00:
                actualQty++;
                qty.setText(actualQty + " " + splitQty[1]);
                costView.setText("Your cost: " + increasePrice);
                break;

            case 01:

                if(actualQty == 1)
                    Toast.makeText(context, "Can't have quantity less than 1.", Toast.LENGTH_SHORT).show();

                else {
                    actualQty--;
                    qty.setText(actualQty + " " + splitQty[1]);
                    costView.setText("Your cost: " + decreasePrice);
                }
                break;
        }

    }


    private void deleteOrAddToFav(ImageView addToFav, ProductDetails productDetails){

        if (!userLocAvailable) {
            if (gpsAndNetworkChecker.isLocationPermissionGranted()) {
                if (gpsAndNetworkChecker.isGpsEnabled()) {

                    profileData.put("latitude", String.valueOf(gpsAndNetworkChecker.getUserLat()));
                    profileData.put("longitude", String.valueOf(gpsAndNetworkChecker.getUserLng()));

                    if (profileData.get("latitude").equals("0.0") |
                            profileData.get("longitude").equals("0.0")) {
                        Toast.makeText(context, "Please turn off your location once and turn it on again.", Toast.LENGTH_SHORT).show();
                    } else {

                        reference.child("profile").setValue(profileData);

                    }
                }
            }
        }
        else {
            if (addToFav.getTag().equals("fav")) {
                dbHandler.deleteProduct(Integer.parseInt(productDetails.getProductId()));
                addToFav.setImageResource(R.drawable.ic_favoritep);
                addToFav.setTag("n_fav");
                Toast.makeText(context, "Removed from favorites!", Toast.LENGTH_SHORT).show();
            } else if (addToFav.getTag().equals("n_fav")) {
                //create new product
                DraftsItems item = new DraftsItems(productDetails.getProductName(),
                        productDetails.getProductImage(), productDetails.getProductQuantity(),
                        productDetails.getMarketPrice(), productDetails.getProdcutKey(),
                        productDetails.getProductId());

                addToFav.setImageResource(R.drawable.ic_fav_filled);
                addToFav.setTag("fav");

                //add product to db
                dbHandler.addProduct(item);

                Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT).show();

            }
        }
    }


}
