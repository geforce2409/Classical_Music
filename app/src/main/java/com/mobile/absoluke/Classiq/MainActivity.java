package com.mobile.absoluke.Classiq;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import dataobject.UserInfo;
import tool.Tool;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    RoundedImage roundImageAvatar;
    FloatingActionButton fabAddPost;

    //Firebase
    DatabaseReference mDatabase, notifyRef;
    FirebaseUser currentUser;


    private int[] tabIcons = {
            R.drawable.home,
            R.drawable.entertainment,
            R.drawable.food,
            R.drawable.accommodation,
            R.drawable.notification,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createTabFragment();
        matchComponents();
    }

    private void matchComponents() {
        roundImageAvatar = findViewById(R.id.roundImageAvatar);

        roundImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.changeActivity(MainActivity.this, ProfileActivity.class);
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users_info").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                Picasso.with(MainActivity.this).load(userInfo.getAvatarLink()).into(roundImageAvatar);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        notifyRef = mDatabase.child("notifications").child(currentUser.getUid());
//        notifyRef.orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    for(DataSnapshot data:dataSnapshot.getChildren()){
//                        Notification notify = data.getValue(Notification.class);
//                        Toast.makeText(MainActivity.this, notify.getSenderName() + " đã thích bài viết", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        fabAddPost = findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.changeActivity(MainActivity.this, AddPostActivity.class);
            }
        });
    }

    private void createTabFragment() {
        // Always cast your custom Toolbar here, and set it as the ActionBar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
//        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
//        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default main_toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here

        PagerAdapterMain pagerAdapterMain = new PagerAdapterMain(getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.containerMain);
        viewPager.setAdapter(pagerAdapterMain);

        tabLayout = findViewById(R.id.tabsMain);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(pagerAdapterMain);
        setupTabIcons();

        //Change TabItem icon color
        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(4).getIcon().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //for other tabs
                tab.getIcon().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            }

            //Let it empty
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }
}
