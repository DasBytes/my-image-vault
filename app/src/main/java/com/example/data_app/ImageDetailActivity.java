package com.example.data_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ImageDetailActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView imageNameTextView;
    private Button deleteButton;
    private String imageUrl;
    private String imageName;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.image_view_detail);
        imageNameTextView = findViewById(R.id.image_name_detail);
        deleteButton = findViewById(R.id.delete_button);

        // Get data from Intent
        imageUrl = getIntent().getStringExtra("imageUrl");
        imageName = getIntent().getStringExtra("imageName");

        // Load image into ImageView
        Picasso.get().load(imageUrl).into(imageView);
        imageNameTextView.setText(imageName);

        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Images");

        // Set click listener for delete button
        deleteButton.setOnClickListener(v -> deleteImage());
    }

    private void deleteImage() {
        databaseReference.child(imageName).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ImageDetailActivity.this, "Image deleted successfully.", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(ImageDetailActivity.this, "Failed to delete image.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
