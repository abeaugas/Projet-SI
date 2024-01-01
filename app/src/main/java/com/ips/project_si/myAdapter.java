package com.ips.project_si;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.MyViewHolder> {


    private Context context;
    private List<String> titles;
    private List<Integer> images;

    public myAdapter(Context context , List<String> titles , List<Integer> images){
        this.context = context ;
        this.titles = titles;
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(context).inflate(R.layout.grid_item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.nTextView.setText(titles.get(position));
        holder.nImageView.setImageResource(images.get(position));


    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView nImageView;
        TextView nTextView;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            nImageView = itemView.findViewById(R.id.imgv1);
            nTextView = itemView.findViewById(R.id.txtv1);
        }
    }




}
