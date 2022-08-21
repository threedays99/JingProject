package com.example.travel_uk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private Spinner Gender;
    private TextInputLayout Firstname,Surname,Email,Telephone,Pwd,Pwdconf,City;
    private String firstname_v= "",surname_v= "",email_v= "",telephone_v= "",pwd_v= "",pwdconf_v= "",city_v= "";
    private Button Register;
    private CountryCodePicker Phonecode;
    private DatabaseReference Databasereference;
    private FirebaseAuth Firebaseauth;
    String Users ="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//      Creating a gender selection adapter
        Gender = (Spinner) findViewById(R.id.gender);
        ArrayAdapter gender_adapter = ArrayAdapter.createFromResource(this,R.array.Gender,android.R.layout.simple_spinner_item);
        gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Gender.setPrompt("Select one");
//      Adapters associated with drop-down boxes
        Gender.setAdapter(gender_adapter);
//      Dropdown box setting listener
        Gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int p, long l) {
                String gender = adapterView.getItemAtPosition(p).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//      get id
        String gender = Gender.getSelectedItem().toString();
        Firstname = (TextInputLayout) findViewById(R.id.firstname);
        Surname = (TextInputLayout) findViewById(R.id.surname);
        Email = (TextInputLayout) findViewById(R.id.emailaddress);
        Telephone = (TextInputLayout) findViewById(R.id.telephone);
        Pwd = (TextInputLayout) findViewById(R.id.password);
        Pwdconf = (TextInputLayout) findViewById(R.id.pwdconf);
        City = (TextInputLayout) findViewById(R.id.city);

        Register = (Button) findViewById(R.id.register);

        Phonecode = (CountryCodePicker) findViewById(R.id.phonecode);
//      Get a Firebase instance and a FirebaseAuth instance
        Databasereference = FirebaseDatabase.getInstance().getReference("Users");
        Firebaseauth = FirebaseAuth.getInstance();
//      Get String Subsequent Determination
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname_v = Firstname.getEditText().getText().toString().trim();
                surname_v = Surname.getEditText().getText().toString().trim();
                email_v = Email.getEditText().getText().toString().trim();
                telephone_v = Telephone.getEditText().getText().toString().trim();
                pwd_v = Pwd.getEditText().getText().toString().trim();
                pwdconf_v = Pwdconf.getEditText().getText().toString().trim();
                city_v = City.getEditText().getText().toString().trim();


                if (isValid()) {
                    final ProgressDialog dialogwait = new ProgressDialog(Register.this);
                    dialogwait.setCancelable(false);
                    dialogwait.setCanceledOnTouchOutside(false);
                    dialogwait.setMessage("please wait...");
                    dialogwait.show();


//      Registration for Firebase authentication, based on the isSuccessful() method of the Task object to determine whether the operation was successful or not
                    Firebaseauth.createUserWithEmailAndPassword(email_v, pwd_v).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = Firebaseauth.getCurrentUser();
                                String userid = firebaseUser.getUid();
                                Databasereference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                                HashMap<String, Object> Hashmap_info = new HashMap<>();
                                Hashmap_info.put("Firstname", firstname_v);
                                Hashmap_info.put("Surname", surname_v);
                                Hashmap_info.put("Email", email_v);
                                Hashmap_info.put("Telephone", telephone_v);
                                Hashmap_info.put("Password", pwd_v);
                                Hashmap_info.put("Confirm Password", pwdconf_v);
                                Hashmap_info.put("City", city_v);
                                Hashmap_info.put("Gender", gender);
                                Hashmap_info.put("imageurl","https://firebasestorage.googleapis.com/v0/b/travel-uk-8675c.appspot.com/o/Posts%2Fholder.jpg?alt=media&token=a20fc912-49e7-460b-8ece-9f761bacae22");

                                Databasereference.setValue(Hashmap_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialogwait.dismiss();
                                        Firebaseauth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                                    builder.setMessage("Register successfully!please verify email");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("I got it!", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();

                                                            String telephonenumber = Phonecode.getSelectedCountryCodeWithPlus() + telephone_v;
                                                            Intent phoneverify = new Intent(com.example.travel_uk.Register.this,PhoneVerify.class);
                                                            phoneverify.putExtra("telephonenumber",telephonenumber);
                                                            startActivity(phoneverify);



                                                        }
                                                    });
                                                    AlertDialog alertdialog = builder.create();
                                                    alertdialog.show();
                                                }else{
                                                    dialogwait.dismiss();
                                                    Toast.makeText(com.example.travel_uk.Register.this,"please try again",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }


                        }
                    });
                }
            }
        });

    }
//  All input boxes cannot be empty, verify email and password are reasonable

    String email_pattern = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
    String pwd_pattern = "^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,}$";

    public boolean isValid(){

        Firstname.setErrorEnabled(false);
        Firstname.setError("");
        Surname.setErrorEnabled(false);
        Surname.setError("");
        Email.setErrorEnabled(false);
        Email.setError("");
        Telephone.setErrorEnabled(false);
        Telephone.setError("");
        Pwd.setErrorEnabled(false);
        Pwd.setError("");
        Pwdconf.setErrorEnabled(false);
        Pwdconf.setError("");
        City.setErrorEnabled(false);
        City.setError("");

        boolean ValidFinal=false,ValidFirstname=false, ValidSurname=false, ValidEmail=false, ValidTelephone=false, ValidPwd=false, ValidPwdconf=false,ValidCity=false;
        if(firstname_v.length() == 0){
            Firstname.setErrorEnabled(true);
            Firstname.setError("Firstname cannot be empty");
        }else{
            ValidFirstname = true;
        }
        if(surname_v.length() == 0){
            Surname.setErrorEnabled(true);
            Surname.setError("Surname cannot be empty");
        }else{
            ValidSurname=true;
        }
        if(email_v.length() == 0){
            Email.setErrorEnabled(true);
            Email.setError("Email cannot be empty");
        }
        else if(!email_v.matches(email_pattern)){
            Email.setErrorEnabled(true);
            Email.setError("Enter correct email");
        }else{
            ValidEmail=true;
        }
        if(telephone_v.length() == 0){
            Telephone.setErrorEnabled(true);
            Telephone.setError("Telephone cannot be empty");
        }else{
            ValidTelephone=true;
        }
        if(pwd_v.length() == 0){
            Pwd.setErrorEnabled(true);
            Pwd.setError("Password cannot be empty");
        }else if(!pwd_v.matches(pwd_pattern)){
            Pwd.setErrorEnabled(true);
            Pwd.setError("Password length must be greater than 8 digits(contain upper and lower case letters and numbers)");
        }else{
            ValidPwd=true;
        }
        if(pwdconf_v.length() == 0){
            Pwd.setErrorEnabled(true);
            Pwd.setError("Confirm password cannot be empty");

        }else if(!pwdconf_v.equals(pwd_v)){
            Pwd.setErrorEnabled(true);
            Pwd.setError("Password missmatch");
        }else{
            ValidPwdconf=true;
        }
        if(city_v.length() == 0){
            City.setErrorEnabled(true);
            City.setError("Firstname cannot be empty");
        }else{
            ValidCity = true;
        }
        ValidFinal = (ValidFirstname && ValidSurname && ValidEmail && ValidTelephone && ValidPwd && ValidPwdconf && ValidCity) ? true : false;
        return ValidFinal;

    }
}