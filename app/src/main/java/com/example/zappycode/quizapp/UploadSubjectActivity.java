package com.example.zappycode.quizapp;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zappycode.quizapp.Models.SubjectModel;
import com.example.zappycode.quizapp.databinding.ActivityUploadSubjectBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.HashMap;

public class UploadSubjectActivity extends AppCompatActivity {
    ActivityUploadSubjectBinding binding;
    FirebaseDatabase database;
    Dialog loadingdialog;
    Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadSubjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        database = FirebaseDatabase.getInstance();

        loadingdialog = new Dialog(this);
        loadingdialog.setContentView(R.layout.loading_dialog);
        loadingdialog.setCancelable(false);

        binding.fetchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        binding.uploadSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subjName = binding.editSubject.getText().toString();

                if (subjName.isEmpty()) {
                    binding.editSubject.setError("Please enter subject name");
                } else if (imageuri == null) {
                    Toast.makeText(UploadSubjectActivity.this, "Please select subject image", Toast.LENGTH_LONG).show();
                } else {
                    uploadData(subjName, imageuri);
                }
            }
        });
    }
    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadData(String subjName, Uri imageUri) {
        loadingdialog.show();

        // Convert image to Base64 string
        String base64Image = encodeImageToBase64(imageUri);

        if (base64Image == null) {
            Toast.makeText(this, "Failed to encode image", Toast.LENGTH_SHORT).show();
            loadingdialog.dismiss();
            return;
        }

        // Generate a unique key for the subject
        DatabaseReference subjectRef = database.getReference().child("subjects").push();
        String subjectKey = subjectRef.getKey();

        // Create data for the subject
        HashMap<String, Object> subjectData = new HashMap<>();
        subjectData.put("subjectName", subjName);
        subjectData.put("imageBase64", base64Image); // Store Base64 string
        subjectData.put("subjectKey", subjectKey);

        // Save data in the database
        subjectRef.setValue(subjectData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(UploadSubjectActivity.this, "Data uploaded successfully", Toast.LENGTH_LONG).show();
                    loadingdialog.dismiss();
                    onBackPressed();
                })
                .addOnFailureListener(e -> {
                    loadingdialog.dismiss();
                    Toast.makeText(UploadSubjectActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageuri = data.getData();
            binding.subjectImage.setImageURI(imageuri);
        }
    }
}
