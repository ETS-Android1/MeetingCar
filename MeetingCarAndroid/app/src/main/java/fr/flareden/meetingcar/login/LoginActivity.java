package fr.flareden.meetingcar.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.listener.IConnectHandler;
import fr.flareden.meetingcar.metier.listener.IRegisterHandler;

public class LoginActivity extends AppCompatActivity implements IConnectHandler, IRegisterHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Metier.getINSTANCE().isLogin(this);

        TextInputLayout birthday = (TextInputLayout) findViewById(R.id.tf_birthday_register);

        birthday.setEndIconOnClickListener((View view) -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Selectionne une data").build();
            datePicker.addOnPositiveButtonClickListener((Long selection) -> {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                ((TextInputLayout)findViewById(R.id.tf_birthday_register)).getEditText().setText(format.format(new Date(selection)));
            });
            datePicker.show(getSupportFragmentManager(), "tag");
        });
        ((TextInputLayout) findViewById(R.id.tf_email_login)).getEditText().addTextChangedListener(new MyTextWatcher((TextInputLayout) findViewById(R.id.tf_email_login), getResources()));
        ((TextInputLayout) findViewById(R.id.tf_password_login)).getEditText().addTextChangedListener(new MyTextWatcher((TextInputLayout) findViewById(R.id.tf_password_login), getResources()));
        ((TextInputLayout) findViewById(R.id.tf_password_register)).getEditText().addTextChangedListener(new MyTextWatcher((TextInputLayout) findViewById(R.id.tf_password_register), getResources()));
        ((TextInputLayout) findViewById(R.id.tf_surname_register)).getEditText().addTextChangedListener(new MyTextWatcher((TextInputLayout) findViewById(R.id.tf_surname_register), getResources()));
        ((TextView) findViewById(R.id.tv_required)).setText("* : " + getResources().getString(R.string.prompt_required));
        SharedPreferences sp = getSharedPreferences("auto_connect", MODE_PRIVATE);
        String email = sp.getString("email", null);
        if(email != null){
            ((TextInputLayout) findViewById(R.id.tf_email_login)).getEditText().setText(email.trim());
        }
    }

    public void askRegisterMenu(View view) {
        findViewById(R.id.linear_register).setVisibility(View.VISIBLE);
        findViewById(R.id.linear_login_button).setVisibility(View.GONE);

        ((TextInputLayout) findViewById(R.id.tf_email_login)).setHint(getResources().getString(R.string.prompt_email) + "*");
        ((TextInputLayout) findViewById(R.id.tf_password_login)).setHint(getResources().getString(R.string.prompt_password) + "*");
        ((TextInputLayout) findViewById(R.id.tf_password_register)).setHint(getResources().getString(R.string.prompt_retype_password) + "*");
        ((TextInputLayout) findViewById(R.id.tf_surname_register)).setHint(getResources().getString(R.string.prompt_surname) + "*");
    }

    public void loginAction(View view) {

        TextInputLayout emailTIL = (TextInputLayout) findViewById(R.id.tf_email_login);
        TextInputLayout passwordTIL = (TextInputLayout) findViewById(R.id.tf_password_login);
        String email = emailTIL.getEditText().getText().toString().trim();
        String password = passwordTIL.getEditText().getText().toString().trim();
        boolean loginPossible = true;
        if (email.length() <= 0) {
            emailTIL.setError(getResources().getString(R.string.error_empty));
            emailTIL.setErrorEnabled(true);
            loginPossible = false;
        }
        if (password.length() <= 0) {
            passwordTIL.setError(getResources().getString(R.string.error_empty));
            passwordTIL.setErrorEnabled(true);
            loginPossible = false;
        }
        if (loginPossible) {
            CommunicationWebservice.getINSTANCE().connect(email, password, this, false);
        }
    }

    public void registerAction(View view) {
        boolean registerPossible = true;
        TextInputLayout emailTIL = (TextInputLayout) findViewById(R.id.tf_email_login);
        TextInputLayout passwordTIL = (TextInputLayout) findViewById(R.id.tf_password_login);
        TextInputLayout passwordSecondTIL = (TextInputLayout) findViewById(R.id.tf_password_register);
        TextInputLayout surnameTIL = (TextInputLayout) findViewById(R.id.tf_surname_register);

        String email = emailTIL.getEditText().getText().toString().trim();
        String password = passwordTIL.getEditText().getText().toString().trim();
        String passwordSecond = passwordSecondTIL.getEditText().getText().toString().trim();
        String surname = surnameTIL.getEditText().getText().toString().trim();
        String name = ((TextInputLayout) findViewById(R.id.tf_name_register)).getEditText().getText().toString().trim();
        String phone = ((TextInputLayout) findViewById(R.id.tf_phone_register)).getEditText().getText().toString().trim();
        String address = ((TextInputLayout) findViewById(R.id.tf_address_register)).getEditText().getText().toString().trim();

        String birthday = ((TextInputLayout) findViewById(R.id.tf_birthday_register)).getEditText().getText().toString().trim();
        String[] splitted = birthday.split("/");
        birthday = splitted[2] + splitted[1] + splitted[0];

        if (email.length() <= 0) {
            emailTIL.setError(getResources().getString(R.string.error_empty));
            emailTIL.setErrorEnabled(true);
            registerPossible = false;
        }
        if (password.length() <= 0) {
            passwordTIL.setError(getResources().getString(R.string.error_empty));
            passwordTIL.setErrorEnabled(true);
            registerPossible = false;
        }
        if (passwordSecond.length() <= 0) {
            passwordSecondTIL.setError(getResources().getString(R.string.error_empty));
            passwordSecondTIL.setErrorEnabled(true);
            registerPossible = false;
        } else if (!passwordSecond.equals(password)) {
            passwordSecondTIL.setError(getResources().getString(R.string.password_no_match));
            passwordSecondTIL.setErrorEnabled(true);
            registerPossible = false;
        }
        if (surname.length() <= 0) {
            surnameTIL.setError(getResources().getString(R.string.error_empty));
            surnameTIL.setErrorEnabled(true);
            registerPossible = false;
        }

        Client c = new Client(-1, surname, name, email, phone, birthday, address);
        CommunicationWebservice.getINSTANCE().inscription(c, password ,this);

    }

    public void askCancelRegister(View view) {
        findViewById(R.id.linear_register).setVisibility(View.GONE);
        findViewById(R.id.linear_login_button).setVisibility(View.VISIBLE);
        ((TextInputLayout) findViewById(R.id.tf_email_login)).setHint(getResources().getString(R.string.prompt_email));
        ((TextInputLayout) findViewById(R.id.tf_password_login)).setHint(getResources().getString(R.string.prompt_password));
    }

    @Override
    public void onConnectionSuccess(Client c, String hashedPassword, boolean isAutoConnect) {
        SharedPreferences sp = getSharedPreferences("auto_connect", MODE_PRIVATE);
        if(!isAutoConnect) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            sp.edit().putString("email", c.getEmail());
            sp.edit().putString("password", hashedPassword);
            sp.edit().putString("date", format.format(Calendar.getInstance().getTime()));
        }
        Metier.getINSTANCE().setUtilisateur(c);
        startActivity(new Intent(this, (Class<?>) getIntent().getSerializableExtra("calling-activity")));
    }

    @Override
    public void onConnectionFail(boolean unknown) {
        runOnUiThread(() -> {
        TextView tv = (TextView) findViewById(R.id.tv_error_login);
            if (unknown) {
                tv.setText(getResources().getString(R.string.wrong_email_password));
            } else {
                tv.setText(getResources().getString(R.string.please_enter_email_password));
            }
            tv.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void askIsLogin(boolean isLogin) {

        if (isLogin) {
            //startActivity(new Intent(this, (Class<?>) getIntent().getSerializableExtra("calling-activity")));
        }
    }

    @Override
    public void onRegisterClient(State state, String email, String password) {
        if(state == State.OK){
            CommunicationWebservice.getINSTANCE().connect(email, password, this , false);
        } else if(state == State.EMAIL_ALREADY_ON_USE){
            runOnUiThread(()-> {
                TextInputLayout til = (TextInputLayout) findViewById(R.id.tf_email_login);
                til.setError(getResources().getString(R.string.email_already_use));
                til.setErrorEnabled(true);
            });
        }
    }
}