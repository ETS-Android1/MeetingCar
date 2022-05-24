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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.databinding.FragmentHomeBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.listener.IConnectHandler;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    // RECYCLER VIEW
    protected RecyclerView recycler;
    protected SpecialAdapter adapter;
    private SearchView search;

    protected ArrayList<IViewModel> data = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        data.clear();

        Client c = Metier.getINSTANCE().getUtilisateur();
        if (c == null) {
            queryData(-1);
        } else {
            queryData(c.getId());
        }

        // RECYCLER VIEW INIT
        recycler = binding.rvAnnounce;
        adapter = this.generateAdapter();
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));

        //AddTouchListener
        touchListener();

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

        // FAB ADD ANNOUNCE
        binding.homeFabAdd.setOnClickListener((View view) -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();
            navController.navigate(R.id.nav_create_announce);
        });

        Metier.getINSTANCE().isLogin(new IConnectHandler() {
            @Override
            public void onConnectionSuccess(Client c, String hashedPassword, boolean isAutoConnect) {

            }

            @Override
            public void onConnectionFail(boolean unknown) {

            }

            @Override
            public void askIsLogin(boolean isLogin) {
                if (isLogin) {
                    getActivity().runOnUiThread(() -> {
                        initFab(binding);
                    });
                }
            }
        });

        return binding.getRoot();
    }

    protected SpecialAdapter generateAdapter() {
        return new SpecialAdapter(data, SpecialAdapter.Type.Advert);
    }

    protected void touchListener() {
        // GESTURE
        GestureDetector gd = new GestureDetector(this.getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        Fragment self = this;
        // LISTENER RECYCLER
        recycler.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                boolean touch = gd.onTouchEvent(e);
                if (child != null && touch) {
                    int pos = rv.getChildAdapterPosition(child);
                    AdvertViewModel avm = (AdvertViewModel) data.get(pos);

                    Bundle b = new Bundle();
                    b.putInt("idAnnonce", avm.getId());
                    NavController navController = NavHostFragment.findNavController(self);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_annonce, b);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        data.clear();
        binding = null;
    }

    protected void queryData(int idClient) {
        CommunicationWebservice.getINSTANCE().getAnnonceListe(0, liste -> {
            for (Annonce a : liste) {
                this.data.add(new AdvertViewModel(a.getId(),
                        a.getTitle(),
                        a.getDesc(),
                        a.getVendeur().getAdresse(),
                        "" + a.getPrix(),
                        (a.isLocation() ? AdvertViewModel.TYPE.RENT : AdvertViewModel.TYPE.SELL)));
            }
            getActivity().runOnUiThread(() -> {
                this.adapter.notifyDataSetChanged();
            });
        });
    }

    protected void initFab(FragmentHomeBinding binding) {
        binding.homeFabAdd.setVisibility(View.VISIBLE);
    }
}
