package fr.flareden.meetingcar.ui.mail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MailViewModel extends ViewModel {

    //

    private final MutableLiveData<String> mText;

    public MailViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Mail CLASS");
    }

    public LiveData<String> getText() {
        return mText;
    }
}