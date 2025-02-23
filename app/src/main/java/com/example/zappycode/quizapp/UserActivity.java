package com.example.zappycode.quizapp;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.zappycode.quizapp.Adapters.UserSubjectAdapter;
import com.example.zappycode.quizapp.Adapters.subjectAdapter;
import com.example.zappycode.quizapp.Models.SubjectModel;
import com.example.zappycode.quizapp.databinding.ActivityAdminBinding;
import com.example.zappycode.quizapp.databinding.ActivityUserBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    ActivityUserBinding binding;
    FirebaseDatabase database;
    UserSubjectAdapter adapter;
    ArrayList<SubjectModel> list;

    Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase and other variables
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();

        // Set up loading dialog
        setupLoadingDialog();

        // Set up GridLayoutManager
        int spanCount = getResources().getConfiguration().screenWidthDp / 180;
        GridLayoutManager layoutManager = new GridLayoutManager(this, Math.max(spanCount, 2));
        binding.subjectCategory.setLayoutManager(layoutManager);

        // Set up adapter
        adapter = new UserSubjectAdapter(UserActivity.this, list);
        binding.subjectCategory.setAdapter(adapter);

        // Show loading dialog
        showLoadingDialog();

        // Retrieve subjects from Firebase
        database.getReference().child("subjects")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Dismiss the loading dialog once data is loaded
                        hideLoadingDialog();

                        if (dataSnapshot.exists()) {
                            list.clear();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                SubjectModel model = dataSnapshot1.getValue(SubjectModel.class);
                                if (model != null && model.getSubjectName() != null && model.getImageBase64() != null) {
                                    // Decode Base64 image string into a Bitmap
                                    model.setKey(dataSnapshot1.getKey());
                                    list.add(model);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(UserActivity.this, "No subjects found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Dismiss the loading dialog in case of error
                        hideLoadingDialog();
                        Toast.makeText(UserActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



    }
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