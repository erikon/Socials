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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class AddEvent extends AppCompatActivity implements View.OnClickListener{

    EditText eventName;
    EditText date;
    EditText description;
    Button createSocial;
    ImageView eventImage;

    private Intent data;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("/socials");
    private StorageReference mStorageRef;

    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.createEventButton:
                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.firebase_link));
                final String key = myRef.child("socials").push().getKey();
                StorageReference imageRef = mStorageRef.child(key + ".png");

                imageRef.putFile(data.getData()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "need an image!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ArrayList<String> users = new ArrayList<String>();
                        users.add(mUser.getUid());
                        Social s = new Social(eventName.getText().toString(), key + ".png", mUser.getEmail() , description.getText().toString(),
                                1, date.getText().toString(), users);
                        myRef.child(key).setValue(s);
                        Toast.makeText(getApplicationContext(), "Post Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                        startActivity(intent);
                    }
                });
                Utils.progressBar(this, getString(R.string.create_social_progressbar));
                break;
            default:
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventName = (EditText) findViewById(R.id.eventName);
        date = (EditText) findViewById(R.id.eventDate);
        description = (EditText) findViewById(R.id.description);
        createSocial = (Button) findViewById(R.id.createEventButton);
        eventImage = (ImageView) findViewById(R.id.eventImage);

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(AddEvent.this).create();
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
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        createSocial.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if ((requestCode == 3 || requestCode == 1) && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ((ImageView) findViewById(R.id.eventImage)).setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.data = data;
    }
}
