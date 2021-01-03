package com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.main;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.visionplus.hadyalkahlout.mynotesapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private ImageView edit;
    private TextView shortcut, name, email, categoriesNum, doneNotesNum, waitNotesNum;
    private EditText edFirstName, edLastName, edPhone, edEmail;
    private Button save;
    private FirebaseFirestore db;
    private String fName;
    private String lName;
    private String userEmail;
    private String userPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.get_data));
        progressDialog.show();

        db = FirebaseFirestore.getInstance();
        edit = root.findViewById(R.id.imgProfileInfoEdit);
        shortcut = root.findViewById(R.id.tvProfileShortcut);
        name = root.findViewById(R.id.tvProfileName);
        email = root.findViewById(R.id.tvProfileEmail);
        categoriesNum = root.findViewById(R.id.tvCategoryNum);
        doneNotesNum = root.findViewById(R.id.tvDoneNotesNum);
        waitNotesNum = root.findViewById(R.id.tvWaitNotesNum);
        edFirstName = root.findViewById(R.id.edProfileFirstName);
        edLastName = root.findViewById(R.id.edProfileLastName);
        edPhone = root.findViewById(R.id.edProfilePhone);
        edEmail = root.findViewById(R.id.edProfileEmail);
        save = root.findViewById(R.id.btnProfileSave);

        final SharedPreferences shared = getActivity().getSharedPreferences("shared", Context.MODE_PRIVATE);
        final String userId = shared.getString("userID", "");

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        fName = documentSnapshot.getString("firstname");
                        lName = documentSnapshot.getString("lasttname");
                        userEmail = documentSnapshot.getString("email");
                        userPhone = documentSnapshot.getString("phone");

                        shortcut.setText(fName.toUpperCase().charAt(0) + "");
                        name.setText(fName + " " + lName);
                        email.setText(userEmail);
                        edFirstName.setText(fName);
                        edLastName.setText(lName);
                        edPhone.setText(userPhone);
                        edEmail.setText(userEmail);
                    }
                });

        db.collection("categories").whereEqualTo("user", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        categoriesNum.setText(queryDocumentSnapshots.size()+"");
                    }
                });

        db.collection("notes").whereEqualTo("user", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int done = 0;
                        int wait = 0;
                        for (DocumentSnapshot note : queryDocumentSnapshots) {
                            if (Integer.parseInt(note.get("done").toString()) == 1) {
                                ++done;
                            } else if (Integer.parseInt(note.get("done").toString()) == 0) {
                                ++wait;
                            }
                        }
                        doneNotesNum.setText(done+"");
                        waitNotesNum.setText(wait+"");
                    }
                });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edFirstName.setEnabled(true);
                edLastName.setEnabled(true);
                edPhone.setEnabled(true);
                edEmail.setEnabled(true);
                save.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edFirstName.getText().toString().equals("") || edLastName.getText().toString().equals("") || edPhone.getText().toString().equals("") || edEmail.getText().toString().equals("")) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.custom_dialog);
                    TextView head = dialog.findViewById(R.id.tvDialogTitel);
                    TextView body = dialog.findViewById(R.id.tvDialogText);
                    Button ok = dialog.findViewById(R.id.btnOk);
                    head.setText(R.string.editing_failed);
                    body.setText(R.string.empty_massage);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                } else {
                    boolean valid = true;
                    if (!nameCheck(edFirstName.getText().toString())) {
                        edFirstName.setError(getString(R.string.fname_wrong));
                        valid = false;
                    }
                    if (!nameCheck(edLastName.getText().toString())) {
                        edLastName.setError(getString(R.string.lname_wrong));
                        valid = false;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(edEmail.getText().toString()).matches()) {
                        edEmail.setError(getString(R.string.email_wrong));
                        valid = false;
                    }
                    if (valid) {
                        progressDialog.setMessage(getString(R.string.update_data));
                        progressDialog.show();
                        db.collection("users").document(userId)
                                .update("firstname", edFirstName.getText().toString(),
                                        "lasttname", edLastName.getText().toString(),
                                        "email", edEmail.getText().toString(),
                                        "phone", edPhone.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), R.string.udate_done, Toast.LENGTH_SHORT).show();
                                        fName = edFirstName.getText().toString();
                                        lName = edLastName.getText().toString();
                                        userEmail = edEmail.getText().toString();
                                        userPhone = edPhone.getText().toString();
                                        shortcut.setText(fName.charAt(0) + "");
                                        name.setText(fName + " " + lName);
                                        email.setText(userEmail);
                                        edFirstName.setEnabled(false);
                                        edLastName.setEnabled(false);
                                        edPhone.setEnabled(false);
                                        edEmail.setEnabled(false);
                                        save.setVisibility(View.GONE);
                                        edit.setVisibility(View.VISIBLE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), R.string.wrong_massege, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });

        progressDialog.dismiss();

        return root;
    }


    private boolean nameCheck(@NonNull String name) {
        boolean upperc = false;
        boolean lowerc = false;
        for (int i = 0; i < name.length(); i++) {
            if (Character.isUpperCase(name.charAt(i))) {
                upperc = true;
                break;
            }
        }
        for (int i = 0; i < name.length(); i++) {
            if (Character.isLowerCase(name.charAt(i))) {
                lowerc = true;
                break;
            }
        }

        return upperc && lowerc;
    }
}
