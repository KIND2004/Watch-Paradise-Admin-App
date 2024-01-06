package lk.xrontech.watchparadiseadmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import lk.xrontech.watchparadiseadmin.R;
import lk.xrontech.watchparadiseadmin.fragment.AddProductFragment;
import lk.xrontech.watchparadiseadmin.fragment.HomeFragment;
import lk.xrontech.watchparadiseadmin.fragment.ManageBrandsFragment;
import lk.xrontech.watchparadiseadmin.fragment.ManageProductsFragment;
import lk.xrontech.watchparadiseadmin.fragment.ManageUsersFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar materialToolbar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        materialToolbar = findViewById(R.id.materialToolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar(materialToolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        loadFragment(new HomeFragment());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.home) {
            loadFragment(new HomeFragment());
        } else if (item.getItemId() == R.id.addProduct) {
            loadFragment(new AddProductFragment());
        } else if (item.getItemId() == R.id.manageProducts) {
            loadFragment(new ManageProductsFragment());
        } else if (item.getItemId() == R.id.manageUsers) {
            loadFragment(new ManageUsersFragment());
        } else if (item.getItemId() == R.id.manageBrand) {
            loadFragment(new ManageBrandsFragment());
        } else if (item.getItemId() == R.id.logout) {
            SharedPreferences.Editor editor = getSharedPreferences("admin_details", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(getApplicationContext(), AuthActivity.class));
            firebaseAuth.signOut();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }
}