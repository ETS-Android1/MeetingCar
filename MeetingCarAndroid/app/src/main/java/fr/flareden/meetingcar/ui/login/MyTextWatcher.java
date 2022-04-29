package fr.flareden.meetingcar.ui.login;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

import fr.flareden.meetingcar.R;

public class MyTextWatcher implements TextWatcher {
    TextInputLayout til;
    Resources resources;
    public MyTextWatcher(TextInputLayout til, Resources resources) {
        this.til = til;
        this.resources = resources;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(editable.toString().trim().length() <= 0){
            til.setError(resources.getString(R.string.error_empty));
            til.setErrorEnabled(true);
        } else {
            til.setErrorEnabled(false);
        }
    }
}
