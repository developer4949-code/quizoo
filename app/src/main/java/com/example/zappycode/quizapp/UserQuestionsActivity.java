package com.example.zappycode.quizapp;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.zappycode.quizapp.Models.QuestionModel;
import com.example.zappycode.quizapp.databinding.ActivityUserQuestionsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class UserQuestionsActivity extends AppCompatActivity {

    ActivityUserQuestionsBinding binding;
    FirebaseDatabase database;
    ArrayList<QuestionModel> list;
    private int count = 0;
    private int position = 0;
    private int correctAnswer = 0;
    private int wrongAnswer = 0;
    private final long questionTime = 20; // Time in minutes
    private long timeLeft;
    private long startTime; // Store start time
    private CountDownTimer qtimer;
    private String catId, subCatId;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUserQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupLoadingDialog();
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();

        // Retrieve category and subcategory IDs from Intent
        catId = getIntent().getStringExtra("catId");
        subCatId = getIntent().getStringExtra("subCatId");

        showLoadingDialog();
        startTimer();

        binding.SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToScoreActivity();
            }
        });

        database.getReference().child("subjects").child(catId).child("subcategories").child(subCatId)
                .child("questions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        hideLoadingDialog();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                QuestionModel model = ds.getValue(QuestionModel.class);
                                if (model != null) {
                                    model.setKey(ds.getKey());
                                    list.add(model);
                                }
                            }
                            if (!list.isEmpty()) {
                                initializeQuestionUI();
                            }
                        } else {
                            Toast.makeText(UserQuestionsActivity.this, "No questions available. Please try another category.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        hideLoadingDialog();
                        Toast.makeText(UserQuestionsActivity.this, "Failed to load questions. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startTimer() {
        long time = questionTime * 60 * 1000; // Convert minutes to milliseconds
        startTime = time; // Save start time for later calculation

        qtimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                String timeRemaining = String.format("%02d:%02d min",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                binding.timer.setText(timeRemaining);
            }

            @Override
            public void onFinish() {
                navigateToScoreActivity();
                Toast.makeText(UserQuestionsActivity.this, "Time is up!", Toast.LENGTH_SHORT).show();
            }
        };
        qtimer.start();
    }

    private void initializeQuestionUI() {
        // Set click listeners for options
        for (int i = 0; i < 4; i++) {
            binding.optionsToUser.getChildAt(i).setOnClickListener(v -> checkAnswer((Button) v));
        }

        // Start with the first question
        playAnimation(binding.questionToUser, 0, list.get(position).getQuestion());

        binding.NextBtn.setOnClickListener(v -> {
            enableOption(true);
            position++;
            if (position == list.size()) {
                qtimer.cancel();
                navigateToScoreActivity();
            } else {
                count = 0;
                playAnimation(binding.questionToUser, 0, list.get(position).getQuestion());
            }
        });

        binding.SubmitBtn.setOnClickListener(v -> {
            qtimer.cancel();
            navigateToScoreActivity();
        });
    }

    private void playAnimation(View view, int value, final String data) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
                .setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {
                        if (value == 0 && count < 4) {
                            String option = "";
                            switch (count) {
                                case 0:
                                    option = list.get(position).getOptionA();
                                    break;
                                case 1:
                                    option = list.get(position).getOptionB();
                                    break;
                                case 2:
                                    option = list.get(position).getOptionC();
                                    break;
                                case 3:
                                    option = list.get(position).getOptionD();
                                    break;
                            }
                            playAnimation(binding.optionsToUser.getChildAt(count), 0, option);
                            count++;
                        }
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        if (value == 0) {
                            try {
                                ((TextView) view).setText(data);
                                binding.questionCount.setText(String.format("%d/%d", position + 1, list.size()));
                            } catch (Exception e) {
                                ((Button) view).setText(data);
                            }
                            view.setTag(data);
                            playAnimation(view, 1, data);
                        }
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {
                    }
                });
    }

    private void checkAnswer(Button selectedOption) {
        enableOption(false);
        if (selectedOption.getTag().toString().equals(list.get(position).getAnswer())) {
            selectedOption.setBackgroundResource(R.drawable.correct_option);
            correctAnswer++;
        } else {
            wrongAnswer++;
            selectedOption.setBackgroundResource(R.drawable.incorrect_option);
            Button correctOption = binding.optionsToUser.findViewWithTag(list.get(position).getAnswer());
            if (correctOption != null) {
                correctOption.setBackgroundResource(R.drawable.correct_option);
            }
        }
    }

    private void enableOption(boolean enable) {
        for (int i = 0; i < 4; i++) {
            View option = binding.optionsToUser.getChildAt(i);
            option.setEnabled(enable);
            if (enable) {
                option.setBackgroundResource(R.drawable.btn_option_background);
            }
        }
    }

    private void navigateToScoreActivity() {
        // Calculate the time taken (difference between start and current time)
        long totalTime = startTime - timeLeft;

        // Format the time taken
        String formattedTime = String.format("%02d:%02d min",
                TimeUnit.MILLISECONDS.toMinutes(totalTime),
                TimeUnit.MILLISECONDS.toSeconds(totalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime)));

        // Send the formatted time to the next activity
        Intent intent = new Intent(UserQuestionsActivity.this, scoreActivity.class);
        intent.putExtra("time_taken", formattedTime);  // Pass formatted time
        intent.putExtra("correct", String.valueOf(correctAnswer));
        intent.putExtra("wrong", String.valueOf(wrongAnswer));
        intent.putExtra("total_question", String.valueOf(list.size()));
        startActivity(intent);
        finish();
    }

    private void setupLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(false);
        LottieAnimationView lottieView = loadingDialog.findViewById(R.id.lottieAnimationView);
        lottieView.playAnimation();
    }

    private void showLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
