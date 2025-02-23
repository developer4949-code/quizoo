package com.example.zappycode.quizapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.zappycode.quizapp.Adapters.QuestionAdapter;
import com.example.zappycode.quizapp.Models.QuestionModel;
import com.example.zappycode.quizapp.databinding.ActivityQuestionsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuestionsActivity extends AppCompatActivity {

    ActivityQuestionsBinding binding;
    FirebaseDatabase database;
    QuestionAdapter adapter;
    ArrayList<QuestionModel> list;
    Dialog loadingdialog;
    private String catId,subCatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        catId=getIntent().getStringExtra("catId");
        subCatId=getIntent().getStringExtra("subCatId");

        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();

        // Set up loading dialog
        loadingdialog = new Dialog(this);
        loadingdialog.setContentView(R.layout.loading_dialog);
        loadingdialog.setCancelable(false); // Make it non-cancelable
        loadingdialog.show();

        // Set up GridLayoutManager
        int spanCount = 1;
        GridLayoutManager layoutManager = new GridLayoutManager(this, Math.max(spanCount, 1));
        binding.questions.setLayoutManager(layoutManager);

        // Set up adapter
        adapter = new QuestionAdapter(QuestionsActivity.this, list,catId,subCatId);
        binding.questions.setAdapter(adapter);

        // Retrieve subjects from Firebase
        database.getReference().child("subjects").child(catId).child("subcategories").child(subCatId)
                .child("questions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        loadingdialog.dismiss();
                        if (dataSnapshot.exists()) {
                            list.clear();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    QuestionModel model = dataSnapshot1.getValue(QuestionModel.class);
                                    model.setKey(dataSnapshot1.getKey());
                                    list.add(model);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(QuestionsActivity.this, "No questions found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingdialog.dismiss();
                        Toast.makeText(QuestionsActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        // Handle upload subject button click
        binding.uploadSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(QuestionsActivity.this,UploadQuestionActivity.class);
                intent.putExtra("catId",catId);
                intent.putExtra("subCatId",subCatId);
                startActivity(intent);
            }
        });
    }
}