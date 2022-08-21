package com.example.travel_uk.Adapter;

import android.content.Context;
import android.util.EventLogTags;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_uk.Model.PostModel;
import com.example.travel_uk.R;

import org.w3c.dom.Text;

import java.util.List;

public class MySaveAdapter extends RecyclerView.Adapter<MySaveAdapter.ViewHolder> {

     Context context;
     List<PostModel> postModels;

    public MySaveAdapter(Context context, List<PostModel> postModel) {
        this.context = context;
        this.postModels = postModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mypost_item_layout, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final PostModel postModel = postModels.get(position);
        holder.Description.setText(postModel.getDescription());
        Glide.with(context).load(postModel.getPostimage()).into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView postImage;
        TextView Description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.imgProfile);
            Description = itemView.findViewById(R.id.descriptiontxt);
        }
    }


}
