package com.mobile.absoluke.Classiq;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Yul Lucia on 11/07/2017.
 */

public class FragmentPhotos extends Fragment {

    DatabaseReference mDatabase, photoRef;
    FirebaseUser currentUser;

    String userID;

    Intent intent;
    Bundle bundle;

    private RecyclerView recyclerViewImages;
    private ImageLinkAdapter mImagesAdapter;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<String> listLink;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_photos, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getActivity().getIntent();
        bundle = intent.getBundleExtra("BUNDLE");

        if (bundle == null) {
            userID = currentUser.getUid();
        } else {
            userID = bundle.getString("ID");
        }

        listLink = new ArrayList<>();
        listLink.clear();

        recyclerViewImages = itemView.findViewById(R.id.recycler_view_images);
        gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerViewImages.setHasFixedSize(true);
        recyclerViewImages.setLayoutManager(gridLayoutManager);
        mImagesAdapter = new ImageLinkAdapter(getContext(), listLink);
        recyclerViewImages.setAdapter(mImagesAdapter);

        mDatabase.child("photos").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) return;

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    listLink.add(data.getValue().toString());
                    mImagesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return itemView;
    }
}
