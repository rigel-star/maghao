package com.service.maghao.fragment_classes;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.service.maghao.MyCartActivity;
import com.service.maghao.R;
import com.service.maghao.adapter_classes.ProductsCategoryAdapter;
import com.service.maghao.adapter_classes.OffersBadgeAdapter;
import com.service.maghao.adapter_classes.OtherItemsAdapter;
import com.service.maghao.adapter_classes.ServicesItemsAdapter;
import com.service.maghao.extras.CartListSizes;
import com.service.maghao.model_classes.CategoryItem;
import com.service.maghao.model_classes.OfferBadge;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    //floating action button
    private ImageView cartFloatingButton;
    //view more button
    private Button viewMoreServices;
    //click code
    int CLICK_CODE = 00;

    //list sizes
    CartListSizes listSizes;

    //adapters
    private ProductsCategoryAdapter categoryItemsAdapter;
    private ServicesItemsAdapter emergencyItemsAdapter;
    private OtherItemsAdapter otherItemsAdapter;
    private OffersBadgeAdapter offersBadgeAdapter;

    //recycler views
    private RecyclerView homeRecyclerView, emergencyRecyclerView, othersRecyclerView, offersRecyclerView;

    //lists
    private List<CategoryItem> emergencyItemsList;
    private List<CategoryItem> categoryItemList;
    private List<CategoryItem> othersItemList;
    private List<OfferBadge> offerBadgeList;

    //ListSizes listSizes = new ListSizes();

    private TextView listSize;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, container, false);

        listSizes = new CartListSizes();

        listSize = view.findViewById(R.id.cart_list_size_home_fragment);
        viewMoreServices = view.findViewById(R.id.view_more_bttn_service);

        categoryItemList = new ArrayList<>();
        emergencyItemsList = new ArrayList<>();
        othersItemList = new ArrayList<>();
        offerBadgeList = new ArrayList<>();

        addDataToList();

        cartFloatingButton = view.findViewById(R.id.cart_floating_button);


        new Thread(new Runnable() {
            @Override
            public void run() {

                listSizes.setSizeTextView(listSize);
                listSizes.loadOrderList();

            }
        }).start();

        //button method
        onButtonClicks();

        categoryItemsAdapter = new ProductsCategoryAdapter(getActivity(), categoryItemList);
        emergencyItemsAdapter = new ServicesItemsAdapter(getActivity(), emergencyItemsList);
        otherItemsAdapter = new OtherItemsAdapter(getActivity(), othersItemList);
        offersBadgeAdapter = new OffersBadgeAdapter(getActivity(), offerBadgeList);

        homeRecyclerView = view.findViewById(R.id.home_page_recycler_view);
        emergencyRecyclerView = view.findViewById(R.id.emergency_recycler_view);
        othersRecyclerView = view.findViewById(R.id.others_recycler_view);
        offersRecyclerView = view.findViewById(R.id.offers_recycler_view_home_screen);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //LinearLayoutManager servicesLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //LinearLayoutManager olm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager offersLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        GridLayoutManager productsLayoutManager = new GridLayoutManager(getActivity(), 4);
        GridLayoutManager serviceLayoutManager = new GridLayoutManager(getActivity(), 4);
        GridLayoutManager othersLayoutManager = new GridLayoutManager(getActivity(), 4);

        homeRecyclerView.setLayoutManager(productsLayoutManager);
        emergencyRecyclerView.setLayoutManager(serviceLayoutManager);
        othersRecyclerView.setLayoutManager(othersLayoutManager);
        offersRecyclerView.setLayoutManager(offersLayoutManager);

        homeRecyclerView.setAdapter(categoryItemsAdapter);
        emergencyRecyclerView.setAdapter(emergencyItemsAdapter);
        othersRecyclerView.setAdapter(otherItemsAdapter);
        offersRecyclerView.setAdapter(offersBadgeAdapter);


        return view;
    }


    //on button clicks
    private void onButtonClicks(){

        cartFloatingButton.setOnClickListener(
                view -> startActivity(new Intent(getActivity(), MyCartActivity.class)));

        viewMoreServices.setOnClickListener(
                view -> {
                    hideAndShow();
                }
        );
    }

    //adding categorices
    private void addDataToList(){


        //home items
        categoryItemList.add(new CategoryItem("Veg", R.raw.icon_veg));
        categoryItemList.add(new CategoryItem("Grocery", R.raw.grocery_products));
        categoryItemList.add(new CategoryItem("Bakery", R.raw.icon_milk));
        categoryItemList.add(new CategoryItem("Beverages", R.raw.icon_beverages));
        categoryItemList.add(new CategoryItem("Non Veg", R.raw.icon_non_veg));
        categoryItemList.add(new CategoryItem("Fruits", R.raw.fruits_icon));

        //services items
        emergencyItemsList.add(new CategoryItem("Doctor", R.raw.icon_doctor));
        emergencyItemsList.add(new CategoryItem("Electrician", R.raw.icon_electrician));
        emergencyItemsList.add(new CategoryItem("Tailor", R.raw.icon_tailor));
        emergencyItemsList.add(new CategoryItem("Plumber", R.raw.icon_plumber));
        emergencyItemsList.add(new CategoryItem("Barber", R.raw.icon_salon));
        emergencyItemsList.add(new CategoryItem("Teacher", R.raw.icon_teacher));
        emergencyItemsList.add(new CategoryItem("Mechanic", R.raw.icon_mechanics));
        emergencyItemsList.add(new CategoryItem("Painter", R.raw.icon_painter));

        //others
        othersItemList.add(new CategoryItem("Oil & Gas", R.raw.icon_oil));
        othersItemList.add(new CategoryItem("Hire", R.raw.icon_hire));
        othersItemList.add(new CategoryItem("Book", R.raw.icon_book));
        othersItemList.add(new CategoryItem("Emergency", R.raw.icon_emergency));


        offerBadgeList.add(new OfferBadge(R.drawable.offer_one));
        offerBadgeList.add(new OfferBadge(R.drawable.offer_two));
        offerBadgeList.add(new OfferBadge(R.drawable.offer_three));


    }

    private void hideAndShow(){
        switch (CLICK_CODE){

            case 00:
                viewMoreServices.setText("View less");
                emergencyItemsList.add(new CategoryItem("Engineer", R.raw.icon_engineer));
                categoryItemsAdapter.notifyDataSetChanged();
                CLICK_CODE = 01;
                break;

            case 01:
                viewMoreServices.setText("View more");
                emergencyItemsList.remove(8);
                categoryItemsAdapter.notifyDataSetChanged();
                CLICK_CODE = 00;
                break;
        }
    }

}
