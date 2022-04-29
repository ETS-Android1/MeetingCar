package fr.flareden.meetingcar.ui.announces;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AnnouncesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AnnouncesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("annouces CLASS");
    }

    public LiveData<String> getText() {
        return mText;
    }
}