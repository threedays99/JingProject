package com.example.travel_uk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginEmail extends AppCompatActivity {

    private TextInputLayout Email,Pwd;
    private TextView Forgotpwd,Creataccount;
    private Button Btnlogin;
    private FirebaseAuth Firebaseauth;
    String Emailvalue="",Pwdvalue="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        Email = (TextInputLayout) findViewById(R.id.email_address);
        Pwd = (TextInputLayout) findViewById(R.id.emailpwd);
        Forgotpwd = (TextView) findViewById(R.id.forgotpwd);
        Creataccount = (TextView) findViewById(R.id.textcreat);
        Btnlogin = (Button) findViewById(R.id.logemail);

        Firebaseauth = FirebaseAuth.getInstance();

        Btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Emailvalue = Email.getEditText().getText().toString().trim();
                Pwdvalue = Pwd.getEditText().getText().toString().trim();

                if(isValid()){

                    Firebaseauth.signInWithEmailAndPassword(Emailvalue,Pwdvalue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                if (Firebaseauth.getCurrentUser().isEmailVerified()){
                                    Toast.makeText(LoginEmail.this,"Login Successful!",Toast.LENGTH_SHORT).show();
                                    Intent Tohome = new Intent(LoginEmail.this,HomePage.class);
                                    startActivity(Tohome);
                                    finish();

                                }else{
                                    Toast.makeText(LoginEmail.this,"Login Failed!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                }
            }
        });
        Forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginEmail.this,ForgotPassword.class));
                finish();
            }
        });


        Creataccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginEmail.this,Register.class));
                finish();
            }
        });




    }

    String Emailpattern = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";

    public boolean isValid() {


        Email.setErrorEnabled(false);
        Email.setError("");
        Pwd.setErrorEnabled(false);
        Pwd.setError("");

        boolean ValidLast = false, ValidEmail = false, ValidPwd = false;
        if(Emailvalue.length() == 0){
            Email.setErrorEnabled(true);
            Email.setError("Email cannot be empty");
        }
        else if(!Emailvalue.matches(Emailpattern)){
            Email.setErrorEnabled(true);
            Email.setError("Enter correct email");
        }else{
            ValidEmail=true;
        }
        if (Pwdvalue.length() == 0) {
            Pwd.setErrorEnabled(true);
            Pwd.setError("Password cannot be empty");
        } else {
            ValidPwd = true;
        }

        ValidLast = (ValidEmail && ValidPwd);
        return ValidLast;

    }

}