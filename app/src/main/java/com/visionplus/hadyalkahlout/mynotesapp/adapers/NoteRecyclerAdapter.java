package com.visionplus.hadyalkahlout.mynotesapp.adapers;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.visionplus.hadyalkahlout.mynotesapp.R;
import com.visionplus.hadyalkahlout.mynotesapp.models.NoteItem;

import java.util.ArrayList;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.MyViewHolder> {

    ArrayList<NoteItem> data;
    LayoutInflater mInflater;
    Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;

    public NoteRecyclerAdapter(Context context, ArrayList<NoteItem> data){
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.note_recycler_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.title.setText(data.get(position).getTitle());
        holder.description.setText(data.get(position).getDescription());
        if (data.get(position).getDone() == 1){
            holder.done.setImageResource(R.drawable.ic_check_do);
        }else{
            holder.done.setImageResource(R.drawable.ic_check_undo);
        }
        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updating data...");
                progressDialog.show();
                if (data.get(position).getDone() == 1){
                    db.collection("notes").document(data.get(position).getId())
                            .update("done", 0)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    holder.done.setImageResource(R.drawable.ic_check_undo);
                                    data.get(position).setDone(0);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Something went wrong!! Try again later.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else if (data.get(position).getDone() == 0){
                    db.collection("notes").document(data.get(position).getId())
                            .update("done", 1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    holder.done.setImageResource(R.drawable.ic_check_do);
                                    data.get(position).setDone(1);
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title, description;
        ImageView done;
        MyViewHolder(View item){
            super(item);
            this.title = item.findViewById(R.id.tvNoteTitle);
            this.description = item.findViewById(R.id.tvNoteDescription);
            this.done = item.findViewById(R.id.imgNoteUndo);
        }
    }

    public void removeItem(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        TextView title = dialog.findViewById(R.id.tvDialogTitel);
        TextView text = dialog.findViewById(R.id.tvDialogText);
        Button ok = dialog.findViewById(R.id.btnOk);
        title.setText("Deleting Confirm");
        text.setText("Are you want to delete this note ?");
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Deleting note...");
                progressDialog.show();
                db.collection("notes").document(data.get(position).getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                data.remove(position);
                                notifyItemRemoved(position);
                                progressDialog.dismiss();
                                dialog.cancel();
                                Toast.makeText(context, "Note deleted successfully!!", Toast.LENGTH_SHORT).show();
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