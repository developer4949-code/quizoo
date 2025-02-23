package com.example.zappycode.quizapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.zappycode.quizapp.Adapters.SubCategoryAdapter;
import com.example.zappycode.quizapp.Models.SubCategoryModel;
import com.example.zappycode.quizapp.databinding.ActivitySubCategoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubCategoryActivity extends AppCompatActivity {

    ActivitySubCategoryBinding binding;
    FirebaseDatabase database;
    SubCategoryAdapter adapter;
    ArrayList<SubCategoryModel> list;
    private String subjectId;

    private Dialog loadingDialog;
    private LottieAnimationView lottieAnimationView; // Lottie animation view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase and variables
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();
        subjectId = getIntent().getStringExtra("catId");

        // Setup Lottie loading dialog
        setupLoadingDialog();

        // Set up GridLayoutManager
        int spanCount = 0;
        GridLayoutManager layoutManager = new GridLayoutManager(this, Math.max(spanCount, 1));
        binding.subjectCategory.setLayoutManager(layoutManager);

        // Set up adapter
        adapter = new SubCategoryAdapter(SubCategoryActivity.this, list, subjectId);
        binding.subjectCategory.setAdapter(adapter);

        // Show loading dialog
        showLoadingDialog();

        // Retrieve subcategories from Firebase
        database.getReference().child("subjects").child(subjectId).child("subcategories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Hide loading dialog
                        hideLoadingDialog();

                        if (dataSnapshot.exists()) {
                            list.clear();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                SubCategoryModel model = dataSnapshot1.getValue(SubCategoryModel.class);
                                if (model != null && model.getCategoryName() != null) {
                                    model.setKey(dataSnapshot1.getKey());
                                    list.add(model);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SubCategoryActivity.this, "No subcategories found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Hide loading dialog
                        hideLoadingDialog();
                        Toast.makeText(SubCategoryActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Handle upload subject button click
        binding.uploadSubject.setOnClickListener(v -> {
            Intent intent = new Intent(SubCategoryActivity.this, UploadSubCategoryActivity.class);
            intent.putExtra("catId", subjectId);
            startActivity(intent);
        });
    }

    /**
     * Set up the loading dialog with Lottie animation.
     */
    private void setupLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog); // Use your loading XML layout
        loadingDialog.setCancelable(false); // Prevent dismissal by the user

        // Ensure Lottie animation starts automatically in the dialog
        LottieAnimationView lottieView = loadingDialog.findViewById(R.id.lottieAnimationView);
        lottieView.playAnimation();
    }

    /**
     * Show the loading dialog.
     */
    private void showLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    /**
     * Hide the loading dialog.
     */
    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
