package com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.main;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.visionplus.hadyalkahlout.mynotesapp.R;
import com.visionplus.hadyalkahlout.mynotesapp.adapers.NoteRecyclerAdapter;
import com.visionplus.hadyalkahlout.mynotesapp.models.NoteItem;
import com.visionplus.hadyalkahlout.mynotesapp.models.SwipeToDeleteCallback;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.functhions.CreateNoteFragment;

import java.util.ArrayList;

public class NotesFragment extends Fragment {
    public NotesFragment() {
        // Required empty public constructor
    }

    private ImageView add;
    private ImageView back;
    private TextView appbarHead, empty;
    private RecyclerView notes;
    private ArrayList<NoteItem> data = new ArrayList<>();
    private NoteRecyclerAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notes, container, false);
        back = root.findViewById(R.id.imgNoteBack);
        add = root.findViewById(R.id.imgAdd);
        appbarHead = root.findViewById(R.id.appbarNoteHead);
        empty = root.findViewById(R.id.tvNoteEmpty);
        notes = root.findViewById(R.id.rcNotes);
        String id = "";
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("id");
        }

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.get_data));
        progressDialog.show();

        db.collection("categories").document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        appbarHead.setText(documentSnapshot.getString("name"));
                    }
                });

        db.collection("notes").whereEqualTo("categoryId", id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            for (DocumentSnapshot note : queryDocumentSnapshots.getDocuments()) {
                                NoteItem item = new NoteItem(
                                        note.getId(),
                                        note.getString("categoryId"),
                                        note.getString("name"),
                                        note.getString("description"),
                                        Integer.parseInt(note.get("done").toString())
                                );
                                data.add(item);
                            }

                            empty.setVisibility(View.GONE);
                            notes.setVisibility(View.VISIBLE);
                            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.top_move_anim);
                            notes.startAnimation(anim);
                            enableSwipeToDeleteAndUndo();
                            adapter = new NoteRecyclerAdapter(getContext(), data);
                            notes.setAdapter(adapter);
                            notes.setLayoutManager(new LinearLayoutManager(getContext()));
                        }
                    }
                });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentActionHolder, new CategoriesFragment()).commit();
            }
        });


        final String catID = id;

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNoteFragment fragment = new CreateNoteFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", catID);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentActionHolder, fragment).commit();
            }
        });

        progressDialog.dismiss();

        return root;
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();

                adapter.removeItem(position);

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(notes);
    }
}
