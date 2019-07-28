package com.smuzh001.snapchatclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.UUID;

public class NewSnap extends AppCompatActivity {
    private ImageView imageView;
    private Button snapButton;
    private TextView textView;

    //FirebaseStorage storage = FirebaseStorage.getInstance();
    //StorageReference storageReference = storage.getReference();
    //creates a random imagename for when you upload to firebase, otherwise same name will keep getting overwritten
    String imageName = UUID.randomUUID().toString() + ".jpg";
    /**/
    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }
    /**/
    /**/
    @Override
    //if permission provided to use the camera access photos
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
    }
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_snap);
        //gets the intent to gran an image.

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textMessage);
        Button snapButton = (Button) findViewById(R.id.snapButton);
        getPhoto();
    }


    /**/
    //called when ImageButton is clicked
    public void chooseImageClicked(View view){
        getPhoto();
    }

    /**/
    public void snapButtonClick(View view){
        //store image data in a Bytestream to upload to the database (ref. Firebase:Upload from data in memory)
        /**/
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        //modified to fit needs of our project

        final UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("snaps").child(imageName).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(NewSnap.this, "UploadFailed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(NewSnap.this, "UploadSucceed", Toast.LENGTH_SHORT).show();
                //get the download URL
                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("snaps").child(imageName);
                String downloadUrl = ref.getDownloadUrl().toString();

                Intent intent = new Intent(getApplicationContext(), SelectUser.class);
                intent.putExtra("imageURL", downloadUrl);
                intent.putExtra("imageName", imageName);
                intent.putExtra("body",textView.getText().toString());
                startActivity(intent);

            }
        });
        /**/

    }
    /**/
    //once you choose an image this function handles displaying image on app.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imageView.setImageBitmap(bitmap);
                //store image data in a Bytestream to upload to the database (ref. Firebase:Upload from data in memory)
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    /**/
}
