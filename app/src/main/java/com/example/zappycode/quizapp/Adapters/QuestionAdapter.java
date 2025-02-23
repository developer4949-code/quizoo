package com.example.zappycode.quizapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zappycode.quizapp.Models.QuestionModel;
import com.example.zappycode.quizapp.R;
import com.example.zappycode.quizapp.databinding.SubcategoryDesignBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHOlder> {

    Context context;
    ArrayList<QuestionModel> lists;
    String catId,subCatId;

    public QuestionAdapter(Context context, ArrayList<QuestionModel> lists,String catId,String  subCatId) {
        this.lists = lists;
        this.context=context;
        this.catId=catId;
        this.subCatId=subCatId;
    }

    @NonNull
    @Override
    public ViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_design, parent, false);
        return new ViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHOlder viewHOlder, int i) {
        QuestionModel model = lists.get(i);

        // Set subject name
        viewHOlder.binding.SubCategoryName.setText(model.getQuestion());

        viewHOlder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int position = viewHOlder.getAdapterPosition(); // Get the updated position

                if (position != RecyclerView.NO_POSITION) { // Ensure the position is valid
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete this question?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                FirebaseDatabase.getInstance().getReference().child("subjects")
                                        .child(catId)
                                        .child("subcategories")
                                        .child(subCatId)
                                        .child("questions")
                                        .child(lists.get(position).getKey()) // Use the key at the updated position
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                lists.remove(position); // Remove the item from the list
                                                notifyItemRemoved(position); // Notify the adapter
                                                Toast.makeText(context, "Question deleted successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed to delete question: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

            binding=SubcategoryDesignBinding.bind(itemView);
        }
    }
}
