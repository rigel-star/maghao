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
import com.service.maghao.model_classes.Notifications;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>{


    private List<Notifications> notificationsList;
    private Context context;

    public NotificationsAdapter(){}

    public NotificationsAdapter(Context context, List<Notifications> notificationsList) {
        this.notificationsList = notificationsList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.notification_card, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.icon.setImageResource(notificationsList.get(position).getIcon());
        holder.notifDesc.setText(notificationsList.get(position).getNotifDesc());
        holder.date.setText(notificationsList.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView notifDesc, date;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.notification_icon);
            notifDesc = itemView.findViewById(R.id.notification_offer_description);
            date = itemView.findViewById(R.id.notification_date);

        }
    }
}
