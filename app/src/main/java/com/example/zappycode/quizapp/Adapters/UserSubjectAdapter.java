package com.example.zappycode.quizapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zappycode.quizapp.Models.SubjectModel;
import com.example.zappycode.quizapp.R;
import com.example.zappycode.quizapp.UserSubCategoryActivity;
import com.example.zappycode.quizapp.databinding.UserSubjectDesignBinding;

import java.util.ArrayList;

public class UserSubjectAdapter extends RecyclerView.Adapter<UserSubjectAdapter.ViewHOlder> {

    Context context;
    ArrayList<SubjectModel> lists;

    public UserSubjectAdapter(Context context, ArrayList<SubjectModel> lists) {
        this.lists = lists;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_subject_design, parent, false);
        return new ViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHOlder viewHOlder, int i) {
        SubjectModel model = lists.get(i);

        // Set subject name
        viewHOlder.binding.subjectName.setText(model.getSubjectName());

        // Decode Base64 image string
        if (model.getImageBase64() != null) {
            byte[] decodedString = Base64.decode(model.getImageBase64(), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            viewHOlder.binding.subjectImages.setImageBitmap(decodedBitmap);
        }
        viewHOlder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, UserSubCategoryActivity.class);
                intent.putExtra("catId",model.getKey());
                intent.putExtra("subjectName",model.getSubjectName());
                context.startActivity(intent);
            }
        });



    }



    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHOlder extends RecyclerView.ViewHolder {
        UserSubjectDesignBinding binding;
        public ViewHOlder(@NonNull View itemView) {

            super(itemView);

            binding=UserSubjectDesignBinding.bind(itemView);
        }
    }
}
