package fr.flareden.meetingcar.ui.follow;

import java.util.ArrayList;

import fr.flareden.meetingcar.ui.home.AdvertViewModel;
import fr.flareden.meetingcar.ui.home.HomeFragment;

public class FollowFragment extends HomeFragment {

    @Override
    protected ArrayList<AdvertViewModel> queryData() {

        // Change DATA
        ArrayList<AdvertViewModel> data = new ArrayList<>();
        data.add(new AdvertViewModel(0, "E", "A2", "A3", "A4", AdvertViewModel.TYPE.RENT));

        return data;

    }
}