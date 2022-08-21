package com.example.travel_uk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.travel_uk.Model.CustomPlaceModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;


public class CreateJourney extends AppCompatActivity {

    private EditText edittime,editStreet,editCity;
    private Button btnsave;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    public static final int PICTURE_CROPPING_CODE = 200;
    private static final int OPEN_ALBUM_CODE = 100;
    private StorageReference storageReference;
    private StorageTask storageTask;
    private Uri imageUri;
    private FirebaseUser firebaseUser;
    String myUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journey);

//        获取id
        edittime = (EditText) findViewById(R.id.editTextTime);
        editStreet = (EditText)findViewById(R.id.editstreet);
        editCity = (EditText)findViewById(R.id.editcity);
        btnsave = (Button)findViewById(R.id.btnsavelocation);
        imageView = (ImageView)findViewById(R.id.imagesite);

        storageReference = FirebaseStorage.getInstance().getReference("Custom Places");

        firebaseDatabase = FirebaseDatabase.getInstance();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ActivityCompat.checkSelfPermission(CreateJourney.this, Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED)
                        && (ActivityCompat.checkSelfPermission(CreateJourney.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED)
                        && (ActivityCompat.checkSelfPermission(CreateJourney.this, Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED)) {

                    ActivityCompat.requestPermissions(
                            CreateJourney.this,new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,}
                                    ,1
                    );
                }else {


                }

            }
        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        CropImage.activity()
                .setAspectRatio(1,1)
                .start(this);

    }

    private String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
        //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        }   else   {
        //If scheme is a File
        //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

    }

        return extension;
}

    private void uploadImage() {
        final ProgressDialog dialogwait = new ProgressDialog(this);
        dialogwait.setMessage("Saving...");
        dialogwait.show();

        if (imageUri != null){
            StorageReference fileref = storageReference.child(System.currentTimeMillis() +"." + getMimeType(getApplicationContext(),imageUri));
            storageTask =fileref.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()){
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloaduri = task.getResult();
                        myUri = downloaduri.toString();

                        databaseReference = FirebaseDatabase.getInstance().getReference("PlacesInfo");

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("date", edittime.getText().toString());
                        hashMap.put("image", myUri);
                        hashMap.put("city", editCity.getText().toString());
                        hashMap.put("street", editStreet.getText().toString());
                        hashMap.put("poster", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(editStreet.getText().toString()).setValue(hashMap);
                        dialogwait.dismiss();
                        startActivity(new Intent(CreateJourney.this,HomePage.class));
                        finish();
                    }else {
                        Toast.makeText(CreateJourney.this,"Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateJourney.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(CreateJourney.this,"No Image Selected!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            imageUri = result.getUri();
            imageView.setImageURI(imageUri);
        }else {
            Toast.makeText(this,"Something get wrong~",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(CreateJourney.this,HomePage.class);
            startActivity(i);
            finish();
        }
    }


    }