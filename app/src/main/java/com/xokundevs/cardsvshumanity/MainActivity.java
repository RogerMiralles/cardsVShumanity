package com.xokundevs.cardsvshumanity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.xokundevs.cardsvshumanity.fragsBar.AjustesFragment;
import com.xokundevs.cardsvshumanity.fragsBar.PerfilFragment;
import com.xokundevs.cardsvshumanity.fragsBar.PrincipalFragment;


public class MainActivity extends AppCompatActivity {

    private int window;
    private BottomNavigationView navView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Connection.crearCon();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean bool = true;
        if(savedInstanceState != null){
            window = savedInstanceState.getInt("window");
            bool = false;
        }
        else {
            window = 1;
        }

        bindViews();
        setUpViews();

        UpdateFragment(bool);
    }

    public void bindViews(){
        navView = findViewById(R.id.navigationView);
    }

    private void setUpViews(){
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

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navView.getMenu().getItem(window).setChecked(true);
    }

    /**
     * Nombre: UpdateFragment
     * Desc: Usa la funcion CargarFragmente para enseñar el fragment correspondiente
     * @param bool primera vez que se muestra pantalla
     */
    private void UpdateFragment(boolean bool){
        switch (window){
            case 0:
                CargarFragmente(new PerfilFragment());
                break;
            case 1:
                Fragment f = new PrincipalFragment();
                Bundle b = new Bundle();
                b.putBoolean("first", bool);
                f.setArguments(b);
                CargarFragmente(f);
                break;
            case 2:
                CargarFragmente(new AjustesFragment());
                break;
        }
    }

    /**
     * Nombre: CargarFragmente
     * Desc: Remplaza el contenido de un Relative Layout llamado "contenedorFragmento"
     *       por el contenido de un fragment
     * @param fragment fragmento a cargar
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
