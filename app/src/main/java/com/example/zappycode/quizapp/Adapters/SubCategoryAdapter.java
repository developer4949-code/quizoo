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
import com.example.zappycode.quizapp.SubCategoryActivity;
import com.example.zappycode.quizapp.databinding.SubcategoryDesignBinding;
import com.example.zappycode.quizapp.databinding.SubjectDesignBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHOlder> {

    Context context;
    ArrayList<SubCategoryModel> lists;
    private String catId;
    private String subCatId;

    public SubCategoryAdapter(Context context, ArrayList<SubCategoryModel> lists,String catId) {
        this.lists = lists;
        this.context=context;
        this.catId=catId;

    }



    @NonNull
    @Override
    public ViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_design, parent, false);
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
                Intent intent=new Intent(context, QuestionsActivity.class);
                intent.putExtra("catId",catId);
                intent.putExtra("subCatId",model.getKey());

                context.startActivity(intent);
            }
        });

        viewHOlder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int position = viewHOlder.getAdapterPosition(); // Get the updated position

                if (position != RecyclerView.NO_POSITION) { // Ensure the position is valid
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete this category?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                FirebaseDatabase.getInstance().getReference().child("subjects")
                                        .child(catId)
                                        .child("subcategories")
                                        .child(lists.get(position).getKey()) // Use the key at the updated position
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                lists.remove(position); // Remove the item from the list
                                                notifyItemRemoved(position); // Notify the adapter
                                                Toast.makeText(context, "Category deleted successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed to delete category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.cancel());

                    builder.create().show();
                }
                return true;
            }
        });

    }



    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHOlder extends RecyclerView.ViewHolder {
        SubcategoryDesignBinding binding;
        public ViewHOlder(@NonNull View itemView) {

            super(itemView);

            binding= SubcategoryDesignBinding.bind(itemView);
        }
    }
}
