package com.service.maghao.adapter_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.maghao.R;
import com.service.maghao.model_classes.Request;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.MyViewHolder> {

    public RequestsAdapter(){}

    List<Request> requestList;
    Context context;

    public RequestsAdapter(Context context, List<Request> requestList){
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.request_card, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Request request = requestList.get(position);

        holder.type.setText(request.getType());
        Picasso.get().load(request.getImg()).into(holder.img);

        holder.cancel.setOnClickListener(
                v -> {
                    Toast.makeText(context, request.getType(), Toast.LENGTH_SHORT).show();
                }
        );

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img;
        TextView type;
        Button cancel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.request_card_img);
            type = itemView.findViewById(R.id.request_card_name);
            cancel = itemView.findViewById(R.id.request_card_cancel_bttn);
        }
    }
}
