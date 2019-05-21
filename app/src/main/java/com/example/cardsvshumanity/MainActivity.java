package com.example.cardsvshumanity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.cardsvshumanity.fragsBar.ajustesFragment;
import com.example.cardsvshumanity.fragsBar.perfilFragment;
import com.example.cardsvshumanity.fragsBar.principalFragment;


public class MainActivity extends AppCompatActivity {

    private int window;
    private BottomNavigationView navView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Connection.crearCon();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle(getString(R.string.cch));
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean bool = true;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        window = 1;
                        break;
                    case R.id.navigation_profile:
                        window = 0;
                        break;
                    case R.id.navigation_settings:
                        window = 2;
                        break;
                    default:
                        bool = false;
                        break;
                }
                UpdateFragment(true);
                return bool;
            }
        };
        navView = findViewById(R.id.navigationView);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        boolean bool = true;
        if(savedInstanceState != null){
            window = savedInstanceState.getInt("window");
            bool = false;
        }
        else {
            window = 1;
        }
        navView.getMenu().getItem(window).setChecked(true);
        UpdateFragment(bool);
    }


    /**
     * 
     * @param bool
     */
    private void UpdateFragment(boolean bool){
        switch (window){
            case 0:
                CargarFragmente(new perfilFragment());
                break;
            case 1:
                Fragment f = new principalFragment();
                Bundle b = new Bundle();
                b.putBoolean("first", bool);
                f.setArguments(b);
                CargarFragmente(f);
                break;
            case 2:
                CargarFragmente(new ajustesFragment());
                break;
        }
    }

    /**
     * Nombre: CargarFragmente
     * Desc: Remplaza el contenido de un Relative Layout llamado "contenedorFragmento"
     *       por el contenido de un fragment
     * @param fragment
     */
    private void CargarFragmente(Fragment fragment){
        FragmentManager manager=getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.contendedorFragmento,fragment).commit();
    }


    /**
     * Nombre: onSaveInstanceState
     * Desc:
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("window", window);
        super.onSaveInstanceState(outState);
    }
}
