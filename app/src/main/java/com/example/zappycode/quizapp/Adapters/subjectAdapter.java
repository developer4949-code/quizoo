package com.example.zappycode.quizapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zappycode.quizapp.Models.SubjectModel;
import com.example.zappycode.quizapp.R;
import com.example.zappycode.quizapp.SubCategoryActivity;
import com.example.zappycode.quizapp.databinding.SubjectDesignBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class subjectAdapter extends RecyclerView.Adapter<subjectAdapter.ViewHOlder> {

    Context context;
    ArrayList<SubjectModel> lists;

    public subjectAdapter(Context context,ArrayList<SubjectModel> lists) {
        this.lists = lists;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_design, parent, false);
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
                Intent intent=new Intent(context, SubCategoryActivity.class);
                intent.putExtra("catId",model.getKey());
                context.startActivity(intent);
            }
        });

        viewHOlder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this subject?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            FirebaseDatabase.getInstance().getReference().child("subjects")
                                    .child(model.getKey())
                                    .removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Remove the item from the list
                                            lists.remove(i); // Remove the item at the current position
                                            notifyItemRemoved(i); // Notify the adapter that the item was removed
                                            Toast.makeText(context, "Subject deleted successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Failed to delete subject: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.cancel());

                // Show the AlertDialog
                builder.create().show();

                return true;
            }
        });


    }



    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHOlder extends RecyclerView.ViewHolder {
        SubjectDesignBinding binding;
        public ViewHOlder(@NonNull View itemView) {

            super(itemView);

            binding=SubjectDesignBinding.bind(itemView);
        }
    }
}
