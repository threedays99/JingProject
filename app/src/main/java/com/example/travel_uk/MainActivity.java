package com.example.travel_uk;


import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth Firebaseauth;
    FirebaseDatabase Firebasedatabase;
    DatabaseReference Databasereference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textview_title);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(textView,"alpha",0f,1f);
        objectAnimator.setDuration(2500);
        objectAnimator.start();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Firebaseauth = FirebaseAuth.getInstance();
                if(Firebaseauth.getCurrentUser()!=null){
                    if(Firebaseauth.getCurrentUser().isEmailVerified()){
                        Firebaseauth=FirebaseAuth.getInstance();
                        Databasereference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
                    }
                      else{
                          Firebaseauth.signOut();
                    }
                }
                    Intent intent=new Intent(MainActivity.this, MainPage.class);
                    startActivity(intent);
                    finish();
            }
        }, 3000);
    }
}