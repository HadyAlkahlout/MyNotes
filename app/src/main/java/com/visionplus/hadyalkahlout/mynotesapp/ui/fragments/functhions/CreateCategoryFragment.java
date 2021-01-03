package com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.functhions;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.visionplus.hadyalkahlout.mynotesapp.R;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.main.CategoriesFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateCategoryFragment extends Fragment {

    public CreateCategoryFragment() {
        // Required empty public constructor
    }

    private ImageView back;
    private EditText name, details;
    private Button save;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    int edit = 0;
    String id = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_category, container, false);
        back = root.findViewById(R.id.imgCreateCategoryBack);
        name = root.findViewById(R.id.tvCatName);
        details = root.findViewById(R.id.tvCatDetails);
        save = root.findViewById(R.id.btnNewCatSave);

        SharedPreferences shared = getActivity().getSharedPreferences("shared", Context.MODE_PRIVATE);
        final String user = shared.getString("userID", "");

        Bundle bundle = getArguments();
        if (bundle != null) {
            edit = bundle.getInt("edit");
            id = bundle.getString("id");
        }

        if (edit == 1) {
            db.collection("categories").document(id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            name.setText(documentSnapshot.getString("name"));
                            details.setText(documentSnapshot.getString("details"));
                        }
                    });
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("") || details.getText().toString().equals("")) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.custom_dialog);
                    TextView head = dialog.findViewById(R.id.tvDialogTitel);
                    TextView body = dialog.findViewById(R.id.tvDialogText);
                    Button ok = dialog.findViewById(R.id.btnOk);
                    head.setText(R.string.creation_failed);
                    body.setText(R.string.empty_massage);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setCancelable(false);
                    if (edit == 0) {
                        progressDialog.setMessage(getString(R.string.create_categorys));
                        progressDialog.show();
                        Map<String, String> category = new HashMap<>();
                        category.put("user", user);
                        category.put("name", name.getText().toString());
                        category.put("details", details.getText().toString());
                        db.collection("categories")
                                .add(category)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), R.string.creation_success, Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentActionHolder, new CategoriesFragment()).commit();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), R.string.wrong_massege, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        progressDialog.setMessage(getString(R.string.edit_category));
                        progressDialog.show();
                        db.collection("categories").document(id)
                                .update("name", name.getText().toString(),
                                        "details", details.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), R.string.update_success, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), R.string.wrong_massege, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentActionHolder, new CategoriesFragment()).commit();
            }
        });
        return root;
    }
}
