package fr.flareden.meetingcar.ui.mail;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.databinding.FragmentHomeBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.entity.messagerie.Discussion;
import fr.flareden.meetingcar.ui.home.HomeFragment;
import fr.flareden.meetingcar.ui.home.SpecialAdapter;

public class MailFragment extends HomeFragment {
    @Override
    protected void queryData(int idClient) {
        System.out.println("QUERY");
        if (idClient >= 0) {
            System.out.println("A");

            CommunicationWebservice.getINSTANCE().getDiscussions(0, liste -> {
                System.out.println("Liste : " + liste.size());
                for (Discussion a : liste) {
                    super.data.add(new MailViewModel(a));
                }
                getActivity().runOnUiThread(() -> {
                    super.adapter.notifyDataSetChanged();
                });
            });
        }
    }

    @Override
    protected SpecialAdapter generateAdapter() {
        return new SpecialAdapter(data, SpecialAdapter.Type.Discussion);
    }

    @Override
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
        super.recycler.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                boolean touch = gd.onTouchEvent(e);
                if (child != null && touch) {
                    int pos = rv.getChildAdapterPosition(child);
                    MailViewModel mvm = (MailViewModel) data.get(pos);

                    Bundle b = new Bundle();
                    b.putSerializable("discussion", mvm.getDiscussion());

                    NavController navController = NavHostFragment.findNavController(self);
                    navController.popBackStack();

                    navController.navigate(R.id.nav_discussion, b);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initFab(FragmentHomeBinding binding) {

    }
}