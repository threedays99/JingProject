package com.example.travel_uk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class LoginPhone extends AppCompatActivity {

    private CountryCodePicker Phonecode;
    private EditText Phonenumer;
    private Button Btnsendcode;
    private TextView Createacc;
    private FirebaseAuth Firebaseauth;
    String Phonenumber_v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        Phonecode = (CountryCodePicker) findViewById(R.id.CountryCode);
        Phonenumer = (EditText) findViewById(R.id.phonenumber);
        Btnsendcode = (Button) findViewById(R.id.vericode);
        Createacc = (TextView) findViewById(R.id.Textcreate);

        Btnsendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Phonenumber_v = Phonenumer.getText().toString().trim();
                String mobilenumber = Phonecode.getSelectedCountryCodeWithPlus() + Phonenumber_v;
                Intent To_code = new Intent(LoginPhone.this,LoginPhoneCode.class);
                To_code.putExtra("mobilenumber",mobilenumber);
                startActivity(To_code);
                finish();

            }
        });

        Createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginPhone.this,Register.class));
                finish();
            }
        });
    }
}