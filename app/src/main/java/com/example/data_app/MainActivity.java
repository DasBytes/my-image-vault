package com.example.data_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private Uri imageUrl;
    private Button chooseButton, uploadButton, displayButton;
    private EditText imageNameEditText;
    private ImageView imageView;
    private ProgressBar progressBar;

    private static final int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        chooseButton = findViewById(R.id.chooseImageButton);
        uploadButton = findViewById(R.id.uploadButton);
        displayButton = findViewById(R.id.displayButton);
        imageNameEditText = findViewById(R.id.imageNameEditText);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        chooseButton.setOnClickListener(v -> chooseImage());
        uploadButton.setOnClickListener(v -> uploadImage());
        displayButton.setOnClickListener(v -> displayImages());
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUrl = data.getData();
            Picasso.get().load(imageUrl).into(imageView);
        }
    }

    private void uploadImage() {
        String imageName = imageNameEditText.getText().toString();
        if (imageUrl != null && !imageName.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Images").child(imageName);
            storageReference.putFile(imageUrl).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    storageReference.getDownloadUrl().addOnCompleteListener(urlTask -> {
                        if (urlTask.isSuccessful()) {
                            String downloadUrl = urlTask.getResult().toString();
                            saveImageToDatabase(imageName, downloadUrl);
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Upload failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Select an image and enter a name", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToDatabase(String imageName, String downloadUrl) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userImagesRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Images");

        userImagesRef.child(imageName).setValue(downloadUrl).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to save image " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayImages() {
        startActivity(new Intent(MainActivity.this, ShowRecycleView.class));
    }
}
