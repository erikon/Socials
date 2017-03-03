package com.example.eric.socials;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * Created by eric on 2/19/17.
 */

public class FeedAdapter  extends RecyclerView.Adapter<FeedAdapter.CustomViewHolder>{


    private Context context;
    public ArrayList<Social> data;

    public FeedAdapter(Context context, ArrayList<Social> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false);
        return new CustomViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Social s = data.get(data.size()-position-1);
        holder.eventName.setText("Title: " + s.eventName);
        holder.email.setText("Creator: " + s.emailOfCreator);
        holder.numRSVP.setText("People Interested: " + Integer.toString(s.numRSVP));

        class DownloadFilesTaskSocial extends AsyncTask<String, Void, Bitmap> {             // Download and insert images into the feed views
            protected Bitmap doInBackground(String... strings) {
                try {
                    return Glide.with(context).
                        load(strings[0]).
                        asBitmap().
                        into(100, 100). // Width and height
                        get();
                }
                catch (Exception e) {
                    return null;
                }
            }

            protected void onProgressUpdate(Void... progress) {}

            protected void onPostExecute(Bitmap result) {
                holder.eventImage.setImageBitmap(result);
            }
        }
        FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdb-socials-47b33.appspot.com").child(s.eventImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                new DownloadFilesTaskSocial().execute(uri.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        holder.eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                String socialId = s.eventImage.substring(0, s.eventImage.length() - 4);
                intent.putExtra("socialID", socialId);
                intent.putExtra("name",s.eventName);
                intent.putExtra("image", s.eventImage);
                intent.putExtra("desc", s.description);                         // Since this is an adapter class, we can't use strings file
                intent.putExtra("numRSVP", Integer.toString(s.numRSVP));
                intent.putExtra("email", s.emailOfCreator);
                intent.putExtra("usersInterested", s.usersInterested);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {            // Custom View Holder Class for the feed of socials
        TextView eventName;
        TextView email;
        TextView numRSVP;
        ImageView eventImage;

        public CustomViewHolder (View view) {
            super(view);
            this.eventName = (TextView) view.findViewById(R.id.eventName);
            this.email = (TextView) view.findViewById(R.id.email);
            this.numRSVP = (TextView) view.findViewById(R.id.numRSVPButton);
            this.eventImage = (ImageView) view.findViewById(R.id.eventImage);
        }
    }

}
