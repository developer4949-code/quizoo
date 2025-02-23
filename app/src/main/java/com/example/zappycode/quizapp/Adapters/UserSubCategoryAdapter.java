package com.example.zappycode.quizapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zappycode.quizapp.Models.SubCategoryModel;
import com.example.zappycode.quizapp.QuestionsActivity;
import com.example.zappycode.quizapp.R;
import com.example.zappycode.quizapp.UserQuestionsActivity;
import com.example.zappycode.quizapp.databinding.SubcategoryDesignBinding;
import com.example.zappycode.quizapp.databinding.UserSubcategoryDesignBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserSubCategoryAdapter extends RecyclerView.Adapter<UserSubCategoryAdapter.ViewHOlder> {

    Context context;
    ArrayList<SubCategoryModel> lists;
    private String catId;
    private String subCatId;

    public UserSubCategoryAdapter(Context context, ArrayList<SubCategoryModel> lists, String catId) {
        this.lists = lists;
        this.context=context;
        this.catId=catId;

    }



    @NonNull
    @Override
    public ViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_subcategory_design, parent, false);
        return new ViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHOlder viewHOlder, int i) {
        SubCategoryModel model = lists.get(i);

        // Set subject name
        viewHOlder.binding.SubCategoryName.setText(model.getCategoryName());
        viewHOlder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, UserQuestionsActivity.class);
                intent.putExtra("catId",catId);
                intent.putExtra("subCatId",model.getKey());

                context.startActivity(intent);
            }
        });


    }



    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHOlder extends RecyclerView.ViewHolder {
        UserSubcategoryDesignBinding binding;
        public ViewHOlder(@NonNull View itemView) {

            super(itemView);

            binding= UserSubcategoryDesignBinding.bind(itemView);
        }
    }
}
