package com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.settings;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.visionplus.hadyalkahlout.mynotesapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    private ConstraintLayout layout;
    private LinearLayout content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about, container, false);

        layout = root.findViewById(R.id.layoutAbout);
        content = root.findViewById(R.id.layoutContent);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentActionHolder, new SettingsFragment()).commit();
            }
        });


        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        layout.startAnimation(anim);
        Animation anim1 = AnimationUtils.loadAnimation(getContext(), R.anim.top_move_anim);
        content.startAnimation(anim1);

        return root;
    }
}
