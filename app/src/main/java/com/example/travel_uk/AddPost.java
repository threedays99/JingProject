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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class AddPost extends AppCompatActivity {

    private ImageView Close,imagePost;
    private EditText Description;
    private TextView Post;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private StorageTask storageTask;
    private Uri imageUri;
    String myUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Close = (ImageView) findViewById(R.id.close);
        imagePost = (ImageView) findViewById(R.id.imagepost);
        Description = (EditText) findViewById(R.id.description);
        Post = (TextView) findViewById(R.id.post);

        storageReference = FirebaseStorage.getInstance().getReference("Posts");

        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ActivityCompat.checkSelfPermission(AddPost.this, Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED)
                        && (ActivityCompat.checkSelfPermission(AddPost.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED)
                        && (ActivityCompat.checkSelfPermission(AddPost.this, Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED)) {

                    ActivityCompat.requestPermissions(
                            AddPost.this,new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,}
                            ,1
                    );
                }else {

                }

            }
        });

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddPost.this,HomePage.class);
                startActivity(i);
                finish();
            }
        });
        Post.setOnClickListener(new View.OnClickListener() {
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
            } else {
                //If scheme is a File
                //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

            }

            return extension;
        }

    private void uploadImage() {
        final ProgressDialog dialogwait = new ProgressDialog(this);
        dialogwait.setMessage("Posting...");
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

                        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                        String postid = databaseReference.push().getKey();
                        HashMap<String, Object> Hashmap_posts = new HashMap<>();
                        Hashmap_posts.put("postid", postid);
                        Hashmap_posts.put("postimage", myUri);
                        Hashmap_posts.put("description", Description.getText().toString());
                        Hashmap_posts.put("poster", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        databaseReference.child(postid).setValue(Hashmap_posts);
                        dialogwait.dismiss();
                        startActivity(new Intent(AddPost.this,HomePage.class));
                        finish();
                    }else {
                        Toast.makeText(AddPost.this,"Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddPost.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(AddPost.this,"No Image Selected!",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            imageUri = result.getUri();
            imagePost.setImageURI(imageUri);
        }else {
            Toast.makeText(this,"Something get wrong~",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(AddPost.this,HomePage.class);
            startActivity(i);
            finish();
        }
    }
}