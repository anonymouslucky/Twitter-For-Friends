package com.lucky.friends;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTweets extends AppCompatActivity {

    DatabaseReference database;
    StorageReference storage;
    List<String> order;
    Map<String,String> map;
    Map<String,String> tweets;
    OrderOfTweets orderClass;
    Tweet tweetClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tweets);

        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();

        database.child("Order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderClass = dataSnapshot.getValue(OrderOfTweets.class);
                if(orderClass == null){
                    orderClass = new OrderOfTweets();
                }
                order = orderClass.getOrderOfTweets();
                database.child("Tweets").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        map = (Map<String, String>) dataSnapshot.getValue();
                        if(map == null){
                            map = new HashMap<>();
                        }
                        tweets = new HashMap<>();
                        showTweets();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    public void showTweets(){
        int num = order.size();
        LinearLayout layout = (LinearLayout) findViewById(R.id.tweetLayout);

        ProgressDialog dialog = new ProgressDialog(ViewTweets.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
        for(int i=order.size()-1;i>=0;i--){
            String current = order.get(i);
            final LinearLayout tweetLayout = new LinearLayout(ViewTweets.this);
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.setMargins(0,10,5,70);
            tweetLayout.setBackgroundColor(0xff5b5a88);
            tweetLayout.setLayoutParams(ll);
            tweetLayout.setOrientation(LinearLayout.VERTICAL);
            TextView user = new TextView(ViewTweets.this);
            user.setTextColor(0x99ffcc00);
            user.setText(current.toUpperCase());
            user.setTextSize(30);
            TextView textView = new TextView(ViewTweets.this);
            textView.setTextColor(0xff99cc00);
            textView.setTextSize(25);
            textView.setText(map.get(current));
            final ImageView image = new ImageView(this);
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            storage.child(current+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(ViewTweets.this).load(uri).into(image);
                    tweetLayout.addView(image);
                }
            });
            tweetLayout.addView(user);
            tweetLayout.addView(textView);
            layout.addView(tweetLayout);
        }
        dialog.dismiss();
    }

}
