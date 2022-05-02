package fr.flareden.meetingcar.ui.myannounces;

import java.util.ArrayList;

import fr.flareden.meetingcar.ui.home.AdvertViewModel;
import fr.flareden.meetingcar.ui.home.HomeFragment;

public class AnnouncesFragment extends HomeFragment {

    @Override
    protected ArrayList<AdvertViewModel> queryData() {

        // Change DATA
        ArrayList<AdvertViewModel> data = new ArrayList<>();
        data.add(new AdvertViewModel(0, "F", "A2", "A3", "A4", AdvertViewModel.TYPE.RENT));

        return data;

    }
}