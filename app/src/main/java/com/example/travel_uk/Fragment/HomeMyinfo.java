package com.example.travel_uk.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel_uk.Adapter.MySaveAdapter;
import com.example.travel_uk.Adapter.MyPostAdapter;
import com.example.travel_uk.EditProfile;
import com.example.travel_uk.Model.MyPostModel;
import com.example.travel_uk.Model.PostModel;
import com.example.travel_uk.Model.UserModel;
import com.example.travel_uk.R;
import com.example.travel_uk.Settings;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeMyinfo extends Fragment {

    private ImageView imageProfile, imageSetting,Gender;
    private TextView userName, City;
    private Button editProfile;
    private ImageButton myPosts, savedPosts;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference,requestref;
    private List<String> mySaves;
    private Query query;

    RecyclerView recyclerView;
    List<PostModel> postModelList;

    RecyclerView recyclerView_save;
    MySaveAdapter mySaveAdapter;
    List<PostModel> postModelList_save;

    MyPostAdapter myPostAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myinfo, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        imageProfile = (ImageView) view.findViewById(R.id.image_profile);
        imageSetting = (ImageView) view.findViewById(R.id.settings);
        userName = (TextView) view.findViewById(R.id.username);
        City = (TextView) view.findViewById(R.id.city);
        editProfile = (Button) view.findViewById(R.id.edit_profile);
        myPosts = (ImageButton) view.findViewById(R.id.myposts);
        savedPosts = (ImageButton) view.findViewById(R.id.savedposts);
        Gender = (ImageView) view.findViewById(R.id.gender);

        recyclerView = view.findViewById(R.id.recyclerview_image);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestref = FirebaseDatabase.getInstance().getReference().child("Posts");
        Query filtquery = requestref.orderByChild("poster").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseRecyclerOptions<MyPostModel> mypost = new FirebaseRecyclerOptions.Builder<MyPostModel>()
                .setQuery(filtquery,MyPostModel.class).build();
        myPostAdapter = new MyPostAdapter(mypost);
        recyclerView.setAdapter(myPostAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                myPostAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);


        recyclerView_save = view.findViewById(R.id.recyclerview_save);
        recyclerView_save.setHasFixedSize(true);
        recyclerView_save.setLayoutManager(new LinearLayoutManager(getContext()));
        postModelList_save = new ArrayList<>();
        mySaveAdapter = new MySaveAdapter(getContext(), postModelList_save);
        recyclerView_save.setAdapter(mySaveAdapter);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_save.setVisibility(View.GONE);

        userInfo();
        mySaves();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfile.class));

            }
        });

        imageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Settings.class);
                startActivity(intent);
            }
        });

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_save.setVisibility(View.GONE);
            }
        });

        savedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_save.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    public void onStart(){
        super.onStart();
        myPostAdapter.startListening();
    }

    public void onStop(){
        super.onStop();
        myPostAdapter.stopListening();
    }


    private void userInfo() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                UserModel userModel = snapshot.getValue(UserModel.class);
                Glide.with(getContext()).load(userModel.getImageurl()).into(imageProfile);
                userName.setText(userModel.getFirstname());
                City.setText(userModel.getCity());
                if (userModel.getGender().equals("male")){
                    Gender.setImageResource(R.drawable.ic_male);
                }else if (userModel.getGender().equals("female"))
                {
                    Gender.setImageResource(R.drawable.ic_female);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void mySaves(){
        mySaves = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    mySaves.add(dataSnapshot.getKey());
                }
                getSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSaves() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postModelList_save.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PostModel postModel = dataSnapshot.getValue(PostModel.class);
                    for (String id : mySaves){
                        if (postModel.getPostid().equals(id)){
                            postModelList_save.add(postModel);
                        }
                    }
                }
                mySaveAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
