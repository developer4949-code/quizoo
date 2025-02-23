package com.example.zappycode.quizapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.zappycode.quizapp.Models.QuestionModel;
import com.example.zappycode.quizapp.databinding.ActivityUploadQuestionBinding;
import com.example.zappycode.quizapp.databinding.LoadingDialogBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

public class UploadQuestionActivity extends AppCompatActivity {

    ActivityUploadQuestionBinding binding;
     FirebaseDatabase database;

     RadioGroup options;

     LinearLayout answers;

     private String catId,subCatId;
     Dialog loadingdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUploadQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();

        catId=getIntent().getStringExtra("catId");
        subCatId=getIntent().getStringExtra("subCatId");

        database=FirebaseDatabase.getInstance();

        options=findViewById(R.id.options);
        answers=findViewById(R.id.answers);

        loadingdialog = new Dialog(this);
        loadingdialog.setContentView(R.layout.loading_dialog);
        loadingdialog.setCancelable(true);


        binding.uploadQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = binding.question.getText().toString();
                if (question.isEmpty()) {
                    binding.question.setError("Question is required");
                    return;
                }

                // Validate all options
                for (int i = 0; i < answers.getChildCount(); i++) {
                    EditText answ = (EditText) answers.getChildAt(i);
                    if (answ.getText().toString().isEmpty()) {
                        answ.setError("Required");
                        return;
                    }
                }

                // Check which radio button is selected
                int checkedId = options.getCheckedRadioButtonId();
                if (checkedId == -1) {
                    Toast.makeText(UploadQuestionActivity.this, "Select Correct Answer", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton selectedButton = findViewById(checkedId);
                int correct = options.indexOfChild(selectedButton);

                loadingdialog.show();

                // Prepare QuestionModel
                QuestionModel model = new QuestionModel();
                model.setQuestion(question);
                model.setAnswer(((EditText) answers.getChildAt(correct)).getText().toString());
                model.setOptionA(((EditText) answers.getChildAt(0)).getText().toString());
                model.setOptionB(((EditText) answers.getChildAt(1)).getText().toString());
                model.setOptionC(((EditText) answers.getChildAt(2)).getText().toString());
                model.setOptionD(((EditText) answers.getChildAt(3)).getText().toString());

                // Upload to Firebase
                database.getReference().child("subjects").child(catId).child("subcategories").child(subCatId)
                        .child("questions")
                        .push()
                        .setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                loadingdialog.dismiss();
                                Toast.makeText(UploadQuestionActivity.this, "Question uploaded successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingdialog.dismiss();
                                Toast.makeText(UploadQuestionActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



    }
}