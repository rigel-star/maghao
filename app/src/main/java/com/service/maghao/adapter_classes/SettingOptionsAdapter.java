package com.service.maghao.adapter_classes;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.service.maghao.EditProfileActivity;
import com.service.maghao.R;
import com.service.maghao.model_classes.SettingOptions;

import java.util.List;

public class SettingOptionsAdapter extends RecyclerView.Adapter<SettingOptionsAdapter.MyViewHolder>{

    private List<SettingOptions> settingOptionsList;
    private Context context;

    public static final String TAG = "SettingActivity";

    public SettingOptionsAdapter(){}

    public SettingOptionsAdapter(Context context, List<SettingOptions> settingOptionsList) {
        this.settingOptionsList = settingOptionsList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.settings_option_card, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.settingOptionName.setText(settingOptionsList.get(position).getOptionName());
        holder.settingOptionIcon.setImageResource(settingOptionsList.get(position).getOptionIcon());


        Pair[] pairs = new Pair[2];

        pairs[0] = new Pair<View, String>(holder.settingOptionIcon, "imageTrans");
        pairs[1] = new Pair<View, String>(holder.settingOptionName, "nameTrans");

        holder.settingOptionLayout.setOnClickListener(
                view -> {

                    if (position == 0){

                    }

                    //user sign out
                    if (position == 3){
                        try {
                            Toast.makeText(context, "Logging out!", Toast.LENGTH_SHORT).show();
                            Thread.sleep(2000);
                            FirebaseAuth.getInstance().signOut();

                            Intent intent = new Intent(context, EditProfileActivity.class);

                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            context.startActivity(intent);

                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }

                }
        );
    }

    @Override
    public int getItemCount() {
        return settingOptionsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        LinearLayout settingOptionLayout;
        ImageView settingOptionIcon;
        TextView settingOptionName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            settingOptionIcon = itemView.findViewById(R.id.setting_option_icon);
            settingOptionName = itemView.findViewById(R.id.setting_option_name);
            settingOptionLayout = itemView.findViewById(R.id.setting_option_linear_layout);

        }
    }
}
