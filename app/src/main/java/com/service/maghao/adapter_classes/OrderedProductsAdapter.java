package com.service.maghao.adapter_classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.service.maghao.R;
import com.service.maghao.important_classes.UserAccountId;
import com.service.maghao.model_classes.DraftsItems;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderedProductsAdapter extends RecyclerView.Adapter<OrderedProductsAdapter.MyViewHolder>{


    private List<DraftsItems> categoryItemList;
    private Context context;
    private RecyclerView recyclerView;

    //user acc id
    UserAccountId userAccountId = new UserAccountId();

    //database reference
    DatabaseReference reference =
            FirebaseDatabase.getInstance().getReference("dataOnCart").child(userAccountId.getUserId());


    OrderedProductsAdapter(){}

    public OrderedProductsAdapter(Context context, List<DraftsItems> categoryItemList, RecyclerView recyclerView){
        this.recyclerView = recyclerView;
        this.context = context;
        this.categoryItemList = categoryItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.my_cart_recycler_views_card, null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textView.setText(categoryItemList.get(position).getName());
        holder.qtyView.setText(categoryItemList.get(position).getQty());
        holder.priceView.setText("Rs. " + categoryItemList.get(position).getPrice());

        String quantity = findProductQtyAndPrice(holder.qtyView.getText().toString(), holder.priceView.getText().toString())[0];

        holder.decIncTextView.setText(quantity);

        Picasso.get().load(categoryItemList.get(position).getImage()).into(holder.imageView);

        //increase button click
        holder.increase.setOnClickListener(
                view -> {
                    incDecProductQty(00, holder.priceView, holder.decIncTextView, holder.qtyView,
                            categoryItemList.get(position).getProductKey(),
                            categoryItemList.get(position).getName(),
                            categoryItemList.get(position).getImage());
                }
        );

        //decrease button click
        holder.decrease.setOnClickListener(
                view -> {

                    if (Integer.parseInt(holder.decIncTextView.getText().toString()) == 1)
                        Toast.makeText(context, "Quantity can't be less than 1", Toast.LENGTH_SHORT).show();
                    else
                    incDecProductQty(01, holder.priceView, holder.decIncTextView, holder.qtyView,
                            categoryItemList.get(position).getProductKey(),
                            categoryItemList.get(position).getName(),
                            categoryItemList.get(position).getImage());
                }
        );

        holder.delete.setOnClickListener(
                view -> {
                    deleteProduct(position, categoryItemList.get(position).getName());
                }
        );

    }

    @Override
    public int getItemCount() {
        return categoryItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{


        ImageView imageView;
        TextView textView, qtyView, priceView, decIncTextView;
        Button decrease, increase;
        ImageView delete;
        LinearLayout incDecLayout;
        ImageView state;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.drafts_ordered_image_view);
            textView = itemView.findViewById(R.id.drafts_ordered_text_view);
            qtyView = itemView.findViewById(R.id.drafts_ordered_quantity_view);
            priceView = itemView.findViewById(R.id.drafts_ordered_price_view);

            decIncTextView = itemView.findViewById(R.id.decrease_increase_quantity_text_view);
            decrease = itemView.findViewById(R.id.decrease_product_quantity_button);
            increase = itemView.findViewById(R.id.increase_product_quantity_button);
            delete = itemView.findViewById(R.id.del_ordered_product);
            state = itemView.findViewById(R.id.p_state);

            //layout
            incDecLayout = itemView.findViewById(R.id.inc_dec_linear_layout);

        }
    }

    //calculate product quantity
    private String[] findProductQtyAndPrice(String qty, String price){

        String[] finalResult = new String[2];

        finalResult[0] = qty.split(" ")[0];
        finalResult[1] = String.valueOf(price.charAt(1));


        return finalResult;
    }

    //increase and decrease of product qty
    private void incDecProductQty(int CLICK_CODE, TextView priceView, TextView qtyView,
                                  TextView qtyView2, String key, String name, String image){

        final int[] qty = {Integer.parseInt(qtyView.getText().toString())};
        String[] splitQty = qtyView2.getText().toString().split(" ");

        String[] splitPrice = priceView.getText().toString().split(" ");

        //get they actual price of product by dividing available qty with available price
        int actualPrice = Integer.parseInt(String.valueOf(splitPrice[1])) /
                Integer.parseInt(qtyView.getText().toString());

        //increasing price of qty on users button clicks
        int increasePrice = Integer.parseInt(splitPrice[1]) + actualPrice;

        //decresing price of qty on users button clicks
        int decreasePrice = Integer.parseInt(splitPrice[1]) - actualPrice;

        switch (CLICK_CODE){

            case 00:
                qty[0]++;
                priceView.setText("Rs. " + increasePrice);
                qtyView.setText(String.valueOf(qty[0]));
                qtyView2.setText(qty[0] + " " + splitQty[1]);
                updateDataChangeToFirebase(key, priceUpdate(priceView.getText().toString()), qtyView2.getText().toString(), name, image);
                break;

            case 01:
                qty[0]--;
                priceView.setText("Rs. " + decreasePrice);
                qtyView.setText(String.valueOf(qty[0]));
                qtyView2.setText(qty[0] + " " + splitQty[1]);
                updateDataChangeToFirebase(key, priceUpdate(priceView.getText().toString()), qtyView2.getText().toString(), name, image);
                break;
        }

    }

    private String priceUpdate(String price){

        String[] split = price.split(" ");

        return split[1];
    }

    /*
    * if user increases or decreases product qty from his/her ordered list
    * i have to update data on firebase
    * everytime he/she updates in app change the data in firebase
    * */
    private void updateDataChangeToFirebase(String key, String price, String qty, String name, String image){

        Map<String, String> updatedData = new HashMap<>();

        updatedData.put("price", price);
        updatedData.put("qty", qty);
        updatedData.put("image", image);
        updatedData.put("name", name);

        Map<String, Object> keyUpdate = new HashMap<>();

        keyUpdate.put(key, updatedData);

        reference.child("orders").updateChildren(keyUpdate);

    }

    private void deleteProduct(int position, String name){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete product: " + name + "?");

        builder.setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reference.child("orders").child(categoryItemList.get(position).getProductKey()).removeValue();
                    }
                });

        builder.create().show();

        recyclerView.removeViewAt(position);
        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();

    }
}
