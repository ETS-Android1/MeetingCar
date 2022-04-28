package fr.flareden.meetingcar.ui.follow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FollowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FollowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("FollowText!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}