package fr.flareden.meetingcar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import fr.flareden.meetingcar.databinding.ActivityMainBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.Metier;

public class MainActivity extends AppCompatActivity {
    // NAVIGATION DRAWER
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BINDING
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // NAVIGATION DRAWER
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_mail, R.id.nav_follow, R.id.nav_announces, R.id.nav_history, R.id.nav_login)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // LOGIN BUTTON
        findViewById(R.id.buttonLogin).setOnClickListener((View view) -> {
            if (!CommunicationWebservice.CONNECTED) {
                navController.popBackStack();
                navController.navigate(R.id.nav_login);
                drawer.closeDrawer((int) Gravity.START);

            } else {
                Metier.getINSTANCE().disconnect();
                Button b = findViewById(R.id.buttonLogin);
                b.setText(R.string.action_sign_in);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}