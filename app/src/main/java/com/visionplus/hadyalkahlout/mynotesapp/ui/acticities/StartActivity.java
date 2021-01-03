package com.visionplus.hadyalkahlout.mynotesapp.ui.acticities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.visionplus.hadyalkahlout.mynotesapp.R;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.auth.LoginFragment;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.auth.SplashFragment;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        int open = getIntent().getIntExtra("open",0);

        if (open == 0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new SplashFragment()).commit();
        }else if (open == 1){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new LoginFragment()).commit();
        }
    }
}
