package com.example.cardsvshumanity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.cardsvshumanity.jugarPerfil.ajustesFragment;
import com.example.cardsvshumanity.jugarPerfil.perfilFragment;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    CargarFragmente(new principalFragment());
                    return true;
                case R.id.navigation_profile:
                    CargarFragmente(new perfilFragment());
                    return true;
                case R.id.navigation_settings:
                    CargarFragmente(new ajustesFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("CCH");
        BottomNavigationView navView = findViewById(R.id.navigationView);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        CargarFragmente(new principalFragment());
        navView.getMenu().getItem(0).setChecked(true);
    }


    private void CargarFragmente(Fragment fragment){
        FragmentManager manager=getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.contendedorFragmento,fragment).commit();
    }
}
