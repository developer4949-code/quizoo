package com.example.zappycode.quizapp;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zappycode.quizapp.databinding.ActivityScoreBinding;

public class scoreActivity extends AppCompatActivity {

    private String time_taken, correct, wrong, total_question;
    ActivityScoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        time_taken = intent.getStringExtra("time_taken");
        correct = intent.getStringExtra("correct");
        wrong = intent.getStringExtra("wrong");
        total_question = intent.getStringExtra("total_question");

        binding.timeTaken.setText(time_taken);
        binding.totalQues.setText(total_question);
        binding.score.setText(correct);

        float success_ratio=((float)Integer.parseInt(correct)/Integer.parseInt(total_question))*100;
        binding.success.setText(success_ratio+"%");


        // Use try-catch to safely parse the numbers
        try {
            int totalQuestions = Integer.parseInt(total_question);
            int correctAnswers = Integer.parseInt(correct);

            // Check if the parsed values are within valid ranges
            if (totalQuestions > 0) {
                float progress = ((float) correctAnswers / totalQuestions) * 100;

                // Get reference to the CircularProgressView
                CircularProgressView circularProgressView = findViewById(R.id.circularProgressView);

                // Start animation with progress update
                circularProgressView.startProgressAnimation(0, progress);
            } else {
                Toast.makeText(this, "Total questions must be greater than 0.", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            // Handle invalid number format exceptions
            Toast.makeText(this, "Invalid data format. Please check the input values.", Toast.LENGTH_SHORT).show();
        }
    }
}
