package com.service.maghao.fragment_classes;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.service.maghao.R;
import com.service.maghao.adapter_classes.SettingOptionsAdapter;
import com.service.maghao.model_classes.SettingOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    private RecyclerView settingsRecyclerView;
    private SettingOptionsAdapter settingsOptionsAdapter;
    private List<SettingOptions> settingOptionsList;

    //to load user profile data
    private FirebaseFirestore firebaseDatabase;
    private CollectionReference cReference;

    //widgets
    TextView userProfileName, userProfileContact;
    ImageView userProfileImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsRecyclerView = view.findViewById(R.id.settings_recycler_view);

        userProfileName = view.findViewById(R.id.user_name_on_settings);
        userProfileContact = view.findViewById(R.id.user_contact_on_settings);

        userProfileImage = view.findViewById(R.id.user_profile_on_settings);

        settingOptionsList = new ArrayList<>();

        firebaseDatabase = FirebaseFirestore.getInstance();
        cReference = firebaseDatabase.collection("Users");

        addOptionsToSettingsList();

        settingsOptionsAdapter = new SettingOptionsAdapter(getActivity(), settingOptionsList);

        settingsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        settingsRecyclerView.setAdapter(settingsOptionsAdapter);

        loadUserData();

        return view;
    }

    //options list
    private void addOptionsToSettingsList(){

        settingOptionsList.add(new SettingOptions("Help", R.raw.setting_option_help));
        settingOptionsList.add(new SettingOptions("Policy", R.raw.setting_option_policy));
        settingOptionsList.add(new SettingOptions("About Us", R.raw.icon_about_us));
        settingOptionsList.add(new SettingOptions("Logout", R.raw.setting_option_logout));
    }

    //load user data
    private void loadUserData(){

        cReference.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("profileData").get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()){

                                    for (QueryDocumentSnapshot qSnapshot: task.getResult()){

                                        String name = qSnapshot.getString("name");
                                        String number = qSnapshot.getString("number");
                                        String profile = qSnapshot.getString("profile");

                                        userProfileName.setText(name);
                                        userProfileContact.setText(number);
                                        Picasso.get().load(profile).into(userProfileImage);
                                    }
                                }
                            }
                        }
                );

    }

}
