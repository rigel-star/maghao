package com.service.maghao.adapter_classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.service.maghao.MyCartActivity;
import com.service.maghao.R;
import com.service.maghao.model_classes.CategoryItem;
import com.service.maghao.model_classes.ProductDetails;
import java.util.ArrayList;
import java.util.List;

public class ProductsCategoryAdapter extends RecyclerView.Adapter<ProductsCategoryAdapter.MyViewHolder>{


    //category list to pass manually
    private String[] categoryList = {"agroProducts", "groceryProducts", "bakeryProducts", "beveragesProducts",
    "nonVegProducts", "fruitProducts"};
    private String[] categoryName = {"Veg", "Grocery", "Bakery", "Beverages", "Non Veg", "Fruits"};

    //list of all data products
    private List<ProductDetails> productDetails, productDetailsFiltered;
    SearchView searchView;

    private static final String TAG = "PickUp";

    private List<CategoryItem> categoryItemList;
    private Context context;

    //data
    private RecyclerView recyclerView;

    //adapter
    private PickUpItemsAdapter[] itemsAdapter;


    ProductsCategoryAdapter(){}

    public ProductsCategoryAdapter(Context context, List<CategoryItem> categoryItemList){

        this.context = context;
        this.categoryItemList = categoryItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.category_view_main_screen, null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.textView.setText(categoryItemList.get(position).getCategoryName());
        holder.imageView.setImageResource(categoryItemList.get(position).getCategoryImage());

        holder.linearLayout.setOnClickListener(
                view -> pickUpProductsList(categoryList[position], categoryName[position])
        );
    }

    @Override
    public int getItemCount() {
        return categoryItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{


        ImageView imageView;
        TextView textView;
        LinearLayout linearLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.category_image);
            textView = itemView.findViewById(R.id.category_name);
            linearLayout = itemView.findViewById(R.id.particular_items_linear_layout);
        }
    }


    private void pickUpProductsList(String category, String type){


        final View view = LayoutInflater.from(context).inflate(R.layout.products_pickup_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);

        recyclerView = view.findViewById(R.id.products_pickup_recycler_view);
        FloatingActionButton cartbttn = view.findViewById(R.id.products_pickup_cart_bttn);

        final ProgressBar progressBar = view.findViewById(R.id.progressBar);

        //close button
        ImageView closeButton = view.findViewById(R.id.products_pickup_close_button);
        ImageView addExtraProductButton = view.findViewById(R.id.add_extra_product_button);
        TextView categoryName = view.findViewById(R.id.category_product_name);

        categoryName.setText(type);
        addExtraProductButton.setOnClickListener(
                view12 -> {
                    Toast.makeText(context, "Add extra", Toast.LENGTH_SHORT).show();
                }
        );

        cartbttn.setOnClickListener(
                view13 -> {
                    context.startActivity(new Intent(context, MyCartActivity.class));
                }
        );

        progressBar.setVisibility(View.VISIBLE);

        //firebase firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        //document reference
        CollectionReference cReference = database.collection(category);

        //adapter
        itemsAdapter = new PickUpItemsAdapter[1];


        //list
        productDetails = new ArrayList<>();
        productDetailsFiltered = new ArrayList<>();

        searchView = view.findViewById(R.id.pickup_layout_product_search);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {

                        itemsAdapter[0].getFilter().filter(s);
                        return true;
                    }
                }
        );

        //firestore reference
        cReference.get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot qSnap : task.getResult()) {

                                    String quantity = qSnap.getString("quantity");
                                    String marketPrice = qSnap.getString("marketPrice");
                                    String name = qSnap.getString("name");
                                    String image = qSnap.getString("image");
                                    String key = qSnap.getId();
                                    String id = qSnap.getString("id");

                                    Log.d(TAG, "onDataFetched: Name: " + name + " Image: " + image);
                                    productDetails.add(new ProductDetails(name, image, marketPrice, quantity, key, id));
                                    productDetailsFiltered.add(new ProductDetails(name, image, marketPrice, quantity, key, id));


                                }

                                itemsAdapter[0] = new PickUpItemsAdapter(context, productDetails);

                                //LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

                                recyclerView.setLayoutManager(new GridLayoutManager(context, 1));

                                recyclerView.setAdapter(itemsAdapter[0]);

                                progressBar.setVisibility(View.GONE);

                            }

                        }

                    }
                }
        );

        builder.setView(view);

        final AlertDialog dialog = builder.create();

        //dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        closeButton.setOnClickListener(
                view1 -> dialog.dismiss()
        );

        dialog.show();

    }
}
