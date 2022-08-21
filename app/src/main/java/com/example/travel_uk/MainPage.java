package com.example.travel_uk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.Button;
import android.widget.ImageView;

public class MainPage extends AppCompatActivity {

    private ImageView bgimage;
    private Button login_email;
    private Button login_phone;
    private Button sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        bgimage = (ImageView) findViewById(R.id.imageView2);

        Animation scale_in = AnimationUtils.loadAnimation(this,R.anim.scale_inside);
        Animation scale_out = AnimationUtils.loadAnimation(this,R.anim.scale_outside);
        bgimage.setAnimation(scale_in);
        bgimage.setAnimation(scale_out);

        scale_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bgimage.startAnimation(scale_out);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        scale_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bgimage.startAnimation(scale_in);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        login_email = (Button) findViewById(R.id.btn_email);
        login_phone = (Button) findViewById(R.id.btn_phone);
        sign_up = (Button) findViewById(R.id.btn_signup);

        login_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent signwithemail = new Intent(MainPage.this,LoginEmail.class);
               signwithemail.putExtra("home","email");
               startActivity(signwithemail);
               finish();
            }
        });
        login_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signwithphone = new Intent(MainPage.this,LoginPhone.class);
                signwithphone.putExtra("home","phone");
                startActivity(signwithphone);
                finish();
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(MainPage.this,Register.class);
                signup.putExtra("home","signup");
                startActivity(signup);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}