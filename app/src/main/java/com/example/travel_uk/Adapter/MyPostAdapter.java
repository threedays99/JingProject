package com.example.travel_uk.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_uk.Model.MyPostModel;
import com.example.travel_uk.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MyPostAdapter extends FirebaseRecyclerAdapter<MyPostModel,MyPostAdapter.myViewHolder> {

    public MyPostAdapter(@NonNull FirebaseRecyclerOptions<MyPostModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull MyPostModel model) {
        holder.Description.setText(model.getDescription());
        Glide.with(holder.imgProfile.getContext()).load(model.getPostimage()).into(holder.imgProfile);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mypost_item_layout,parent,false);
        return new myViewHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getRef().removeValue();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        ImageView imgProfile;
        TextView Description;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            Description = itemView.findViewById(R.id.descriptiontxt);
        }
    }
}
