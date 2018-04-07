package com.mobile.absoluke.Classiq;

import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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


public class AddPostActivity extends AppCompatActivity {
    static final String TAG = "AddPostActivity";
    private static final int REQUEST_CODE_FILE_AUDIO = 1;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 401;
    private final int MAX_IMAGE_SELECTION_LIMIT = 10;
    private final int REQUEST_IMAGE = 301;
    //Components
    RoundedImage roundedImageAvatar;
    TextView tvUsername;
    EditText editText;
    ImageButton btnChoosePic;
    ImageButton btnChooseAudio;
    ImageButton btnPost;
    boolean chosePic = false;
    boolean choseAudio = false;

    //Audio File
    Uri audioFile;

    //Firebase
    FirebaseUser currentUser;
    DatabaseReference mDatabase, curUserRef;
    FirebaseStorage storage;
    StorageReference storageRef;

    int counter;

    //Dataobject
    UserInfo userInfo;

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

    void matchComponents() {
        roundedImageAvatar = findViewById(R.id.roundImageAvatar);
        tvUsername = findViewById(R.id.tvUsername);
        editText = findViewById(R.id.editText);
        btnChoosePic = findViewById(R.id.btnChoosePic);
        btnPost = findViewById(R.id.btnPost);
        btnChooseAudio = findViewById(R.id.btnChooseAudio);

        //spnTag = findViewById(R.id.spinnerTag);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Tag_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        // spnTag.setAdapter(adapter);
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

        btnChooseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent audioIntent = new Intent()
                        .setType("audio/*")
                        .setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(audioIntent, "Select a file"), REQUEST_CODE_FILE_AUDIO);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Push dữ liệu lên
                //++status
                String status = editText.getText().toString();
                //++avatar link
                String avatarLink = userInfo.getAvatarLink();
                //++get timestamp
                long timestamp = Calendar.getInstance().getTimeInMillis();

                final Post newPost = new Post();
                newPost.init();
                newPost.setUserid(currentUser.getUid());
                newPost.setUsername(tvUsername.getText().toString());
                newPost.setContent(status);
                newPost.setAvatarLink(avatarLink);
                newPost.setTimestamp(timestamp);
                newPost.setLikeCount(0);
                newPost.setCmtCount(0);

                //Push để lấy key trước
                String postId = mDatabase.child("posts_awaiting").push().getKey();
                newPost.setPostid(postId);

                //Kiểm tra hình
                if (!chosePic) {
                    newPost.addImageLink("null");
                }
                else {
                    counter = 0;
                    final ArrayList<String> listImg = mImagesAdapter.getListImage();
                    for (int i = 0; i < listImg.size(); i++) {
                        Uri file = Uri.fromFile(new File(listImg.get(i)));
                        UploadTask uploadTask = storageRef.child(currentUser.getUid()).child("posts").child(Tool.generateImageKey(newPost.getPostid()) + i).putFile(file);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri link = taskSnapshot.getDownloadUrl();

                                // Thêm vào post
                                newPost.addImageLink(link.toString());
                                mDatabase.child("photos").child(currentUser.getUid()).push().setValue(taskSnapshot.getDownloadUrl().toString());

                                // Nếu đã check image xong thì bắt đầu check audio.
                                // Do đây là các hàm call back nên không thể tách riêng
                                if (counter == listImg.size() - 1) {
                                    //Kiểm tra audio
                                    if (!choseAudio) {
                                        newPost.setAudioLink("null");
                                        //Set value
                                        mDatabase.child("posts_awaiting").child(newPost.getPostid()).setValue(newPost);

                                        //Thông báo post thành công
                                        Toast.makeText(AddPostActivity.this, R.string.post_success, Toast.LENGTH_SHORT).show();

                                        // Trở về profile activity
                                        Tool.changeActivity(AddPostActivity.this, ProfileActivity.class);
                                        return;
                                    } else {
                                        Uri file = audioFile;
                                        UploadTask uploadTask = storageRef.child(currentUser.getUid()).child("posts").child(Tool.generateImageKey(newPost.getPostid())).putFile(file);
                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Uri link = taskSnapshot.getDownloadUrl();

                                                // Thêm vào post
                                                newPost.setAudioLink(link.toString());
                                                mDatabase.child("audio").child(currentUser.getUid()).push().setValue(taskSnapshot.getDownloadUrl().toString());

                                                //Set value
                                                mDatabase.child("posts_awaiting").child(newPost.getPostid()).setValue(newPost);

                                                //Thông báo post thành công
                                                Toast.makeText(AddPostActivity.this, R.string.post_success, Toast.LENGTH_SHORT).show();

                                                // Trở về profile activity
                                                Tool.changeActivity(AddPostActivity.this, ProfileActivity.class);
                                            }
                                        });
                                    }
                                }
                                counter++;
                            }
                        });
                    }
                }
            }
        });
    }

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

        if (requestCode == REQUEST_CODE_FILE_AUDIO) {
            try {
                choseAudio = true;
                Uri uri = data.getData();
                audioFile = uri;
                String path = uri.getPath();
                Toast.makeText(this, "Update audio success", Toast.LENGTH_SHORT).show();

//                // play audio file using MediaPlayer
//                MediaPlayer mediaPlayer = new MediaPlayer();
//                mediaPlayer.setDataSource(path);
//                mediaPlayer.prepare();
//                mediaPlayer.start();
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
}
