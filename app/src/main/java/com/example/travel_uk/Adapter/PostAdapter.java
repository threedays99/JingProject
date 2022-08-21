package com.example.travel_uk.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.travel_uk.Comments;
import com.example.travel_uk.Fragment.HomeMyinfo;
import com.example.travel_uk.Model.PostModel;
import com.example.travel_uk.Model.UserModel;
import com.example.travel_uk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    Context context;
    List<PostModel> postModels;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    public PostAdapter(Context context, List<PostModel> postModels) {
        this.context = context;
        this.postModels = postModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item_layout,viewGroup,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        PostModel post = postModels.get(position);
        Glide.with(context).load(post.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.bg_load)).into(holder.postImage);
        if (post.getDescription().equals("")){
            holder.Description.setVisibility(View.GONE);
        }else{
            holder.Description.setVisibility(View.VISIBLE);
            holder.Description.setText(post.getDescription());
        }

        posterinfo(holder.imageProfile, holder.userName,holder.Posters,post.getPoster());
        isLike(post.getPostid(), holder.Like);
        txtLikes(holder.Likes, post.getPostid());
        getComments(post.getPostid(),holder.Comments);
        isSave(post.getPostid(),holder.Save);

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPoster());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        new HomeMyinfo()).commit();
            }
        });

        holder.Posters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPoster());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        new HomeMyinfo()).commit();
            }
        });

        holder.Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.Save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).removeValue();

                }
            }
        });

        holder.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.Like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPoster(),post.getPostid());
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        holder.Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Comments.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("poster",post.getPoster());
                context.startActivity(intent);
            }
        });

        holder.Comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Comments.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("poster",post.getPoster());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageProfile,postImage,Like,Comment,Save;
        TextView userName,Likes,Posters,Description,Comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = (ImageView) itemView.findViewById(R.id.imageface);
            postImage = (ImageView) itemView.findViewById(R.id.postimage);
            Like = (ImageView) itemView.findViewById(R.id.like);
            Comment = (ImageView) itemView.findViewById(R.id.comment);
            Save = (ImageView) itemView.findViewById(R.id.save);
            userName = (TextView) itemView.findViewById(R.id.usrename);
            Likes = (TextView) itemView.findViewById(R.id.textlike);
            Posters = (TextView) itemView.findViewById(R.id.textposter);
            Description = (TextView) itemView.findViewById(R.id.textdescription);
            Comments = (TextView) itemView.findViewById(R.id.textcomment);

        }
    }

    private void getComments(String postid, TextView Comments){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Comments.setText("View all " + snapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLike(String postid,ImageView imageView){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_clicklike);
                    imageView.setTag("liked");
                }else {
                    imageView.setImageResource(R.drawable.ic_love);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }

    private void txtLikes(final TextView Likes, String postid){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Likes.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void posterinfo(ImageView imageProfile,TextView username,TextView poster,String userid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel =snapshot.getValue(UserModel.class);
                Glide.with(context).load(userModel.getImageurl()).into(imageProfile);
                username.setText(userModel.getFirstname());
                poster.setText(userModel.getFirstname());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void isSave(String postid, ImageView imageView){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_savedd);
                    imageView.setTag("saved");
                }else {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
