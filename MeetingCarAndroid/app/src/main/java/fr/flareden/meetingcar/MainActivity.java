package fr.flareden.meetingcar;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import fr.flareden.meetingcar.databinding.ActivityMainBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.listener.IClientChangeHandler;

public class MainActivity extends AppCompatActivity implements IClientChangeHandler {
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
                drawer.closeDrawer(Gravity.LEFT);
            } else {
                Metier.getINSTANCE().disconnect();
                Button b = findViewById(R.id.buttonLogin);
                b.setText(R.string.action_sign_in);
            }
        });
        Metier.getINSTANCE().addOnClientChange(this);
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

    @Override
    public void onClientChange(Client c) {
        runOnUiThread(() -> {
            if (c != null) {
                if (c.getImage() != null) {
                    ImageView iv = findViewById(R.id.profile_image);
                    iv.setImageDrawable(c.getImage());
                }

                // ADD USER OPTIONS
                addUserOptions(true);
            } else {
                // SET PLACEHOLDER IMG
                ImageView iv = findViewById(R.id.profile_image);
                iv.setImageResource(R.drawable.ic_baseline_person_profile);

                // HIDE USER OPTIONS
                addUserOptions(false);
            }
        });
    }

    public void addUserOptions(boolean visible) {
        binding.navView.getMenu().findItem(R.id.nav_profile).setVisible(visible);
        binding.navView.getMenu().findItem(R.id.nav_mail).setVisible(visible);
        binding.navView.getMenu().findItem(R.id.nav_follow).setVisible(visible);
        binding.navView.getMenu().findItem(R.id.nav_announces).setVisible(visible);
        binding.navView.getMenu().findItem(R.id.nav_history).setVisible(visible);
    }
}