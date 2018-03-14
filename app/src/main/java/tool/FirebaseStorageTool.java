package tool;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.concurrent.Callable;

/**
 * Created by tranminhquan on 11/08/2017.
 */

public class FirebaseStorageTool {
    static final String STORAGE_LINK = "gs://travellie-5884f.appspot.com";

    public static Uri uploadFromImageView(ImageView imageView,String name, String reference){
        final Uri[] result = {null};

        //Create a ref
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://travellie-5884f.appspot.com");
        StorageReference storageRef = storage.getReference();
        String imgPath = name + Calendar.getInstance().getTimeInMillis();
        StorageReference avatarRef;
        if (reference != null){
            avatarRef = storageRef.child(reference + "/" + imgPath + ".png");
        }
        else{
            avatarRef = storageRef.child(imgPath + ".png");
        }

        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = avatarRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                result[0] = taskSnapshot.getDownloadUrl();
            }
        });
        return result[0];
    }

    public static Uri uploadFromImageView(ImageView imageView, String name, String reference, Callable<Uri> func){
        final Uri[] result = {null};

        //Create a ref
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://travellie-5884f.appspot.com");
        StorageReference storageRef = storage.getReference();
        String imgPath = name + Calendar.getInstance().getTimeInMillis();
        StorageReference avatarRef;
        if (reference != null){
            avatarRef = storageRef.child(reference + "/" + imgPath + ".png");
        }
        else{
            avatarRef = storageRef.child(imgPath + ".png");
        }

        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = avatarRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                result[0] = taskSnapshot.getDownloadUrl();

            }
        });
        return result[0];
    }

}
