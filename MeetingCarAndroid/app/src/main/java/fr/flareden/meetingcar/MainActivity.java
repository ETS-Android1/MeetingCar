package fr.flareden.meetingcar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import fr.flareden.meetingcar.databinding.ActivityMainBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.listener.IClientChangeHandler;
import fr.flareden.meetingcar.metier.listener.IConnectHandler;

public class MainActivity extends AppCompatActivity implements IClientChangeHandler, IConnectHandler {
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

        // LOAD NAVIGATION
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_mail, R.id.nav_follow, R.id.nav_announces, R.id.nav_history, R.id.nav_login, R.id.nav_annonce, R.id.nav_create_announce)
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
                navController.popBackStack();
                navController.navigate(R.id.nav_home);
                drawer.closeDrawer(Gravity.LEFT);

                FragmentManager fm = this.getSupportFragmentManager();
                for(int i = 0 ; i<fm.getBackStackEntryCount() ; i++){
                    fm.popBackStack();
                }
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
                    iv.setImageDrawable(c.getImage().getDrawable());
                }

                // SET TEXT USER
                ((TextView) findViewById(R.id.tv_username)).setText(c.getNom() + (c.getPrenom().length() > 0 ? " " + c.getPrenom() : ""));

                // ADD USER OPTIONS
                addUserOptions(true);

                // SET BUTTON LOGIN
                Button b = findViewById(R.id.buttonLogin);
                b.setText(R.string.disconnect);
            } else {
                // SET PLACEHOLDER IMG
                ImageView iv = findViewById(R.id.profile_image);
                iv.setImageResource(R.drawable.ic_baseline_person_profile);

                // SET TEXT USER
                ((TextView) findViewById(R.id.tv_username)).setText(getResources().getString(R.string.guest));

                // HIDE USER OPTIONS
                addUserOptions(false);

                // SET BUTTON LOGIN
                Button b = findViewById(R.id.buttonLogin);
                b.setText(R.string.action_sign_in);
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


    @Override
    protected void onResume() {
        super.onResume();
        Metier.getINSTANCE().isLogin(this);
    }

    @Override
    public void onConnectionSuccess(Client c, String hashedPassword, boolean isAutoConnect) {
        if(isAutoConnect){
            Metier.getINSTANCE().setUtilisateur(c);

        }
    }

    @Override
    public void onConnectionFail(boolean unknown) {
        SharedPreferences.Editor editor = this.getApplicationContext().getSharedPreferences("auto_connect", Context.MODE_PRIVATE).edit();
        editor.remove("email");
        editor.remove("password");
        editor.remove("date");
    }

    @Override
    public void askIsLogin(boolean isLogin) {
        if(!isLogin){
            SharedPreferences sp = this.getSharedPreferences("auto_connect", Context.MODE_PRIVATE);
            String email = sp.getString("email", null);
            String pass = sp.getString("password", null);
            String date = sp.getString("date", null);

            //Toast.makeText(getApplicationContext(),"EMAIL : " + email+ "\n PASS : " + pass + "\n DATE : " + date, Toast.LENGTH_SHORT ).show();
            if(email != null && pass != null){
                CommunicationWebservice.getINSTANCE().connect(email, pass, this, true);
            }
        }
    }
}