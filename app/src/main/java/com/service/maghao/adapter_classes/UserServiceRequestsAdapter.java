package com.service.maghao.adapter_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.maghao.R;
import com.service.maghao.model_classes.DraftsItems;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserServiceRequestsAdapter extends RecyclerView.Adapter<UserServiceRequestsAdapter.MyViewHolder>{

    private List<DraftsItems> requestList;
    private Context context;

    UserServiceRequestsAdapter(){}

    public UserServiceRequestsAdapter(Context context, List<DraftsItems> requestList) {
        this.requestList = requestList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.my_cart_recycler_views_card, null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //setting qty and price field invisible on service list
        holder.price.setVisibility(View.GONE);
        holder.qty.setVisibility(View.GONE);

        holder.name.setText(requestList.get(position).getName());
        Picasso.get().load(requestList.get(position).getImage()).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name, qty, price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.drafts_ordered_image_view);
            name = itemView.findViewById(R.id.drafts_ordered_text_view);
            qty = itemView.findViewById(R.id.drafts_ordered_quantity_view);
            price = itemView.findViewById(R.id.drafts_ordered_price_view);

        }
    }
}
