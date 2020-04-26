package com.service.maghao.important_classes;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.service.maghao.R;

public class PushNotification extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        generateNotification(remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody());
    }

    public void generateNotification( String title, String message){

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "notificationForEveryOs")
                        .setContentTitle(title)
                        .setSmallIcon(R.drawable.app_logo)
                        .setAutoCancel(true)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(777, builder.build());

    }
}
