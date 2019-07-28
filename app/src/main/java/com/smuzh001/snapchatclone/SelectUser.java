package com.smuzh001.snapchatclone;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




public class SelectUser extends AppCompatActivity {
    private DatabaseReference myDBref;
    private FirebaseUser user;
    private ArrayList<String> contacts = new ArrayList<>();
    private ArrayList<String> contactUIDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        myDBref = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        ListView listView = (ListView) findViewById(R.id.listOfFriends);
        final ArrayAdapter arrayAdapter;
        arrayAdapter = new ArrayAdapter(SelectUser.this, android.R.layout.simple_list_item_1, contacts);
        listView.setAdapter(arrayAdapter);

        //create a listener so whenever a new account is added to users, our contact list is updated.
        myDBref.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("ChildAdded", "New User:" + dataSnapshot.getKey());
                String email = dataSnapshot.child("email").getValue().toString();
                contacts.add(email);
                //this is how we get the UID's of the users as we add to the ListView.
                contactUIDs.add(dataSnapshot.getKey());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String reciever = contacts.get(position);
                Log.i("Contact pressed", "selected " + contacts.get(position));
                //send snap to contact with snapinfo
                String imageName = getIntent().getStringExtra("imageName");
                String imageUrl = getIntent().getStringExtra("imageUrl");
                String body = getIntent().getStringExtra("body");
                //contactUID's list will have its UID in the same position as the selected listViewElement.
                uploadSnapInfo(contactUIDs.get(position),FirebaseAuth.getInstance().getCurrentUser().getEmail() , imageName, imageUrl, body);
            }
        });
    }

    public void uploadSnapInfo(String to, String from, String imageName, String imageUrl, String body){
        //create an empty object under snaps for the user
        String key = myDBref.child("users").child(to).child("snaps").push().getKey();
        Post post = new Post(from, imageName, imageUrl, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/"+to+"/snaps/"+key, postValues);
        myDBref.updateChildren(childUpdates);
    }




}
