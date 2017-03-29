package com.example.eric.socials;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private String[]optionsArray = {"Update Profile", "Create New Social", "Logout"};
    RecyclerView recyclerView;
    FeedAdapter feedAdapter;
    private ArrayList<Social> mSocials = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        recyclerView = (RecyclerView)findViewById(R.id.recylcerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mDrawerList = (ListView) findViewById(R.id.navList);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionsArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {                  // Listener for clicking an Item on the Side Drawer
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SparseBooleanArray clickedItemPositions = mDrawerList.getCheckedItemPositions();
                mAdapter.notifyDataSetChanged();

                for(int j = 0;j < clickedItemPositions.size(); j ++){
                    boolean checked = clickedItemPositions.valueAt(j);

                    if(checked){
                        int key = clickedItemPositions.keyAt(j);
                        String item = (String) mDrawerList.getItemAtPosition(key);

                        if(item.equalsIgnoreCase(getString(R.string.update_profile_button))){
                            Intent intent = new Intent(getApplicationContext(), UpdateProfile.class);
                            startActivity(intent);
                        }
                        if(item.equalsIgnoreCase(getString(R.string.logout_button))){
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                        if(item.equalsIgnoreCase(getString(R.string.create_new_social_button))){
                            Intent intent = new Intent(getApplicationContext(), AddEvent.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(getString(R.string.socials_db_tree));         // DB Reference to the socials tree
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSocials.clear();
                for (DataSnapshot socialSnapShot: dataSnapshot.getChildren()) {
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    Social social = new Social(socialSnapShot.child(getString(R.string.eventName)).getValue(String.class),
                            socialSnapShot.child(getString(R.string.eventImage)).getValue(String.class),
                            socialSnapShot.child(getString(R.string.emailOfCreator)).getValue(String.class),                                // Adding socials to an arraylist so the adapter can use the data
                            socialSnapShot.child(getString(R.string.description)).getValue(String.class),
                            socialSnapShot.child(getString(R.string.numRSVP)).getValue(Integer.class),
                            socialSnapShot.child(getString(R.string.date)).getValue(String.class),
                            socialSnapShot.child(getString(R.string.usersInterested)).getValue(t)
                    );
                    mSocials.add(social);
                }
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        feedAdapter = new FeedAdapter(this, mSocials);
        recyclerView.setAdapter(feedAdapter);                   // Set adapter with arraylist of values
        feedAdapter.notifyDataSetChanged();

    }

    @Override
    public void onResume(){
        super.onResume();
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart(){
        super.onStart();
        feedAdapter.notifyDataSetChanged();
    }

}