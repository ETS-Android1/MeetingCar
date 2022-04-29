package fr.flareden.meetingcar;

import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import fr.flareden.meetingcar.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // NAVIGATION DRAWER
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // RECYCLER VIEW
    private RecyclerView recycler;
    private AdvertAdapter adapter;
    private SearchView search;

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
                R.id.nav_listAds, R.id.nav_home, R.id.nav_mail, R.id.nav_follow, R.id.nav_announces, R.id.nav_history)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // FILL DATA (MAKE IT ON SQL)
        ArrayList<AdvertViewModel> data = new ArrayList<>();
        data.add(new AdvertViewModel(0, "A1", "A2", "A3", "A4", AdvertViewModel.TYPE.RENT));
        data.add(new AdvertViewModel(1, "B1", "B2", "B3", "B4", AdvertViewModel.TYPE.SELL));
        data.add(new AdvertViewModel(2, "C1", "C2", "C3", "C4", AdvertViewModel.TYPE.RENT));
        data.add(new AdvertViewModel(3, "D1", "C2", "C3", "C4", AdvertViewModel.TYPE.RENT));
/*
        // RECYCLER VIEW INIT
        recycler = findViewById(R.id.rv_annonce);
        adapter = new AdvertAdapter(data);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // GESTURE
        GestureDetector gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        // LISTENER RECYCLER
        recycler.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                boolean touch = gd.onTouchEvent(e);
                if (child != null && touch) {
                    int pos = rv.getChildAdapterPosition(child);
                    AdvertViewModel avm = data.get(pos);
                    System.out.println(avm.getId());
                    return true;
                }
                return false;
            }
        });

        // SEARCH
        search = findViewById(R.id.search_annonce);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
 */
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