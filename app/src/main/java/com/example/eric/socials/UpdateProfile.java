package com.example.eric.socials;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UpdateProfile extends AppCompatActivity implements View.OnClickListener{

    EditText nameView;
    ImageView profilePicture;
    Button saveButton;
    private Intent data;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("/user");
    private StorageReference mStorageRef;

    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;

    @Override
    public void onClick(View v){                    //Handles Button Clicks for profilePicture & the Save Button
        switch (v.getId()) {
            case R.id.profilePicture:
                AlertDialog alertDialog = new AlertDialog.Builder(UpdateProfile.this).create();
                alertDialog.setTitle("Set a Photo");

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Upload from Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //launch gallery
                                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3);
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();
                break;

            case R.id.saveButton:
                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.firebase_link));
                final String key = myRef.child("user").push().getKey();
                StorageReference imageRef = mStorageRef.child(key + ".png");

                imageRef.putFile(data.getData()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "need an image!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        User u = new User(nameView.getText().toString(), key + ".png", mUser.getEmail());
                        myRef.child(mUser.getUid()).setValue(u);
                        Toast.makeText(getApplicationContext(), getString(R.string.update_successful), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                        startActivity(intent);
                    }
                });

                Utils.progressBar(this, getString(R.string.updateprofile_progressbar));
                break;

            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        nameView = (EditText) findViewById(R.id.nameView);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        saveButton = (Button) findViewById(R.id.saveButton);

        profilePicture.setOnClickListener(this); //Open upload image on picture click

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //Instantiate Auth & User

        saveButton.setOnClickListener(this);    // Save Information to a new User Object
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes for camera/uploading a picture
        if((requestCode == 3 || requestCode == 1) && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ((ImageView) findViewById(R.id.profilePicture)).setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.data = data; // Pass Intent data to global variable data
    }
}


