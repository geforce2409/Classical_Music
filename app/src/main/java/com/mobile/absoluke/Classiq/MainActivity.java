package com.mobile.absoluke.Classiq;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dataobject.Post;
import dataobject.UserInfo;
import tool.Tool;

public class MainActivity extends AppCompatActivity {

    //Components
    RoundedImage roundImageAvatar;
    FloatingActionButton fabAddPost;

    //List post
    RecyclerView recyvwPosts;
    PostRecyclerAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<Post> listPost;    //test

    //Pagnition - partial load newsfeed
    final int itemPerTurn = 3;    // số item mỗi lượt
    int indexTurn = 0;      // số turn hiện tại
    boolean isLoading;
    String keystart = "";

    //Firebase
    DatabaseReference mDatabase, postRef, notifyRef;
    FirebaseUser currentUser;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        matchComponents();
        initRecyclerView();
    }


    private void matchComponents() {
        roundImageAvatar = findViewById(R.id.roundImageAvatar);
        roundImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.changeActivity(MainActivity.this, ProfileActivity.class);
            }
        });

        listPost = new ArrayList<>();
        listPost.clear();
        adapter = new PostRecyclerAdapter(this, listPost);

        indexTurn = 0;
        isLoading = false;

        recyvwPosts = findViewById(R.id.recyvwPosts);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.post_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        storageRef = FirebaseStorage.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users_info").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                if (userInfo.getAvatarLink() != "null")
                    Picasso.with(MainActivity.this).load(userInfo.getAvatarLink()).into(roundImageAvatar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //!!!! This is for TESTING !!!!
        postRef = mDatabase.child("posts_awaiting");

        //Load newsfeed
        Query query = postRef.orderByKey().startAt("").limitToLast(itemPerTurn);

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

        fabAddPost = findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.changeActivity(MainActivity.this, AddPostActivity.class);
            }
        });
    }

    public void initRecyclerView(){

        recyvwPosts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

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
                    Query querydb = postRef.orderByKey().endAt(keystart).limitToLast(itemPerTurn);
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
}


////        notifyRef = mDatabase.child("notifications").child(currentUser.getUid());
////        notifyRef.orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                if (dataSnapshot.exists()){
////                    for(DataSnapshot data:dataSnapshot.getChildren()){
////                        Notification notify = data.getValue(Notification.class);
////                        Toast.makeText(MainActivity.this, notify.getSenderName() + " đã thích bài viết", Toast.LENGTH_SHORT).show();
////                    }
////                }
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        });
//