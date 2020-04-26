package com.service.maghao.important_classes;

import com.google.firebase.auth.FirebaseAuth;

public class UserAccountId {

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public String getUserId(){

        return userId;
    }

}
