package com.lucky.friends;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.WindowDecorActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Following extends AppCompatActivity {

    ListView followingUnfollowing;
    DatabaseReference reference;
    StorageReference imageReference;
    FirebaseAuth mAuth;
    List<String> Available;
    Map<String,String> tweets;
    OrderOfTweets order;
    List<String> tweetOrder;
    User follower;
    User following;

    String username;

    private static int IMAGE_REQUEST = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        Available = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        followingUnfollowing = (ListView) findViewById(R.id.folllowUnfollow);
        followingUnfollowing.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        username = getIntent().getStringExtra("username");


        reference.child("Usernames").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                for(Map.Entry<String,Object> entry:map.entrySet()){
                    if(!entry.getValue().toString().equals(username)){
                        Available.add(entry.getValue().toString());
                    }
                }
                ArrayAdapter adapter = new ArrayAdapter(Following.this,android.R.layout.simple_list_item_checked,Available);
                followingUnfollowing.setAdapter(adapter);
                reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        follower = dataSnapshot.getValue(User.class);
                        if(follower == null){
                            follower = new User();
                        }
                        for(String str:follower.getFollowing()){
                            followingUnfollowing.setItemChecked(Available.indexOf(str),true);
                        }
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

        followingUnfollowing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if(checkedTextView.isChecked()){

                    final String followingName = Available.get(position);

                    reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            follower = dataSnapshot.getValue(User.class);
                            if(follower == null){
                                follower = new User();
                            }
                            List<String> temp = follower.getFollowing();
                            temp.add(followingName);
                            follower.setFollowing(temp);
                            reference.child(username).setValue(follower);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    reference.child(followingName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            following = dataSnapshot.getValue(User.class);
                            if(following == null){
                                following = new User();
                            }
                            List<String> temp = following.getFollowers();
                            temp.add(username);
                            following.setFollowers(temp);
                            reference.child(followingName).setValue(following);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{

                    final String followingName = Available.get(position);

                    reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            follower = dataSnapshot.getValue(User.class);
                            if(follower == null){
                                follower = new User();
                            }
                            List<String> temp = follower.getFollowing();
                            temp.remove(followingName);
                            follower.setFollowing(temp);
                            reference.child(username).setValue(follower);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    reference.child(followingName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            following = dataSnapshot.getValue(User.class);
                            if(following == null){
                                following = new User();
                            }
                            List<String> temp = following.getFollowers();
                            temp.remove(username);
                            following.setFollowers(temp);
                            reference.child(followingName).setValue(following);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.tweet){

            AlertDialog.Builder builer = new AlertDialog.Builder(Following.this);
            LayoutInflater inflater = getLayoutInflater();

            View v = inflater.inflate(R.layout.tweet,null);
            final EditText tweetText = (EditText) v.findViewById(R.id.tweetText);
            builer.setView(v);

            builer.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Following.this,"No",Toast.LENGTH_SHORT).show();
                }
            });
            builer.setPositiveButton("TWEET", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String tweetByUser = tweetText.getText().toString();
                    reference.child("Tweets").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            tweets = (Map<String, String>) dataSnapshot.getValue();
                            if(tweets == null){
                                tweets = new HashMap<>();
                            }
                            tweets.put(username,tweetByUser);
                            reference.child("Tweets").setValue(tweets);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    reference.child("Order").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            order = dataSnapshot.getValue(OrderOfTweets.class);
                            if(order == null){
                                order = new OrderOfTweets();
                            }
                            tweetOrder = order.getOrderOfTweets();
                            if(tweetOrder.contains(username)){
                                tweetOrder.remove(username);
                            }
                            tweetOrder.add(username);
                            order.setOrderOfTweets(tweetOrder);
                            reference.child("Order").setValue(order);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    startActivity(new Intent(Following.this,ViewTweets.class));
                }
            });
            AlertDialog dialog = builer.create();
            Button image = (Button) v.findViewById(R.id.getImage);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(imageIntent,IMAGE_REQUEST);
                }
            });
            dialog.show();

            return true;
        }else if(item.getItemId() == R.id.logout){
            mAuth.signOut();
            Following.this.finish();
            startActivity(new Intent(Following.this,MainActivity.class));
        }else if(item.getItemId() == R.id.viewFeed){
            startActivity(new Intent(Following.this,ViewTweets.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode ==RESULT_OK && data!=null){
            Uri selectedImage = data.getData();
            try{
                String imageName = username+".jpg";
                imageReference = FirebaseStorage.getInstance().getReference().child(imageName);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] image = baos.toByteArray();
                UploadTask task = imageReference.putBytes(image);
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Following.this,"Network Error",Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });

                task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Toast.makeText(Following.this,"Successful!",Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
