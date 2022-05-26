package fr.flareden.meetingcar.ui.follow;

import android.view.View;

import java.util.ArrayList;

import fr.flareden.meetingcar.databinding.FragmentHomeBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.ui.home.AdvertViewModel;
import fr.flareden.meetingcar.ui.home.HomeFragment;

public class FollowFragment extends HomeFragment {

    @Override
    protected void queryData(int idClient) {
        if(idClient >= 0){
            CommunicationWebservice.getINSTANCE().getAnnoncesFollow(idClient, 0, liste -> {
                for(Annonce a : liste){
                    super.data.add(new AdvertViewModel(a.getId(), a.getTitle(), a.getDesc(), a.getVendeur().getAdresse(), ""+a.getPrix(), (a.isLocation() ? AdvertViewModel.TYPE.RENT : AdvertViewModel.TYPE.SELL) ));
                }
                getActivity().runOnUiThread(() -> {
                    super.adapter.setDataAffichage();
                    super.adapter.notifyDataSetChanged();
                });
            });
        }
    }

    @Override
    protected void initFab(FragmentHomeBinding binding) {

    }
}