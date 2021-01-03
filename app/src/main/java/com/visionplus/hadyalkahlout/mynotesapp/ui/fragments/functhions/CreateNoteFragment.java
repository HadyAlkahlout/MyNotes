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
import com.google.firebase.firestore.FirebaseFirestore;
import com.visionplus.hadyalkahlout.mynotesapp.R;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.main.NotesFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateNoteFragment extends Fragment {

    public CreateNoteFragment() {
        // Required empty public constructor
    }

    private ImageView back;
    private EditText name, details;
    private Button save;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_note, container, false);
        back = root.findViewById(R.id.imgCreateNoteBack);
        name = root.findViewById(R.id.tvNoteTitle);
        details = root.findViewById(R.id.tvNoteDetails);
        save = root.findViewById(R.id.btnNewNoteSave);

        SharedPreferences shared = getActivity().getSharedPreferences("shared", Context.MODE_PRIVATE);
        final String user = shared.getString("userID", "");


        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("id");
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("") || details.getText().toString().equals("")){
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
                }else{
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage(getString(R.string.add_note));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Map<String, Object> note = new HashMap<>();
                    note.put("user", user);
                    note.put("categoryId", id);
                    note.put("name", name.getText().toString());
                    note.put("description", details.getText().toString());
                    note.put("done", 0);
                    db.collection("notes")
                            .add(note)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), R.string.note_creation_success, Toast.LENGTH_SHORT).show();
                                    NotesFragment fragment = new NotesFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id", id);
                                    fragment.setArguments(bundle);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentActionHolder, fragment).commit();
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
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotesFragment fragment = new NotesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentActionHolder, fragment).commit();
            }
        });
        return root;
    }
}
