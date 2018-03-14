package com.mobile.absoluke.travelie;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Yul Lucia on 01/02/2018.
 */

public class PopupIntro extends Activity {

    Button btnSaveBio;
    EditText edtBio;
    TextView bio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_intro);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .24));

        btnSaveBio = findViewById(R.id.btnSaveBio);
        bio = findViewById(R.id.tvIntro);
        edtBio = findViewById(R.id.editBio);

        btnSaveBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtBio.getText().length() <= 0) {
                    Toast.makeText(PopupIntro.this, "Bạn chưa nhập Bio!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PopupIntro.this, "Đã cập nhật Bio!!!", Toast.LENGTH_SHORT).show();
                    /////////////////////////////////
                    //Chuyển Bio info lên firebase, sau đó từ firebase chuyển lại xuống TextView bên ProfileActivity
                    // ======> thuộc tính Bio bên ProfileActivity sẽ luôn nhận giá trị từ Firebase.
                    FirebaseDatabase.getInstance().getReference().child("users_info")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("description").setValue(edtBio.getText().toString());

                }
            }
        });
    }
}
