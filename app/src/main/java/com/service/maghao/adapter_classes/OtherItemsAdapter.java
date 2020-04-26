package com.service.maghao.adapter_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.maghao.R;
import com.service.maghao.model_classes.CategoryItem;

import java.util.List;

public class OtherItemsAdapter extends RecyclerView.Adapter<OtherItemsAdapter.MyViewHolder>{


    private List<CategoryItem> categoryItemList;
    private Context context;

    OtherItemsAdapter(){}

    public OtherItemsAdapter(Context context, List<CategoryItem> categoryItemList){

        this.context = context;
        this.categoryItemList = categoryItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.category_view_main_screen, null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textView.setText(categoryItemList.get(position).getCategoryName());
        holder.imageView.setImageResource(categoryItemList.get(position).getCategoryImage());
    }

    @Override
    public int getItemCount() {
        return categoryItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{


        ImageView imageView;
        TextView textView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.category_image);
            textView = itemView.findViewById(R.id.category_name);
        }
    }
}
