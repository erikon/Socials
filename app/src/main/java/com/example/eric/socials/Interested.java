package com.example.eric.socials;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Interested extends AppCompatActivity {

    RecyclerView recyclerView;
    InterestedAdapter interestedAdapter;

    ArrayList<String>users = new ArrayList<>();
    ArrayList<User>interestedUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/socials");
        DatabaseReference ref2 = database.getReference("/user");

        Intent grabData = getIntent();
        final String socialImageId = grabData.getStringExtra(getString(R.string.email));

        ref.addValueEventListener(new ValueEventListener() {                // Get all the users who are in the arraylist of the specified social event
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot socialSnapShot: dataSnapshot.getChildren()) {
                    if (socialSnapShot.child(getString(R.string.emailOfCreator)).getValue(String.class).equalsIgnoreCase(socialImageId)) {
                        GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                        ArrayList<String> listOfUsersInSocial = socialSnapShot.child(getString(R.string.usersInterested)).getValue(t);
                        for (String userId : listOfUsersInSocial) {
                            users.add(userId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ref2.addValueEventListener(new ValueEventListener() {           // Create the users who are in the arraylist for the social and send their data to the adapter to be displayed
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapShot: dataSnapshot.getChildren()){
                    if(users.contains(userSnapShot.getKey())){
                        User u = new User(
                                userSnapShot.child(getString(R.string.name)).getValue(String.class),
                                userSnapShot.child(getString(R.string.email)).getValue(String.class),
                                userSnapShot.child(getString(R.string.profilePicture)).getValue(String.class)
                        );
                        interestedUsers.add(u);
                    }
                }
                interestedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        interestedAdapter = new InterestedAdapter(getApplicationContext(), interestedUsers);
        recyclerView.setAdapter(interestedAdapter);
        interestedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume(){
        super.onResume();
        interestedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart(){
        super.onStart();
        interestedAdapter.notifyDataSetChanged();
    }
}
