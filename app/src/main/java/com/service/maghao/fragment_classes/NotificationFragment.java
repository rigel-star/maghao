package com.service.maghao.fragment_classes;



import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.service.maghao.R;
import com.service.maghao.adapter_classes.NotificationsAdapter;
import com.service.maghao.model_classes.Notifications;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {


    public NotificationFragment() {
    }

    NotificationManagerCompat compat;

    private RecyclerView myRecyclerView;
    private NotificationsAdapter notificationsAdapter;
    private List<Notifications> notificationsList;

    Button sendNotif;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        compat = NotificationManagerCompat.from(getActivity());

        notificationsList = new ArrayList<>();
        sendNotif = view.findViewById(R.id.send_notif_bttn);

        addNotificationsToList();

        myRecyclerView = view.findViewById(R.id.notification_recycler_view);

        notificationsAdapter = new NotificationsAdapter(getActivity(), notificationsList);


        myRecyclerView.setAdapter(notificationsAdapter);
        myRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        return view;
    }

    //add notifs
    private void addNotificationsToList(){

        notificationsList.add(new Notifications(R.raw.icon_discount_tag,
                "Ramesh Goods Supplier is offering heavy\n" +
                        "discount on Beverage Products from today to next 5 days. Don't miss!",
                "Dec 25, 10:02 AM"));

        notificationsList.add(new Notifications(R.raw.icon_special_offer,
                "Order products worth of Rs 1000 this new year and get special offers to win money and other prizes.",
                "Dec 28, 11:52 AM"));

        notificationsList.add(new Notifications(R.raw.icon_discount_tag,
                "I-Kalu Meat Shop offering 20% discount on Mutton from 1st to 10th January as it's new year.",
                "Dec 28, 05:17 PM"));
    }


}
