package com.visionplus.hadyalkahlout.mynotesapp.adapers;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.visionplus.hadyalkahlout.mynotesapp.R;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.functhions.CreateCategoryFragment;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.main.NotesFragment;
import com.visionplus.hadyalkahlout.mynotesapp.models.CategoryItem;


import java.util.ArrayList;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.MyViewHolder> {

    ArrayList<CategoryItem> data;
    LayoutInflater mInflater;
    Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;

    public CategoryRecyclerAdapter(Context context, ArrayList<CategoryItem> data){
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.category_recycler_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.shortcut.setText(data.get(position).getName().charAt(0)+"");
        holder.name.setText(data.get(position).getName());
        if (data.get(position).getDetails().length() > 10){
            holder.details.setText(data.get(position).getDetails().substring(0,9) + "...");
        }else {
            holder.details.setText(data.get(position).getDetails());
        }
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCategoryFragment fragment = new CreateCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("edit",1);
                bundle.putString("id", data.get(position).getId());
                fragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentActionHolder, fragment).commit();
            }
        });
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotesFragment fragment = new NotesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", data.get(position).getId());
                fragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentActionHolder, fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{
        TextView shortcut, name, details;
        ImageView edit;
        CardView card;
        MyViewHolder(View item){
            super(item);
            this.shortcut = item.findViewById(R.id.tvCategoryShortcut);
            this.name = item.findViewById(R.id.tvCategoryName);
            this.details = item.findViewById(R.id.tvCategoryDetails);
            this.edit = item.findViewById(R.id.imgEdit);
            this.card = item.findViewById(R.id.list_item_category);
        }
    }

    public void removeItem(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        TextView title = dialog.findViewById(R.id.tvDialogTitel);
        TextView text = dialog.findViewById(R.id.tvDialogText);
        Button ok = dialog.findViewById(R.id.btnOk);
        title.setText("Deleting Confirm");
        text.setText("Are you want to delete this category ?");
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Deleting category...");
                progressDialog.show();
                db.collection("categories").document(data.get(position).getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.collection("notes").whereEqualTo("categoryID", data.get(position).getId())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (queryDocumentSnapshots.size() > 0){
                                                    for (DocumentSnapshot note : queryDocumentSnapshots){
                                                        db.collection("notes").document(note.getId())
                                                                .delete();
                                                    }
                                                }
                                            }
                                        });
                                data.remove(position);
                                notifyItemRemoved(position);
                                progressDialog.dismiss();
                                dialog.cancel();
                                Toast.makeText(context, "Category deleted successfully!!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Something went wrong!! Try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}