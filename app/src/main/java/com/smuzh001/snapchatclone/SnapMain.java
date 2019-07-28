package com.smuzh001.snapchatclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SnapMain extends AppCompatActivity {
    FirebaseUser currentUser;
    ListView listView;
    ArrayList<String> snapEmails = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_main);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        listView = (ListView) findViewById(R.id.pendingSnaps);
        final ArrayAdapter arrayAdapter;
        arrayAdapter = new ArrayAdapter(SnapMain.this, android.R.layout.simple_list_item_1, snapEmails);
        listView.setAdapter(arrayAdapter);

        FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Log.d("SnapReceived", "From User:" + dataSnapshot.child(dataSnapshot.getKey()).child("from").getValue().toString());
                snapEmails.add(dataSnapshot.child("from").getValue().toString());
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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.takeSnap) {
            openNewSnapActivity();
            return true;
        } else if (item.getItemId() == R.id.logout) {
            //Logout();
            return true;
        } else return false;
    }
    public void openNewSnapActivity(){
        Intent intent = new Intent(SnapMain.this, NewSnap.class);
        startActivity(intent);
    }

}
