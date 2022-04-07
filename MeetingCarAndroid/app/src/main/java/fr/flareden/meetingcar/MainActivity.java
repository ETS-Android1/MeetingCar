package fr.flareden.meetingcar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private AdvertAdapter adapter;
    private SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FILL DATA (MAKE IT ON SQL)
        ArrayList<AdvertViewModel> data = new ArrayList<>();
        data.add(new AdvertViewModel("A1", "A2", "A3", "A4", AdvertViewModel.TYPE.RENT));
        data.add(new AdvertViewModel("B1", "B2", "B3", "B4", AdvertViewModel.TYPE.SELL));
        data.add(new AdvertViewModel("C1", "C2", "C3", "C4", AdvertViewModel.TYPE.RENT));

        // RECYCLER VIEW INIT
        recycler = findViewById(R.id.rv_annonce);
        adapter = new AdvertAdapter(data);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

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
    }
}