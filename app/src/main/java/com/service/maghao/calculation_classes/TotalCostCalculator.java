package com.service.maghao.calculation_classes;

import android.util.Log;
import android.widget.TextView;

import com.service.maghao.model_classes.DraftsItems;
import com.service.maghao.model_classes.OrderedItems;

import java.util.List;

public class TotalCostCalculator {


    //my cart activity will pass textview and list of cost to this class
    private TextView subTotalTextView;
    private TextView totalCostView, deliveryView, mapCostView;


    //calculate cost for my cart activity
    public int calculateTotalCost(List<DraftsItems> draftsItems){

        int subTotal = 0, total = 0;

        for (int i=0;i<draftsItems.size();i++){
            subTotal += Integer.parseInt(draftsItems.get(i).getPrice());
        }

        Log.d("TotalCost", String.valueOf(subTotal));

        subTotalTextView.setText(String.valueOf("Rs. " + subTotal));

        if (subTotal >= 500){
            deliveryView.setText("FREE");
            totalCostView.setText("Rs. " + subTotal);
        }else {
            total = subTotal+59;
            totalCostView.setText("Rs. " + total);
        }

        return total;

    }

    //passing total cost view from my cart activity
    public void subTotalTextView(TextView costView){
        this.subTotalTextView = costView;
    }
    public void finalTotalTextView(TextView totalView){this.totalCostView = totalView;}
    public void deliveryChargeTextView(TextView deliveryView){this.deliveryView = deliveryView;}
    public void totalCostViewMap(TextView costView){this.mapCostView = costView;}


    public int calculateCostInMapsActivity(List<OrderedItems> list){

        int total = 0;

        for(int i=0;i<list.size();i++){

            int cost = 0;
            cost = Integer.parseInt(list.get(i).getCost());

            total += cost;
        }

        mapCostView.setText("Total cost: Rs. " + total);

        return total;
    }

}
