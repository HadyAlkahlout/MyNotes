package com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.visionplus.hadyalkahlout.mynotesapp.R;
import com.visionplus.hadyalkahlout.mynotesapp.ui.acticities.StartActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    private CardView about, logout;
    private LinearLayout content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        about = root.findViewById(R.id.cardAbout);
        logout = root.findViewById(R.id.cardLogout);
        content = root.findViewById(R.id.layoutChoices);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.top_move_anim);
        content.startAnimation(anim);


        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentActionHolder, new AboutFragment()).commit();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences shared = getActivity().getSharedPreferences("shared", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putInt("active", 0);
                editor.apply();
                Intent i = new Intent(getActivity(), StartActivity.class);
                i.putExtra("open", 1);
                getActivity().startActivity(i);
            }
        });

        return root;
    }
}
