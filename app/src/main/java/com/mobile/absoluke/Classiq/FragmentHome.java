package com.mobile.absoluke.Classiq;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import dataobject.Post;

/**
 * Created by Yul Lucia on 12/16/2017.
 */

public class FragmentHome extends Fragment {

    //List Post
    RecyclerView recyvwPosts;
    PostRecyclerAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<Post> listPost;    //test

    //Firebase
    FirebaseAuth auth;
    FirebaseUser currentUser;
    DatabaseReference mDatabase, postUserRef, curUserRef;
    FirebaseStorage storage;
    StorageReference storageRef;

    //Pagnition - partial load newsfeed
    final int itemPerTurn = 3;    // số item mỗi lượt
    int indexTurn = 0;      // số turn hiện tại
    boolean isLoading;
    String keystart = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        listPost = new ArrayList<>();
        listPost.clear();
        adapter = new PostRecyclerAdapter(getContext(), listPost);

        indexTurn = 0;
        isLoading = false;

        matchComponent(rootView);
        initFirebase();
        initRecyclerView();

        return rootView;
    }

    public void initFirebase(){
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        postUserRef = mDatabase.child("newsfeed/general");
        storage = FirebaseStorage.getInstance("gs://travellie-5884f.appspot.com");
        storageRef = storage.getReference();

        // Lấy user info
        curUserRef = mDatabase.child("users_info").child(currentUser.getUid());

        //Load newsfeed
        Query query = postUserRef.orderByKey().startAt("").limitToLast(itemPerTurn);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int counter = 0;
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    if (counter == 0)
                    {
                        keystart = data.getKey();
                    }
                    listPost.add(data.getValue(Post.class));
                    counter++;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initRecyclerView(){

        recyvwPosts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        recyvwPosts.setLayoutManager(layoutManager);
        final LinearLayoutManager recyclerLayout = (LinearLayoutManager)recyvwPosts.getLayoutManager();
//        recyvwPosts.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//
//            }
//        });
        recyvwPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager == null || adapter == null)
                    return;

                int totalItem = layoutManager.getItemCount();
                int lastVisibleItem = recyclerLayout.findLastVisibleItemPosition();


                // Nếu đã đến cuối list thì load tiếp
                if (!isLoading && ((lastVisibleItem + itemPerTurn) >= totalItem))
                {
                    isLoading = true;
                    //Toast.makeText(getActivity(), "Load more", Toast.LENGTH_SHORT).show();
                    // tiếp tục load từ firebase
                    Query querydb = postUserRef.orderByKey().endAt(keystart).limitToLast(itemPerTurn);
                    querydb.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int counter = 0;
                            long length = dataSnapshot.getChildrenCount();
                            Log.i("LIST", "length " + length);
                            if (length == 1) return;
                            for(DataSnapshot data:dataSnapshot.getChildren()) {


                                if (counter == 0) {
                                    keystart = data.getKey();
                                    Log.i("LIST", "Key start: " + keystart);
                                }

                                if (counter == length - 1) {
                                    isLoading = false;
                                    Log.i("LIST", "key not add " + data.getKey());
                                    break;
                                }
                                boolean isTr = false;
                                Post p = data.getValue(Post.class);
//                                for(int i=0; i<listPost.size(); i++){
//                                    if (p.getPostid().equals(listPost.get(i).getPostid())){
//                                        isTr = true;
//                                        break;
//                                    }
//                                }
                                adapter.addItem(p);

                                counter++;
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        });


        //setting the adapter

        recyvwPosts.setAdapter(adapter);
    }

    public void matchComponent(View rootView){
        recyvwPosts = rootView.findViewById(R.id.recyvwPosts);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.post_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
}