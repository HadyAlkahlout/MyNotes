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
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.visionplus.hadyalkahlout.mynotesapp.R;
import com.visionplus.hadyalkahlout.mynotesapp.adapers.CategoryRecyclerAdapter;
import com.visionplus.hadyalkahlout.mynotesapp.models.CategoryItem;
import com.visionplus.hadyalkahlout.mynotesapp.models.SwipeToDeleteCallback;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    public CategoriesFragment() {
        // Required empty public constructor
    }

    private RecyclerView categories;
    private TextView empty;
    private CategoryRecyclerAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_categories, container, false);

        categories = root.findViewById(R.id.rcCategories);
        empty = root.findViewById(R.id.tvEmpty);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.get_data));
        progressDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<CategoryItem> data = new ArrayList<>();
        db.collection("categories")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            for (DocumentSnapshot category : queryDocumentSnapshots.getDocuments()) {
                                CategoryItem item = new CategoryItem(category.getId(), category.getString("name"), category.getString("details"));
                                data.add(item);
                            }
                            enableSwipeToDeleteAndUndo();
                            empty.setVisibility(View.GONE);
                            categories.setVisibility(View.VISIBLE);
                            adapter = new CategoryRecyclerAdapter(getContext(),data);
                            categories.setAdapter(adapter);
                            categories.setLayoutManager(new LinearLayoutManager(getContext()));

                            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.top_move_anim);
                            categories.startAnimation(anim);
                        }
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
        itemTouchhelper.attachToRecyclerView(categories);
    }
}
