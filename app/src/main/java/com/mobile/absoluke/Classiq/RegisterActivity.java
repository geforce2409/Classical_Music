package com.mobile.absoluke.Classiq;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dataobject.UserInfo;
import tool.Tool;

public class RegisterActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_FILE_AVATAR = 2;
    private static final int REQUEST_CODE_FILE_COVER = 3;
    //Debug
    String TAG = "RegisterActivity";

    //Components
    TextInputEditText etFirstName;
    TextInputEditText etLastName;
    EditText etDayOfBirth;
    Spinner spnGender;
    Button btnUpdate;
    ImageButton imgbtnTakePicture;
    RoundedImage roundedImageChangeAvatar;
    ImageView imageCover;

    //DataObject
    UserInfo userInfo;

    //Intent and bundle
    Intent intent;
    Bundle bundle;

    int choice; //choice of gender
    Calendar myCalendar = Calendar.getInstance();
    //Firebase
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public ChildEventListener userInfoEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String uid = dataSnapshot.getValue(UserInfo.class).getUserid();
            mDatabase.child("interactions/comments").child(uid).setValue("Registered");
            mDatabase.child("interactions/friends").child(uid).setValue("Registered");
            mDatabase.child("interactions/likes").child(uid).setValue("Registered");
            mDatabase.child("interactions/posts").child(uid).setValue("Registered");
            mDatabase.child("interactions/shares").child(uid).setValue("Registered");
            mDatabase.child("interactions/comments").child(uid).setValue("Registered");
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
    };
    DatabaseReference userinfoRef;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://travellie-5884f.appspot.com");
    StorageReference storageRef = storage.getReference();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        matchComponents();

        //
        //Init
        //
        userInfo = new UserInfo();


        //Get data from previous intent
        intent = getIntent();
        bundle = intent.getBundleExtra("BUNDLE");

        String firstName = Profile.getCurrentProfile().getFirstName();
        String lastName = Profile.getCurrentProfile().getLastName() + " " + Profile.getCurrentProfile().getMiddleName();

        Uri profileUri = Profile.getCurrentProfile().getProfilePictureUri(200,200);
        Log.i(TAG, "Profile uri: " + profileUri);

        etFirstName.setText(firstName);
        etLastName.setText(lastName);

        Picasso.with(RegisterActivity.this).load(profileUri).into(roundedImageChangeAvatar, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Toast.makeText(RegisterActivity.this, "Error when loading image", Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void matchComponents(){
        //Match components
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etDayOfBirth = findViewById(R.id.etDayOfBirth);

        spnGender = findViewById(R.id.spnGender);

        btnUpdate = findViewById(R.id.btnUpdate);

        imgbtnTakePicture = findViewById(R.id.imgbtnTakePicture);
        roundedImageChangeAvatar = findViewById(R.id.roundImageChangeAvatar);
        imageCover = findViewById(R.id.imageCover);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnGender.setAdapter(adapter);
        spnGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choice = spnGender.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //Set event
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(RegisterActivity.this, "Debug: Button update clicked!", Toast.LENGTH_SHORT).show();
                if (hasNull()){
                    Log.e(TAG, "Component has null value");
                    Toast.makeText(RegisterActivity.this, R.string.has_null_value, Toast.LENGTH_SHORT).show();
                }
                else{
                    final String uid = bundle.getString("ID");
                    final String phone = bundle.getString("PHONE");
                    final String email = bundle.getString("EMAIL");

                    final Uri[] avatarLink = new Uri[1];
                    StorageReference avatarRef = storageRef.child(uid + "/avatar" + "/" + Tool.generateImageKey("avatar"));
                    byte[] avatarData = Tool.convertToBytes(roundedImageChangeAvatar);
                    UploadTask uploadTaskAvatar = avatarRef.putBytes(avatarData);
                    uploadTaskAvatar.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            avatarLink[0] = taskSnapshot.getDownloadUrl();

                            //Continue with cover
                            final Uri[] coverLink = new Uri[1];
                            StorageReference coverRef = storageRef.child(uid + "/cover" + "/" + Tool.generateImageKey("cover"));
                            byte[] coverData = Tool.convertToBytes(imageCover);
                            UploadTask uploadTaskCover = coverRef.putBytes(coverData);
                            uploadTaskCover.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                    coverLink[0] = taskSnapshot.getDownloadUrl();

                                    //Push data to database:
                                    //-Init and set data for UserInfo instance
                                    userInfo.setUserid(uid);
                                    userInfo.setFirstname(etFirstName.getText().toString());
                                    userInfo.setLastname(etLastName.getText().toString());
                                    userInfo.setEmail(email);
                                    userInfo.setAvatarLink(avatarLink[0].toString());
                                    userInfo.setCoverLink(coverLink[0].toString());
                                    userInfo.setPhone(phone);
                                    userInfo.setDateofbirth(etDayOfBirth.getText().toString());
                                    userInfo.setGender(choice);
                                    userInfo.setDescription("Newbie");
                                    userInfo.setRank("Beginner");

                                    //-Push to database
                                    userinfoRef = mDatabase.child("users_info");
                                    userinfoRef.addChildEventListener(userInfoEventListener);
                                    userinfoRef.child(userInfo.getUserid()).setValue(userInfo);

                                    Toast.makeText(RegisterActivity.this, R.string.update_success, Toast.LENGTH_SHORT).show();
                                    Tool.changeActivity(RegisterActivity.this, ProfileActivity.class);
                                }
                            });

                        }
                    });

                }
            }
        });

        imgbtnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[] {android.Manifest.permission.CAMERA},
                        REQUEST_CODE_CAMERA);
            }
        });

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


        etDayOfBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    // Kiểm tra việc người dùng có cho phép mở Camera trên Android 6.0 hay không
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        } else {
            Toast.makeText(this, "Bạn không cho phép mở Camera",Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Lấy hình vừa chụp được gán vào
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            roundedImageChangeAvatar.setImageBitmap(bitmap);
        }
        else if(requestCode == REQUEST_CODE_FILE_AVATAR && resultCode == RESULT_OK && data != null) {
            Uri selectedfile = data.getData(); //The uri with the location of the file
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            roundedImageChangeAvatar.setImageBitmap(bitmap);
        }
        else if (requestCode == REQUEST_CODE_FILE_COVER && resultCode == RESULT_OK && data != null){
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

    //Kiểm tra có components nào null hay không
    private boolean hasNull(){
        if (etFirstName.getText().length() == 0) return true;
        if (etLastName.getText().length() == 0) return true;
        if (etDayOfBirth.getText().length() == 0) return true;
        //Check gender spinner here!
        Log.i(TAG,"Has no null");
        return false;
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etDayOfBirth.setText(sdf.format(myCalendar.getTime()));
    }
}
