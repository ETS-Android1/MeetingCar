package fr.flareden.meetingcar.ui.mail;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.databinding.FragmentHomeBinding;
import fr.flareden.meetingcar.databinding.FragmentMailBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.metier.entity.messagerie.Discussion;
import fr.flareden.meetingcar.ui.home.AdvertViewModel;
import fr.flareden.meetingcar.ui.home.HomeFragment;
import fr.flareden.meetingcar.ui.home.SpecialAdapter;

public class MailFragment extends HomeFragment {
    @Override
    protected void queryData(int page) {
        if(page >= 0){
            CommunicationWebservice.getINSTANCE().getDiscussions(page, liste -> {
                for(Discussion a : liste){
                    super.data.add(new MailViewModel(a));
                }
                getActivity().runOnUiThread(() -> {
                    super.adapter.notifyDataSetChanged();
                });
            });
        }
    }
    @Override
    protected SpecialAdapter generateAdapter(){
        return new SpecialAdapter(data, SpecialAdapter.Type.Discussion);
    }
    @Override
    protected void touchListener(){
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
                    b.putInt("idDiscussion", mvm.getId());
                    //TODO navigate to nav_discussion
                    /*NavController navController = NavHostFragment.findNavController(self);
                    navController.popBackStack();

                    navController.navigate(R.id.nav_annonce, b);*/
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