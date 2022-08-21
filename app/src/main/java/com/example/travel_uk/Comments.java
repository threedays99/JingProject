package com.example.travel_uk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travel_uk.Adapter.CommentAdapter;
import com.example.travel_uk.Model.CommentModel;
import com.example.travel_uk.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Comments extends AppCompatActivity {

    private EditText addComment;
    private ImageView imageProfile;
    private ImageView Post;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<CommentModel> commentModelList;

    String postid,posterid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        posterid = intent.getStringExtra("poster");

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentModelList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentModelList,postid);
        recyclerView.setAdapter(commentAdapter);

        addComment = (EditText) findViewById(R.id.addcomment);
        imageProfile = (ImageView) findViewById(R.id.imageprofile);
        Post = (ImageView) findViewById(R.id.imgpost);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addComment.getText().toString().equals("")){
                    Toast.makeText(Comments.this,"You can't send empty comment",Toast.LENGTH_SHORT).show();
                }else {
                    addComment();
                }
            }
        });

        getImage();
        readComments();

    }

    private void addComment() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        String commentid = databaseReference.push().getKey();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("comment",addComment.getText().toString());
        hashMap.put("poster",firebaseUser.getUid());
        hashMap.put("commentid",commentid);

        databaseReference.child(commentid).setValue(hashMap);
        addNotification();
        addComment.setText("");

    }

    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(posterid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Commented: "+ addComment.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }

    private void getImage(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel =snapshot.getValue(UserModel.class);
                Glide.with(getApplicationContext()).load(userModel.getImageurl()).into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readComments(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentModelList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    CommentModel commentModel = snapshot1.getValue(CommentModel.class);
                    commentModelList.add(commentModel);

                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}