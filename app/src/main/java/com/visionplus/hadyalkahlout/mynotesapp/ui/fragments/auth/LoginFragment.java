package com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.auth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.visionplus.hadyalkahlout.mynotesapp.ui.acticities.ActionActivity;
import com.visionplus.hadyalkahlout.mynotesapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    private LinearLayout head, body, footer;
    private EditText email, password;
    private TextView signup;
    private Button login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        head = root.findViewById(R.id.layoutLoginHead);
        body = root.findViewById(R.id.layoutLoginBody);
        footer = root.findViewById(R.id.layoutLoginFooter);
        email = root.findViewById(R.id.edLoginEmail);
        password = root.findViewById(R.id.edLoginPassword);
        signup = root.findViewById(R.id.tvSignUp);
        login = root.findViewById(R.id.btnLogin);

        Animation headAnim = AnimationUtils.loadAnimation(getContext(), R.anim.down_move_anim);
        head.startAnimation(headAnim);
        Animation bodyAnim = AnimationUtils.loadAnimation(getContext(), R.anim.left_move_anim);
        body.startAnimation(bodyAnim);
        Animation footerAnim = AnimationUtils.loadAnimation(getContext(), R.anim.right_move_anim);
        footer.startAnimation(footerAnim);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new SignupFragment()).commit();
            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.login_massege));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.custom_dialog);
                TextView head = dialog.findViewById(R.id.tvDialogTitel);
                final TextView body = dialog.findViewById(R.id.tvDialogText);
                Button ok = dialog.findViewById(R.id.btnOk);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                if (email.getText().toString().equals("") || password.getText().toString().equals("")){
                    progressDialog.dismiss();
                    head.setText(R.string.signin_failed);
                    body.setText(R.string.empty_massage);
                    dialog.show();
                }else{
                    db.collection("users")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    Boolean done = false;
                                    for (DocumentSnapshot user : queryDocumentSnapshots.getDocuments()){
                                        if (user.getString("email").equals(email.getText().toString())
                                                && user.getString("password").equals(password.getText().toString())){
                                            done = false;
                                            progressDialog.dismiss();
                                            SharedPreferences shared = getActivity().getSharedPreferences("shared", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = shared.edit();
                                            editor.putInt("active", 1);
                                            editor.putString("userID", user.getId());
                                            editor.apply();
                                            getActivity().startActivity(new Intent(getActivity(), ActionActivity.class));
                                            getActivity().finish();
                                        }else {
                                            done = true;
                                        }
                                    }
                                    if (done){
                                        progressDialog.dismiss();
                                        body.setText(R.string.wrong_login);
                                        dialog.show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), R.string.wrong_massege, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        return root;
    }
}
