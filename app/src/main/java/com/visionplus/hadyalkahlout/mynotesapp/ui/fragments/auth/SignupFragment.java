package com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.auth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.visionplus.hadyalkahlout.mynotesapp.ui.acticities.ActionActivity;
import com.visionplus.hadyalkahlout.mynotesapp.R;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.auth.LoginFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {

    public SignupFragment() {
        // Required empty public constructor
    }

    private LinearLayout head, body, footer;
    private EditText firstName, lastName, email, phone, password;
    private ImageView back;
    private Button signup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_signup, container, false);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        head = root.findViewById(R.id.layoutSignupHead);
        body = root.findViewById(R.id.layoutSignupBody);
        footer = root.findViewById(R.id.layoutSignupFooter);
        firstName = root.findViewById(R.id.edFirstName);
        lastName = root.findViewById(R.id.edLastName);
        email = root.findViewById(R.id.edSignupEmail);
        phone = root.findViewById(R.id.edPhone);
        password = root.findViewById(R.id.edSignupPassword);
        back = root.findViewById(R.id.imgSignupBack);
        signup = root.findViewById(R.id.btnSignup);


        Animation headAnim = AnimationUtils.loadAnimation(getContext(), R.anim.down_move_anim);
        head.startAnimation(headAnim);
        back.startAnimation(headAnim);
        Animation bodyAnim = AnimationUtils.loadAnimation(getContext(), R.anim.left_move_anim);
        body.startAnimation(bodyAnim);
        Animation footerAnim = AnimationUtils.loadAnimation(getContext(), R.anim.right_move_anim);
        footer.startAnimation(footerAnim);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new LoginFragment()).commit();
            }
        });

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog);
        TextView head = dialog.findViewById(R.id.tvDialogTitel);
        final TextView body = dialog.findViewById(R.id.tvDialogText);
        Button ok = dialog.findViewById(R.id.btnOk);
        head.setText(R.string.signup_failed);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.make_account));

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (firstName.getText().toString().equals("") || lastName.getText().toString().equals("") || email.getText().toString().equals("") || phone.getText().toString().equals("") || password.getText().toString().equals("")) {
                    progressDialog.dismiss();
                    body.setText(R.string.empty_massage);
                    dialog.show();
                } else {
                    boolean valid = true;
                    if (!nameCheck(firstName.getText().toString())) {
                        progressDialog.dismiss();
                        body.setText(R.string.fname_wrong);
                        dialog.show();
                        valid = false;
                    }
                    if (!nameCheck(lastName.getText().toString())) {
                        progressDialog.dismiss();
                        body.setText(R.string.lname_wrong);
                        dialog.show();
                        valid = false;
                    }
                    if (!passCheck(password.getText().toString())) {
                        progressDialog.dismiss();
                        body.setText(R.string.password_wrong);
                        dialog.show();
                        valid = false;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                        progressDialog.dismiss();
                        body.setText(getString(R.string.email_wrong));
                        dialog.show();
                        valid = false;
                    }

                    if (valid) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("firstname", firstName.getText().toString());
                        user.put("lasttname", lastName.getText().toString());
                        user.put("email", email.getText().toString());
                        user.put("phone", phone.getText().toString());
                        user.put("password", password.getText().toString());

                        db.collection("users")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        progressDialog.dismiss();
                                        SharedPreferences shared = getActivity().getSharedPreferences("shared", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = shared.edit();
                                        editor.putInt("active", 1);
                                        editor.putString("userID", documentReference.getId());
                                        editor.apply();
                                        Toast.makeText(getContext(), R.string.success_signup, Toast.LENGTH_SHORT).show();
                                        getActivity().startActivity(new Intent(getActivity(), ActionActivity.class));
                                        getActivity().finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("hdhd", e.getMessage());
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), R.string.wrong_massege, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });


        return root;
    }

    private boolean passCheck(String password) {
        boolean lowerc = false;
        boolean digetc = false;
        boolean schar = false;
        if (password.length() >= 8) {
            for (int i = 0; i < password.length(); i++) {
                if (Character.isLowerCase(password.charAt(i))) {
                    lowerc = true;
                    break;
                }
            }
            for (int i = 0; i < password.length(); i++) {
                if (Character.isDigit(password.charAt(i))) {
                    digetc = true;
                    break;
                }
            }
            for (int i = 0; i < password.length(); i++) {
                if (password.charAt(i) == '@' || password.charAt(i) == '#' || password.charAt(i) == '$' || password.charAt(i) == '%' || password.charAt(i) == '&') {
                    schar = true;
                    break;
                }
            }
        }

        if (lowerc && digetc && schar) {
            return true;
        }
        return false;
    }


    private boolean nameCheck(String name) {
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

        if (upperc && lowerc) {
            return true;
        }
        return false;
    }
}
