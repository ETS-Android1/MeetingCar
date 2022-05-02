package fr.flareden.meetingcar.ui.home;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.flareden.meetingcar.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    // RECYCLER VIEW
    private RecyclerView recycler;
    private AdvertAdapter adapter;
    private SearchView search;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        ArrayList<AdvertViewModel> data = queryData();

        // RECYCLER VIEW INIT
        recycler = binding.rvAnnounce;
        adapter = new AdvertAdapter(data);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));

        // GESTURE
        GestureDetector gd = new GestureDetector(this.getActivity(), new GestureDetector.SimpleOnGestureListener() {
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
        search = binding.searchAnnounce;
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

        // FAB GONE OR VISIBLE
        binding.homeFabAdd.setOnClickListener((View view) -> {
            System.out.println("Add!");
            // TODO
        });
        initFab(binding);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected ArrayList<AdvertViewModel> queryData() {

        // FILL DATA (MAKE IT ON SQL)
        ArrayList<AdvertViewModel> data = new ArrayList<>();
        data.add(new AdvertViewModel(0, "A1", "A2", "A3", "A4", AdvertViewModel.TYPE.RENT));
        data.add(new AdvertViewModel(1, "B1", "B2", "B3", "B4", AdvertViewModel.TYPE.SELL));
        data.add(new AdvertViewModel(2, "C1", "C2", "C3", "C4", AdvertViewModel.TYPE.RENT));
        data.add(new AdvertViewModel(3, "D1", "C2", "C3", "C4", AdvertViewModel.TYPE.RENT));

        return data;
    }

    protected void initFab(FragmentHomeBinding binding) {
    }
}
