package com.service.maghao.adapter_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.maghao.R;
import com.service.maghao.model_classes.OfferBadge;

import java.util.List;

public class OffersBadgeAdapter extends RecyclerView.Adapter<OffersBadgeAdapter.MyViewHolder>{


    List<OfferBadge> badgeList;
    Context context;

    public OffersBadgeAdapter(Context context, List<OfferBadge> badgeList) {
        this.badgeList = badgeList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.offers_card_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.offerBadge.setImageResource(badgeList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return badgeList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView offerBadge;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            offerBadge = itemView.findViewById(R.id.offers_badge);
        }
    }
}
