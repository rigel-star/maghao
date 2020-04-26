package com.service.maghao.my_cart;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.service.maghao.ProceedToCheckoutActivity;
import com.service.maghao.R;
import com.service.maghao.adapter_classes.OrderedProductsAdapter;
import com.service.maghao.calculation_classes.TotalCostCalculator;
import com.service.maghao.important_classes.UserAccountId;
import com.service.maghao.model_classes.DraftsItems;
import com.service.maghao.model_classes.ListEmptyMessage;

import java.util.ArrayList;
import java.util.List;


public class OrderedCartFragment extends Fragment {


    public OrderedCartFragment() {
        // Required empty public constructor
    }


    //proceed
    private Button proceedToCheckOutBttn;
    //cost list
    List<Integer> costList = new ArrayList<>();
    //recycler view
    private RecyclerView recyclerView;
    //list
    private List<DraftsItems> itemsList;
    //adapter
    private OrderedProductsAdapter orderedItemsAdapter;
    //db reference
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    //user id
    private UserAccountId userAccountId = new UserAccountId();
    //list to add keys
    private List<String> orderKeyList = new ArrayList<>();
    //layout
    private LinearLayout emptyMsg;
    //empty msg
    private ListEmptyMessage listEmptyMessage = new ListEmptyMessage();
    //cost calculator
    private TotalCostCalculator totalCostCalculator = new TotalCostCalculator();
    //text views
    private TextView subTotal, deliveryCharge, finalTotal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ordered_cart, container, false);

        //init recycler view
        recyclerView = view.findViewById(R.id.ordered_recycler_view);
        //init list
        itemsList = new ArrayList<>();
        //init adapter
        orderedItemsAdapter = new OrderedProductsAdapter(getActivity(), itemsList, recyclerView);
        subTotal = view.findViewById(R.id.sub_total);
        deliveryCharge = view.findViewById(R.id.delivery_charge);
        finalTotal = view.findViewById(R.id.final_total);

        emptyMsg = view.findViewById(R.id.order_list_empty_msg);

        //process button
        proceedToCheckOutBttn = view.findViewById(R.id.proceed_button);

        proceedToCheckOutBttn.setOnClickListener(
                view1 -> {

                    if (itemsList.size() < 1)
                        Toast.makeText(getActivity(), "No orders!", Toast.LENGTH_SHORT).show();
                    else {

                        Intent intent = new Intent(getActivity(), ProceedToCheckoutActivity.class);
                        intent.putExtra("order_sum", finalTotal.getText().toString());
                        startActivity(intent);
                    }
                }
        );

        loadDataToRecyclerView();

        totalCostCalculator.subTotalTextView(subTotal);
        totalCostCalculator.finalTotalTextView(finalTotal);
        totalCostCalculator.deliveryChargeTextView(deliveryCharge);

        return view;
    }


    //loading ordered data of user from 'ordered' node from firebase database
    private void loadDataToRecyclerView(){

        reference.child("dataOnCart").child(userAccountId.getUserId()).child("orders").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        orderKeyList.add(dataSnapshot.getKey());

                        String name = dataSnapshot.child("name").getValue(String.class);
                        String image = dataSnapshot.child("image").getValue(String.class);
                        String qty = dataSnapshot.child("qty").getValue(String.class);
                        String price = dataSnapshot.child("price").getValue(String.class);
                        String id = dataSnapshot.child("id").getValue(String.class);

                        itemsList.add(new DraftsItems(name, image, qty, price, dataSnapshot.getKey(), id));

                        totalCostCalculator.calculateTotalCost(itemsList);

                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                        recyclerView.setAdapter(orderedItemsAdapter);

                        showEmptyListMsg();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        //return orderedItemsList.size();

    }

//    //item touch helper class helps if user swiped the item in the left or right direction
//    private ItemTouchHelper.SimpleCallback orderedTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
//
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//
//            return true;
//        }
//
//        @Override
//        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//            int position = viewHolder.getAdapterPosition();
//
//            //if user swiped left
//            //delete the item in the list
//            if (direction == ItemTouchHelper.LEFT){
//
//                reference.child("dataOnMap").child(userAccountId.getUserId()).child("ordered").child(orderKeyList.get(viewHolder.getAdapterPosition())).removeValue();
//
//                itemsList.remove(position);
//
//                ///Log.d(TAG, "keyOfProduct: " + orderKeyList.get(viewHolder.getAdapterPosition()));
//
//                toastMsg("Order canceled!");
//                orderedItemsAdapter.notifyDataSetChanged();
//
//            }
//        }
//
//        @Override
//        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder,
//                    dX, dY, actionState, isCurrentlyActive)
//                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.redColor))
//                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
//                    .create().decorate();
//        }
//    };

    //toast msg
    private void toastMsg(String msg){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.added_to_cart_msg, null);
        ImageView imageView = view.findViewById(R.id.added_to_cart_img);
        TextView textView = view.findViewById(R.id.added_to_cart_name);
        textView.setText(msg);
        imageView.setVisibility(View.GONE);
        Toast toast = new Toast(getActivity());
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void showEmptyListMsg(){

        listEmptyMessage.setEmptyMsgList(itemsList, emptyMsg);
        listEmptyMessage.showEmptyMsg();
    }

}
