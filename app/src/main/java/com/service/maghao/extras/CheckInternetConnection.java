package com.service.maghao.extras;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.service.maghao.R;

public class CheckInternetConnection {

    private Context context;
    private ConnectivityManager cm;

    public CheckInternetConnection(Context context){
        this.context = context;
    }

    public boolean isNetworkConnected(){
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public boolean isConnectionMobileData(){

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            return true;

        return false;
    }

    public void showNoInternetMsg() {
        //check whether the device is connected to network
        View view = LayoutInflater.from(context).inflate(R.layout.warning_layout, null);

        ImageView icon = view.findViewById(R.id.warning_icon);
        TextView msg = view.findViewById(R.id.warning_msg);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        icon.setImageResource(R.raw.no_internet_con);
        msg.setText("No Internet Connection");

        builder.setView(view);
        builder.create();

        AlertDialog dialog = builder.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
