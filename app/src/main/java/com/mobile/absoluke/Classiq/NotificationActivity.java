package com.mobile.absoluke.Classiq;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dataobject.Notification;

/**
 * Created by Yul Lucia on 12/31/2017.
 */

public class NotificationActivity extends AppCompatActivity {

    //Firebase
    DatabaseReference mDatabase, notifyRef;
    FirebaseUser currentUser;

    //Recycler
    RecyclerView recyvwNotify;
    NotificationAdapter adapter;
    ArrayList<Notification> list;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyvwNotify = findViewById(R.id.recyvwNotify);
        list = new ArrayList<>();
        adapter = new NotificationAdapter(this, list);

        recyvwNotify.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        recyvwNotify.setLayoutManager(layoutManager);

        recyvwNotify.setAdapter(adapter);

        notifyRef = mDatabase.child("notifications").child(currentUser.getUid());
        notifyRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notification newNotify = dataSnapshot.getValue(Notification.class);
                list.add(0, newNotify);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}