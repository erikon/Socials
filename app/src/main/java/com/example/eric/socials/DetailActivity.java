package com.example.eric.socials;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{

    TextView socialName;
    TextView emailAddress;
    TextView socialDescription;

    ImageView socialImage;

    Button interestedButton;
    Button numRSVPButton;

    DatabaseReference myRef;

    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;

    Intent grabIntent;

    ArrayList<String> usersInterested;

    boolean isInterested = false;
    int intNumRSVP;

    @Override
    public void onClick(View v){            // Handles Cases for clicking Interested & Num RSVP Buttons
        switch (v.getId()) {
            case R.id.numRSVPButton:
                if(usersInterested == null || usersInterested.size() == 0){
                    Toast.makeText(getApplicationContext(), "There is no one interested in this event", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Interested.class);
                    intent.putExtra(getString(R.string.email), emailAddress.getText().toString());              // On Click, go to new activity with recylcer view of users interested
                    startActivity(intent);
                }
                break;

            case R.id.interestedButton:
                final String userUID = mUser.getUid();
                final String socialId = grabIntent.getStringExtra("socialID");

                if (!isInterested && (usersInterested == null || !usersInterested.contains(userUID))) {
                    if(usersInterested == null) {
                        ArrayList<String> tempUsers = new ArrayList<>();
                        tempUsers.add(userUID);
                        myRef.child(socialId).child(getString(R.string.usersInterested)).setValue(tempUsers);   // On click, increment num of users interested
                    } else {                                                                                    // and add the user to the arraylist field
                        usersInterested.add(userUID);
                        myRef.child(socialId).child(getString(R.string.usersInterested)).setValue(usersInterested);
                    }

                    intNumRSVP += 1;
                    myRef.child(socialId).child(getString(R.string.numRSVP)).setValue(intNumRSVP);
                    numRSVPButton.setText(Integer.toString(intNumRSVP) + " People Interested!");

                    Toast.makeText(getApplicationContext(), getString(R.string.marked_interested), Toast.LENGTH_SHORT).show();
                    isInterested = true;

                } else {
                    usersInterested.remove(userUID);
                    myRef.child(socialId).child(getString(R.string.usersInterested)).setValue(usersInterested);

                    intNumRSVP -= 1;
                    myRef.child(socialId).child(getString(R.string.numRSVP)).setValue(intNumRSVP);
                    numRSVPButton.setText(Integer.toString(intNumRSVP) + " People Interested!");

                    Toast.makeText(getApplicationContext(), getString(R.string.unmarked_interested), Toast.LENGTH_SHORT).show();
                    isInterested = false;
                }
                break;

            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);

        grabIntent = getIntent();

        socialName = (TextView) findViewById(R.id.socialName);
        socialDescription = (TextView) findViewById(R.id.socialDescription);
        emailAddress = (TextView) findViewById(R.id.emailAddress);

        socialImage = (ImageView) findViewById(R.id.socialImage);

        interestedButton = (Button) findViewById(R.id.interestedButton);
        numRSVPButton = (Button) findViewById(R.id.numRSVPButton);

        socialName.setText(grabIntent.getStringExtra("name"));
        emailAddress.setText(grabIntent.getStringExtra("email"));       // Can't use Strings because these values come from the Adapter
        socialDescription.setText(grabIntent.getStringExtra("desc"));

        final String numrsvp = grabIntent.getStringExtra(getString(R.string.numRSVP));      // Case for 1 vs Multiple people interested
        if (Integer.parseInt(numrsvp) == 1) {
            numRSVPButton.setText("1 Person Interested!");
        } else {
            numRSVPButton.setText(numrsvp + " People Interested!");
        }

        intNumRSVP = Integer.parseInt(numrsvp);

        myRef = FirebaseDatabase.getInstance().getReference("/socials");

        String imageUrl = grabIntent.getStringExtra("image");
        usersInterested = grabIntent.getStringArrayListExtra(getString(R.string.usersInterested));

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        interestedButton.setOnClickListener(this);
        numRSVPButton.setOnClickListener(this);             //Listeners for both buttons

        final AsyncTask<String, Void, Bitmap> asyncTask = new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();                                                 // Download the image in the background and place it into the imageView
                int height = display.getHeight();
                Bitmap bit = null;
                try {
                    bit = BitmapFactory.decodeStream((InputStream) new URL(params[0].toString()).getContent());

                } catch (Exception e) {}
                Bitmap sc = Bitmap.createScaledBitmap(bit,width,height,true);
                return sc;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                socialImage.setImageBitmap(bitmap);
            }
        };


        FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.firebase_link)).child(imageUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                asyncTask.execute(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }
}
