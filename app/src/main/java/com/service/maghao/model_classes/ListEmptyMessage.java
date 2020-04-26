package com.service.maghao.model_classes;

import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

public class ListEmptyMessage {


    private List<DraftsItems> draftsItemsList;
    private LinearLayout msgLayout;

    public ListEmptyMessage(){}

    public void setEmptyMsgList(List<DraftsItems> draftsItemsList, LinearLayout msgLayout){
        this.draftsItemsList = draftsItemsList;
        this.msgLayout = msgLayout;
    }

    public void showEmptyMsg(){
        if (draftsItemsList.size() < 1){
            msgLayout.setVisibility(View.VISIBLE);
        }
        else
            msgLayout.setVisibility(View.GONE);
    }

}
