package comp5216.sydney.edu.au.freshfindau.page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import comp5216.sydney.edu.au.freshfindau.R;
import comp5216.sydney.edu.au.freshfindau.account.User;
import comp5216.sydney.edu.au.freshfindau.permission.MarshmallowPermission;
import comp5216.sydney.edu.au.freshfindau.utils.NavigationUtils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private ImageView profileImage;
    private TextView tvUsername, tvEmail, tvAge, tvGender;
    private static final int PICK_IMAGE = 1;
    private FirebaseFirestore mFirestore;
    private MarshmallowPermission marshmallowPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth, Database, and Storage
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();

        profileImage = findViewById(R.id.iv_profile_image);
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        tvAge = findViewById(R.id.tv_age);
        tvGender = findViewById(R.id.tv_gender);

        NavigationUtils.setupBottomNavigation(this, R.id.bottom_navigation_btn4);
        Toolbar toolbar = findViewById(R.id.profile_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // restart MainActivity
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        loadUserInfo();
        marshmallowPermission = new MarshmallowPermission(this);

        // Setup button to change the profile picture
        Button btnChangeImage = findViewById(R.id.btn_change_image);
        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marshmallowPermission.checkPermissionForReadfiles()) {
                    chooseImage();
                } else {
                    marshmallowPermission.requestPermissionForReadfiles();
                }
            }
        });
    }

    // Function to open the image chooser
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    // Handling result after image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            uploadImage(filePath);
        }
    }

    // Uploading image to Firebase Storage and save URL to Firebase Database
    private void uploadImage(Uri filePath) {
        Bitmap compressedBitmap = compressImage(filePath);

        // 转换Bitmap为Uri
        Uri compressedUri = saveBitmapToCache(compressedBitmap);
        if (compressedUri == null) {
            Log.e("YourTag", "Failed to save compressed image.");
            return;
        }

        StorageReference fileRef = mStorageRef.child("images/").child(mAuth.getCurrentUser().getUid());
        fileRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Save imageURL to Firestore
                        mFirestore.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .update("imageURL", uri.toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Image URL updated successfully
                                        Log.d("YourTag", "Image URL updated successfully.");
                                        Picasso.get().load(uri).into(profileImage); // Update image view
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to update imageURL
                                        Log.e("YourTag", "Failed to update imageURL: ", e);
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        Log.e("YourTag", "Failed to get download URL: ", e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle unsuccessful uploads
                Log.e("YourTag", "Failed to upload image: ", e);
            }
        });
    }


    // Load user information and display in TextViews and ImageView
    private void loadUserInfo() {
        Log.d("YourTag", "loadUserInfo() called");

        mFirestore.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get user information
                                User user = document.toObject(User.class);

                                if(user != null) {
                                    tvUsername.setText("Username: " + user.username);
                                    tvEmail.setText("Email: " + user.email);
                                    tvAge.setText("Age: " + user.age);
                                    tvGender.setText("Gender: " + user.gender);
                                    String imageUrl = user.imageURL;
                                    if(imageUrl != null && !imageUrl.isEmpty()) {
                                        Picasso.get().load(imageUrl).into(profileImage);
                                    }
                                }else{
                                    Log.d("YourTag", "User not found in Firestore");
                                }
                            } else {
                                Log.d("YourTag", "No such document");
                            }
                        } else {
                            Log.d("YourTag", "get failed with ", task.getException());
                        }
                    }
                });
    }

    private Bitmap compressImage(Uri imageUri) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, o);

            // Set compression ratio
            final int REQUIRED_SIZE = 200;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Obtain compressed images
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri saveBitmapToCache(Bitmap bitmap) {
        try {
            File cachePath = new File(getCacheDir(), "images");
            if (!cachePath.exists()) {
                cachePath.mkdirs();
            }
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            return Uri.fromFile(new File(cachePath, "image.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}