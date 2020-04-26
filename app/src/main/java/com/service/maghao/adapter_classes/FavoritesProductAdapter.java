package com.service.maghao.adapter_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.service.maghao.R;
import com.service.maghao.important_classes.UserAccountId;
import com.service.maghao.model_classes.DraftsItems;
import com.service.maghao.sqlite_database.DatabaseHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesProductAdapter extends RecyclerView.Adapter<FavoritesProductAdapter.MyViewHolder> implements Runnable {

    public FavoritesProductAdapter(){}

    private Context context;
    private List<DraftsItems> draftsItemsList;
    private RecyclerView recyclerView;

    //key list
    private List<String> keyList = new ArrayList<>();

    private UserAccountId userAccountId = new UserAccountId();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    //cons
    public FavoritesProductAdapter(Context context, List<DraftsItems> draftsItemsList, RecyclerView recyclerView){
        this.context = context;
        this.draftsItemsList = draftsItemsList;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.draft_product_card, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DraftsItems item = draftsItemsList.get(position);

        keyList.add(item.getProductKey());

        holder.productName.setText(item.getName());
        holder.productQty.setText(item.getQty());
        holder.productCost.setText("Rs. " + item.getPrice());

        Picasso.get().load(item.getImage()).into(holder.productImg);

        holder.orderBtn.setOnClickListener(
                v -> {
                    publishProduct(position, item.getName(), item.getImage(), item.getQty(), item.getPrice());
                }
        );

        holder.delBttn.setOnClickListener(
                v -> deleteProduct(position, Integer.parseInt(item.getId()))
        );
    }

    @Override
    public int getItemCount() {
        return draftsItemsList.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder{

        //components
        ImageView productImg, delBttn;
        TextView productName, productQty, productCost;
        Button orderBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //init components
            productImg = itemView.findViewById(R.id.draft_product_img);
            delBttn = itemView.findViewById(R.id.draft_product_del_bttn);
            productName = itemView.findViewById(R.id.draft_product_name);
            productQty = itemView.findViewById(R.id.draft_product_qty);
            productCost = itemView.findViewById(R.id.draft_product_cost);
            orderBtn = itemView.findViewById(R.id.draft_product_order_btn);
        }
    }

    private void publishProduct(int position, String name, String image, String qty, String price){

        Map<String, String> data = new HashMap<>();

        data.put("name", name);
        data.put("image", image);
        data.put("qty", qty);
        data.put("price", price);

        reference.child("dataOnCart").child(userAccountId.getUserId()).child("orders").push().setValue(data);

        animate(position, recyclerView);

        toastMsg("Product ordered!");

    }

    //toast msg
    private void toastMsg(String msg){

        View view = LayoutInflater.from(context).inflate(R.layout.added_to_cart_msg, null);
        ImageView imageView = view.findViewById(R.id.added_to_cart_img);
        TextView textView = view.findViewById(R.id.added_to_cart_name);
        textView.setText(msg);
        imageView.setVisibility(View.GONE);
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    private void deleteProduct(int position, int id){

        DatabaseHandler dbHandler = new DatabaseHandler(context);
        dbHandler.deleteProduct(id);

        toastMsg("Removed from favorites!");
        recyclerView.removeViewAt(position);

    }

    private void animate(int position, RecyclerView view){

        View v = view.getChildAt(position);

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
        animation.setDuration(800);
        v.startAnimation(animation);

    }


    //runnable thread method: run()
    @Override
    public void run() {

    }

}


