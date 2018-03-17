package com.mobile.absoluke.Classiq;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import dataobject.UserInfo;
import tool.Tool;

public class ProfileActivity extends AppCompatActivity {
    static final String TAG = "ProfileActivity";
    private static final int REQUEST_CODE_FILE_AVATAR = 2;
    private static final int REQUEST_CODE_FILE_COVER = 3;
    public UserInfo userInfo;
    TabLayout tabLayout;

    Intent intent;
    Bundle bundle;
    String userID;



    //Components
    ImageView imageCover;
    TextView tvUsername;
    TextView tvIntro;
    RoundedImage roundedImageChangeAvatar;
    Button btnIntro;
    ImageButton btnRight;
    FloatingActionButton fabAddPost;
    ImageButton btnLeft;

    //Firebase
    FirebaseAuth auth;
    FirebaseUser currentUser;
    DatabaseReference mDatabase, curUserRef;
    FirebaseStorage storage;
    StorageReference storageRef;
    private int[] tabIcons = {
            R.drawable.location,
            R.drawable.posts,
            R.drawable.photos,
            R.drawable.about
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        createTabFragment();
        initFirabase();
        matchComponents();
        loadDataFromFirebase();
    }

    private void createTabFragment(){
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

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.containerProfile);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        setupTabIcons();

        //Change TabItem icon color
        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //for removing the color of first icon when switched to next tab
                tabLayout.getTabAt(0).getIcon().clearColorFilter();
                //for other tabs
                tab.getIcon().clearColorFilter();

            }

            //            Let it empty
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
    }

    private void matchComponents() {
        imageCover = findViewById(R.id.imageCover);
        tvUsername = findViewById(R.id.username);
        roundedImageChangeAvatar = findViewById(R.id.roundImageChangeAvatar);


        // Set a click listener for the Popup Intro
        btnIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, PopupIntro.class));
            }
        });

        // Thay đổi hình cho Avatar
        roundedImageChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent()
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setType("image/*")
                        .setAction(Intent.ACTION_OPEN_DOCUMENT);

                startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_CODE_FILE_AVATAR);
            }
        });

        // Thay đổi hình cho Background Cover
        imageCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent()
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setType("image/*")
                        .setAction(Intent.ACTION_OPEN_DOCUMENT);

                startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_CODE_FILE_COVER);
            }
        });

        fabAddPost = findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.changeActivity(ProfileActivity.this, AddPostActivity.class);
            }
        });

        // Button back
        btnLeft = findViewById(R.id.btn_left);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.changeActivity(ProfileActivity.this, MainActivity.class);
            }
        });
    }

    // Lấy hình vừa chọn gán vào
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_FILE_AVATAR && resultCode == RESULT_OK && data != null) {
            Uri selectedfile = data.getData(); //The uri with the location of the file
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            roundedImageChangeAvatar.setImageBitmap(bitmap);
        } else if (requestCode == REQUEST_CODE_FILE_COVER && resultCode == RESULT_OK && data != null) {
            Uri selectedfile = data.getData(); //The uri with the location of the file
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageCover.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initFirabase(){
        //Auth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        //Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Storage
        storage = FirebaseStorage.getInstance("gs://travellie-5884f.appspot.com");
        storageRef = storage.getReference();

        intent = getIntent();
        bundle = intent.getBundleExtra("BUNDLE");

        tvIntro = findViewById(R.id.tvIntro);
        btnIntro = findViewById(R.id.btnIntro);
        btnRight = findViewById(R.id.btn_right);

        if (bundle == null){
            userID = currentUser.getUid();
            btnRight.setVisibility(View.INVISIBLE);
        }
        else{
            userID = bundle.getString("ID");
            btnIntro.setVisibility(View.INVISIBLE);
        }
    }

    private  void loadDataFromFirebase(){
        curUserRef = mDatabase.child("users_info").child(userID);
        curUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               userInfo = dataSnapshot.getValue(UserInfo.class);
               Log.i(TAG, "Avatar link: " + userInfo.getAvatarLink());
                Log.i(TAG, "User id: " + userInfo.getUserid());

               //Load info to UI
                //-Load avatar and cover
                StorageReference avatarRef = storage.getReferenceFromUrl(userInfo.getAvatarLink());
                Log.i(TAG, "avatarRef: " + avatarRef);

                Picasso.with(ProfileActivity.this).load(Uri.parse(userInfo.getAvatarLink())).into(roundedImageChangeAvatar);

                Picasso.with(ProfileActivity.this).load(Uri.parse(userInfo.getCoverLink())).into(imageCover);

                //-Load info
                String fullName = userInfo.getLastname() + " " + userInfo.getFirstname();
                tvUsername.setText(fullName);
                tvIntro.setText(userInfo.getDescription().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
