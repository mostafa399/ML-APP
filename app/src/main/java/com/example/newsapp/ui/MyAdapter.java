package com.example.newsapp.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.example.newsapp.model.Journel;
import com.google.android.gms.common.util.DataUtils;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<Journel> journelArrayList;

    public MyAdapter(Context context, ArrayList<Journel> journelArrayList) {
        this.context = context;
        this.journelArrayList = journelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_row,parent,false),context);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Journel journel=journelArrayList.get(position);

        String imageUrl=journel.getImageUrl();
        Glide.with(context).load(imageUrl).fitCenter().into(holder.imageView);
        holder.tittle.setText(journel.getTitle());
        holder.description.setText(journel.getDescription());
        holder.name.setText(journel.getUserName());
        holder.time.setText((String) DateUtils.getRelativeTimeSpanString(journel.getTimeAdded().getSeconds()*1000));


    }

    @Override
    public int getItemCount() {
        return journelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton shareButton;
        TextView tittle,time,description,name;
        public MyViewHolder(@NonNull View itemView,Context context1) {
            super(itemView);
            context=context1;
            tittle=itemView.findViewById(R.id.jornalTitleList);
            time=itemView.findViewById(R.id.jornalTimeStampList);
            description=itemView.findViewById(R.id.jornalDescriptionList);
            imageView=itemView.findViewById(R.id.jornalImageList);
            name=itemView.findViewById(R.id.titlerow);
            shareButton=itemView.findViewById(R.id.imageButton);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }
}
