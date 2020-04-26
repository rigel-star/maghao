package com.service.maghao.my_cart;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.service.maghao.MainActivity;
import com.service.maghao.R;
import com.service.maghao.adapter_classes.FavoritesProductAdapter;
import com.service.maghao.important_classes.UserAccountId;
import com.service.maghao.model_classes.DraftsItems;
import com.service.maghao.model_classes.ListEmptyMessage;
import com.service.maghao.sqlite_database.DatabaseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class FavoritesCartFragment extends Fragment {


    public FavoritesCartFragment() {
        // Required empty public constructor
    }

    //db handler
    DatabaseHandler dbHandler;
    //recycler view
    private RecyclerView recyclerView;
    //list
    private List<DraftsItems> itemsList;
    //adapter
    private FavoritesProductAdapter favoritesProductAdapter;
    //db reference
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    //user id
    private UserAccountId userAccountId = new UserAccountId();
    //list to add keys
    private List<String> keyList = new ArrayList<>();
    //list empty msg
    private ListEmptyMessage listEmptyMessage = new ListEmptyMessage();
    //layout
    private LinearLayout emptyMsg;
    //order all button
    private Button orderAllbttn, buyMoreBttn;

    private static final String TAG = "DraftFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_draft_cart, container, false);

        //init db handler
        dbHandler = new DatabaseHandler(getActivity());

        //order all
        orderAllbttn = view.findViewById(R.id.draft_cart_order_all_btn);

        //buy more
        buyMoreBttn = view.findViewById(R.id.draft_cart_buy_more_btn);

        //init recycler view
        recyclerView = view.findViewById(R.id.drafts_recycler_view);

        //init list, db handler getAllItems returns the draft item
        // list so we can add that method to this list
        itemsList = dbHandler.getAllItems();

        for (DraftsItems i: dbHandler.getAllItems())
            Log.d("FAV_LIST", "favListLoaded: " + i.getName());

        //init adapter
        favoritesProductAdapter = new FavoritesProductAdapter(getActivity(), itemsList, recyclerView);

        emptyMsg = view.findViewById(R.id.list_empty_msg_draft);

        listEmptyMessage.setEmptyMsgList(itemsList, emptyMsg);
        listEmptyMessage.showEmptyMsg();

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(favoritesProductAdapter);

        showEmptyListMsg();

        //bttn which orders every item in the draft list one by one
        orderAllbttn.setOnClickListener(
                v -> {
                    //method that order all product from draft list : taking list as a parameter
                    orderAllProducts(itemsList);
                }
        );

        buyMoreBttn.setOnClickListener(
                v -> {
                    getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                }
        );

        return view;
    }

    //public product
    private void publicProduct(Map<String, String> data, int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Confirm order?").setTitle("Your order will be visible to nearby store keepers");
        builder.setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        reference.child("dataOnMap").child(userAccountId.getUserId()).child("ordered").push().setValue(data);

                        reference.child("dataOnMap").child(userAccountId.getUserId()).child("drafts").child(keyList.get(position)).removeValue();

                        itemsList.remove(position);

                        Log.d(TAG, "productName: " + data.get("name"));

                        toastMsg("Product ordered!");
                        favoritesProductAdapter.notifyDataSetChanged();

                    }
                });

        builder.create().show();
    }

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


    //order all products from drafts list...
    private void orderAllProducts(List<DraftsItems> list){

        if (list.size() < 1) {
            Toast.makeText(getActivity(), "Draft cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> data = new HashMap<>();

        for (int i=list.size();i>0;i--){

            DraftsItems items = list.get(i-1);

            data.put("name", items.getName());
            data.put("image", items.getImage());
            data.put("price", items.getPrice());
            data.put("qty", items.getQty());

            reference.child("dataOnCart").child(userAccountId.getUserId())
                    .child("orders").push().setValue(data);

            data.clear();

            Toast.makeText(getActivity(), "All products ordered!", Toast.LENGTH_SHORT).show();

        }

    }

}
