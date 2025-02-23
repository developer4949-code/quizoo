package com.example.zappycode.quizapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.zappycode.quizapp.Models.SubCategoryModel;
import com.example.zappycode.quizapp.databinding.ActivitySubCategoryBinding;
import com.example.zappycode.quizapp.databinding.ActivityUploadSubCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

public class UploadSubCategoryActivity extends AppCompatActivity {

    ActivityUploadSubCategoryBinding binding;
    FirebaseDatabase  database;
    Dialog loadingdialog;
    private String subjectId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityUploadSubCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        database=FirebaseDatabase.getInstance();
        subjectId=getIntent().getStringExtra("catId");

        loadingdialog = new Dialog(this);
        loadingdialog.setContentView(R.layout.loading_dialog);
        loadingdialog.setCancelable(true);

        binding.uploadSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  String subCategoryName=binding.editSubCategory.getText().toString();
                  if(subCategoryName.isEmpty())
                  {
                      binding.editSubCategory.setError("Enter subcategory name");
                  }
                  else
                  {
                      storeData(subCategoryName);
                  }

            }
        });

    }

    private void storeData(String subCategoryName) {

        loadingdialog.show();
        SubCategoryModel model=new SubCategoryModel(subCategoryName);
        database.getReference().child("subjects").child(subjectId).child("subcategories")
                .push()
                .setValue(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loadingdialog.dismiss();
                        Toast.makeText(UploadSubCategoryActivity.this, "Data  uploaded successfully", Toast.LENGTH_SHORT).show();

                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingdialog.dismiss();
                        Toast.makeText(UploadSubCategoryActivity.this,"Something went wrong ",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}