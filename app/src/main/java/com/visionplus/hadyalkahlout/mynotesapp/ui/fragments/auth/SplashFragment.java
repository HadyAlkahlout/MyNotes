package com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.visionplus.hadyalkahlout.mynotesapp.ui.acticities.ActionActivity;
import com.visionplus.hadyalkahlout.mynotesapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_splash, container, false);

        LinearLayout layout = root.findViewById(R.id.layoutSplashLogo);

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        layout.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences shared = getActivity().getSharedPreferences("shared", Context.MODE_PRIVATE);
                int active = shared.getInt("active",0);
                if (active == 1){
                    getActivity().startActivity(new Intent(getActivity(), ActionActivity.class));
                    getActivity().finish();
                }else{
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new LoginFragment()).commit();
                }
            }
        }, 5000);

        return root;
    }
}
