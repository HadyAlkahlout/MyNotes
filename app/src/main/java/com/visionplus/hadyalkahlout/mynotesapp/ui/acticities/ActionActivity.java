package com.visionplus.hadyalkahlout.mynotesapp.ui.acticities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.visionplus.hadyalkahlout.mynotesapp.R;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.main.CategoriesFragment;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.functhions.CreateCategoryFragment;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.main.ProfileFragment;
import com.visionplus.hadyalkahlout.mynotesapp.ui.fragments.settings.SettingsFragment;

public class ActionActivity extends AppCompatActivity {

    BottomAppBar bottomAppBar;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        replaceFragment(new CategoriesFragment());

        bottomAppBar = findViewById(R.id.bottomAppbar);
        setSupportActionBar(bottomAppBar);
        floatingActionButton = findViewById(R.id.floatBtnAdd);

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.itemMenu :
                        replaceFragment(new CategoriesFragment());
                        break;
                    case R.id.itemProfile :
                        replaceFragment(new ProfileFragment());
                        break;
                    case R.id.itemSettings :
                        replaceFragment(new SettingsFragment());
                        break;
                }

                return true;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences shared = v.getContext().getSharedPreferences("shared", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putInt("edit",0);
                editor.commit();
                replaceFragment(new CreateCategoryFragment());
            }
        });


    }

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentActionHolder, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }
}
