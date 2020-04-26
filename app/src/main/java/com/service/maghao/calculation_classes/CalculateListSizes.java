package com.service.maghao.calculation_classes;

import com.service.maghao.model_classes.DraftsItems;

import java.util.ArrayList;
import java.util.List;

public class CalculateListSizes {

    List<DraftsItems> list = new ArrayList<>();

    //ordered list size
    public void setOrderedListSize(List<DraftsItems> list){
        this.list = list;
    }
    public int getOrderedListSize(){
        return list.size();
    }
}
