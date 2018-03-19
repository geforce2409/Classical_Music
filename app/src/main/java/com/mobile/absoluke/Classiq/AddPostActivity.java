package com.mobile.absoluke.Classiq;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.haresh.multipleimagepickerlibrary.MultiImageSelector;
import com.hsalf.smilerating.SmileRating;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dataobject.POST_TYPE;
import dataobject.Post;
import dataobject.UserInfo;
import tool.Tool;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/*
* TO-DO:
* Them chuc nang Get Location       -----   Kiet
* Them chuc nang chon anh tu may    -----   Kiet
* Tham chuc nang Post               -----   Quan
* Xu ly rating                      -----   Quan
* */

public class AddPostActivity extends AppCompatActivity {
    static final String TAG = "AddPostActivity";
    private static final int REQUEST_CODE_FILE_PICTURE = 1;
    private static final int REQUEST_LOCATION = 2;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 401;
    private final int MAX_IMAGE_SELECTION_LIMIT = 10;
    private final int REQUEST_IMAGE = 301;
    //Components
    RoundedImage roundedImageAvatar;
    TextView tvUsername;
    EditText editText;
    ImageButton btnChoosePic;
    ImageButton btnGetLocation;
    ImageButton btnPost;
    Spinner spnTag;
    SmileRating ratingBar;
    boolean chosePic = false;

    //Firebase
    FirebaseUser currentUser;
    DatabaseReference mDatabase, curUserRef;
    FirebaseStorage storage;
    StorageReference storageRef;

    int counter;

    //Dataobject
    UserInfo userInfo;

    //Location GPS
    LocationManager locationManager;
    Geocoder geocoder;
    List<Address> addresses;
    String[] infoLocation = new String[3];
    String info = "";

    int choice; //choice of TAG

    private RecyclerView recyclerViewImages;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<String> mSelectedImagesList = new ArrayList<>();
    private MultiImageSelector mMultiImageSelector;
    private ImagesAdapter mImagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        matchComponents();
        initFirebase();
        updateUI();
    }

    void initFirebase(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        //Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    void matchComponents(){
        roundedImageAvatar = findViewById(R.id.roundImageAvatar);
        tvUsername = findViewById(R.id.tvUsername);
        editText = findViewById(R.id.editText);
        btnChoosePic = findViewById(R.id.btnChoosePic);
        //btnGetLocation = findViewById(R.id.btnGetLocation);
        btnPost = findViewById(R.id.btnPost);
        ratingBar = findViewById(R.id.ratingBar);

        //spnTag = findViewById(R.id.spinnerTag);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Tag_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnTag.setAdapter(adapter);
//        spnTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                choice = spnTag.getSelectedItemPosition();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        recyclerViewImages = findViewById(R.id.recycler_view_images);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewImages.setHasFixedSize(true);
        recyclerViewImages.setLayoutManager(gridLayoutManager);

        mMultiImageSelector = MultiImageSelector.create();

        //Set event
        btnChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkAndRequestPermissions()) {
                    mMultiImageSelector.showCamera(true);
                    mMultiImageSelector.count(MAX_IMAGE_SELECTION_LIMIT);
                    mMultiImageSelector.multi();
                    mMultiImageSelector.origin(mSelectedImagesList);
                    mMultiImageSelector.start(AddPostActivity.this, REQUEST_IMAGE);
                }
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kiểm tra xem rating chưa
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(AddPostActivity.this, R.string.rating_reminder, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Kiểm tra xem chọn mục chưa?
                POST_TYPE ptype = POST_TYPE.GENERAL;
                switch (spnTag.getSelectedItemPosition())
                {
                    case 0:
                        ptype = POST_TYPE.ENTERTAINMENT;
                        break;
                    case 1:
                        ptype = POST_TYPE.FOOD;
                        break;
                    case 2:
                        ptype = POST_TYPE.HOTEL;
                        break;
                    case 3:
                        ptype = POST_TYPE.GENERAL;
                        break;
                }




                //Push dữ liệu lên
                //++status
                String status = editText.getText().toString();
                //++rating
                int rating = ratingBar.getRating();
                //++avatar link
                String avatarLink = userInfo.getAvatarLink();
                //++get timestamp
                long timestamp = Calendar.getInstance().getTimeInMillis();

                //++Lấy ảnh
                //--TO DO

                final Post newPost = new Post();
                newPost.init();
                newPost.setUserid(currentUser.getUid());
                newPost.setUsername(currentUser.getDisplayName());
                newPost.setContent(status);
                newPost.setRating(rating);
                newPost.setAvatarLink(avatarLink);
                newPost.setTimestamp(timestamp);
                newPost.setLikeCount(0);
                newPost.setCmtCount(0);
                newPost.setType(ptype);

                //Push để lấy key trước
                String postId = mDatabase.child("interactions/posts").child(currentUser.getUid()).push().getKey();
                newPost.setPostid(postId);

                //Kiểm tra hình

                if (!chosePic){
                    newPost.addImageLink("noimage");
                    //Set value
                    mDatabase.child("interactions/posts").child(currentUser.getUid()).child(newPost.getPostid()).setValue(newPost);
                    // Đồng thời cập nhật cho database ở tag tương ứng
                    // ++Add vào newsfeed
                    mDatabase.child("newsfeed/general").child(newPost.getPostid()).setValue(newPost);
                    // ++Add vào tab tương ứng
                    switch (newPost.getType())
                    {
                        case ENTERTAINMENT:
                            mDatabase.child("newsfeed/entertainment").child(newPost.getPostid()).setValue(newPost);
                            break;
                        case FOOD:
                            mDatabase.child("newsfeed/food").child(newPost.getPostid()).setValue(newPost);
                            break;
                        case HOTEL:
                            mDatabase.child("newsfeed/hotel").child(newPost.getPostid()).setValue(newPost);
                            break;
                    }

                    //Thông báo post thành công
                    Toast.makeText(AddPostActivity.this, R.string.post_success, Toast.LENGTH_SHORT).show();

                    // Trở về profile activity
                    Tool.changeActivity(AddPostActivity.this, ProfileActivity.class);
                    return;
                }

                counter = 0;
                final ArrayList<String> listImg = mImagesAdapter.getListImage();
                for(int i=0; i<listImg.size(); i++){

                    Uri file = Uri.fromFile(new File(listImg.get(i)));
                    UploadTask uploadTask = storageRef.child(currentUser.getUid()).child("posts").child(newPost.getPostid()).putFile(file);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri link = taskSnapshot.getDownloadUrl();

                            // Thêm vào post
                            newPost.addImageLink(link.toString());
                            mDatabase.child("photos").child(currentUser.getUid()).push().setValue(taskSnapshot.getDownloadUrl().toString());

                            if (counter == listImg.size() - 1){
                                //Set value
                                mDatabase.child("interactions/posts").child(currentUser.getUid()).child(newPost.getPostid()).setValue(newPost);
                                // Đồng thời cập nhật cho database ở tag tương ứng
                                // ++Add vào newsfeed
                                mDatabase.child("newsfeed/general").child(newPost.getPostid()).setValue(newPost);
                                // ++Add vào tab tương ứng
                                switch (newPost.getType())
                                {
                                    case ENTERTAINMENT:
                                        mDatabase.child("newsfeed/entertainment").child(newPost.getPostid()).setValue(newPost);
                                        break;
                                    case FOOD:
                                        mDatabase.child("newsfeed/food").child(newPost.getPostid()).setValue(newPost);
                                        break;
                                    case HOTEL:
                                        mDatabase.child("newsfeed/hotel").child(newPost.getPostid()).setValue(newPost);
                                        break;

                                }

                                //Thông báo post thành công
                                Toast.makeText(AddPostActivity.this, R.string.post_success, Toast.LENGTH_SHORT).show();

                                // Trở về profile activity
                                Tool.changeActivity(AddPostActivity.this, ProfileActivity.class);
                            }

                            counter++;
                        }
                    });
                }


            }
        });
    }

//        btnGetLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    buildAlertMessageNoGps();
//
//                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    getLocation();
//                }
//            }
//        });


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            try {
                chosePic = true;
                mSelectedImagesList = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                mImagesAdapter = new ImagesAdapter(this, mSelectedImagesList);
                recyclerViewImages.setAdapter(mImagesAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkAndRequestPermissions() {
        int externalStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (externalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            btnChoosePic.performClick();
        }
    }

    void updateUI(){
        curUserRef = mDatabase.child("users_info").child(currentUser.getUid());
        curUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);

                //load user info from database to UI
                Picasso.with(AddPostActivity.this).load(Uri.parse(userInfo.getAvatarLink())).into(roundedImageAvatar);
                tvUsername.setText(userInfo.getFirstname() + " " + userInfo.getLastname());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(AddPostActivity.this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (AddPostActivity.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                getAddress(latti, longi);

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                getAddress(latti, longi);

            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                getAddress(latti, longi);

            } else {

                Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    protected void getAddress(double latti, double longi) {

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latti, longi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            //address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            infoLocation[0] = addresses.get(0).getAdminArea(); //State
            infoLocation[1] = addresses.get(0).getCountryName(); //Country
            infoLocation[2] = addresses.get(0).getFeatureName(); // Only if available else return NULL

            for (int i = 0; i < infoLocation.length; i++) {
                if (infoLocation[i] != null) {
                    info += infoLocation[i] + "\n";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Your current location is:" + "\n" + info, Toast.LENGTH_SHORT).show();
        //textView.setText("Your current location is:" + "\n" + temp);
    }
}
