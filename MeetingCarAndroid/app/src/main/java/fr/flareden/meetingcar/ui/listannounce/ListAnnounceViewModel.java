package fr.flareden.meetingcar.ui.listannounce;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListAnnounceViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ListAnnounceViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("listAds!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
