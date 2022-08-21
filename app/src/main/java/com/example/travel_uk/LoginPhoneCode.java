package com.example.travel_uk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginPhoneCode extends AppCompatActivity {

    private EditText Edcode;
    private Button Btncaptcha,Btnresend;
    private FirebaseAuth Firebaseauth;
    String Phonenumber,Verifyid,smscode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_code);

        Btncaptcha = (Button) findViewById(R.id.btn_verify);
        Btnresend = (Button) findViewById(R.id.btn_resend);
        Edcode = (EditText) findViewById(R.id.ed_code);

        Firebaseauth = FirebaseAuth.getInstance();
        Btnresend.setVisibility(View.INVISIBLE);
        Phonenumber = getIntent().getStringExtra("mobilenumber");
        PhoneVerification(Phonenumber);

        Btncaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String captcha = Edcode.getText().toString().trim();
//                Btnresend.setVisibility(View.INVISIBLE);
                if (captcha.isEmpty() && captcha.length()<6){
                    Edcode.setError("Invalid Code");
                    Edcode.requestFocus();
                    return;
                }
                Registerverifycode(captcha);
            }
        });

        new CountDownTimer(60000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                Btnresend.setVisibility(View.VISIBLE);
                Btnresend.setText("Resend in"+millisUntilFinished / 1000 + "seconds");
                Btnresend.setClickable(false);
                Btnresend.setBackgroundColor(Color.parseColor("#c7c7c7"));
                Btnresend.setTextColor(ContextCompat.getColor(LoginPhoneCode.this,android.R.color.black));
                Btnresend.setTextSize(16);

            }
            @Override
            public void onFinish() {
                Btnresend.setVisibility(View.VISIBLE);
                Btnresend.setText("Resend");
                Btnresend.setClickable(true);

            }
        }.start();

        Btnresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Btnresend.setVisibility(View.VISIBLE);
                Resendvericode(Phonenumber);

                new CountDownTimer(60000,1000){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Btnresend.setVisibility(View.VISIBLE);
                        Btnresend.setText("Resend in"+"" + millisUntilFinished / 1000 + "" + "seconds");
                        Btnresend.setClickable(false);
                        Btnresend.setBackgroundColor(Color.parseColor("#082E54"));
                        Btnresend.setTextColor(ContextCompat.getColor(LoginPhoneCode.this, R.color.Black));
                        Btnresend.setTextSize(16);

                    }
                    @Override
                    public void onFinish() {
                        Btnresend.setVisibility(View.VISIBLE);
                        Btnresend.setText("Resend");
                        Btnresend.setClickable(true);

                    }
                }.start();
            }
        });

    }


    private void Resendvericode(String tphonenumber) {

        PhoneVerification(tphonenumber);

    }


    private void PhoneVerification(String phonenumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            smscode = phoneAuthCredential.getSmsCode();
            if(smscode != null){
                Edcode.setText(smscode);
                Registerverifycode(smscode);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            Toast.makeText(LoginPhoneCode.this , e.getMessage(),Toast.LENGTH_LONG).show();
        }
        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId,token);
            Verifyid = verificationId;
        }
    };

    private void Registerverifycode(String smscode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Verifyid,smscode);
        Loginwithphone(credential);
    }

    private void Loginwithphone(PhoneAuthCredential credential) {

        Firebaseauth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    startActivity(new Intent(LoginPhoneCode.this,HomePage.class));
                    finish();
                }else{
                    AlertDialog.Builder builder_alert = new AlertDialog.Builder(LoginPhoneCode.this);
                    builder_alert.setMessage("Incorrect Code!");
                    builder_alert.setPositiveButton("cancel", null);
                    builder_alert.show();
                }
            }
        });

    }

}