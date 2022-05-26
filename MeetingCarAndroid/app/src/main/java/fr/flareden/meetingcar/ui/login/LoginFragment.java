package fr.flareden.meetingcar.ui.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.databinding.FragmentLoginBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.listener.IConnectHandler;
import fr.flareden.meetingcar.metier.listener.IIsLoginHandler;
import fr.flareden.meetingcar.metier.listener.IRegisterHandler;

public class LoginFragment extends Fragment implements IConnectHandler, IRegisterHandler, IIsLoginHandler {
    private FragmentLoginBinding binding;

    private Drawable image_profile;
    private Uri imageURI = null;
    private byte[] imageData = null;
    private boolean imageRead = false;
    private ActivityResultLauncher<String> permissionResult = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                openImageSelector();
            }
        }
    });

    private ActivityResultLauncher<Intent> photoChooser = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == Activity.RESULT_OK) {
                imageURI = result.getData().getData();

            }
        }
    });


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Metier.getINSTANCE().isLogin(this);

        TextInputLayout birthday = binding.tfBirthdayRegister;

        birthday.setEndIconOnClickListener((View view) -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Selectionne une data").build();
            datePicker.addOnPositiveButtonClickListener((Long selection) -> {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                birthday.getEditText().setText(format.format(new Date(selection)));
            });
            datePicker.show(getParentFragmentManager(), "tag");
        });
        binding.tfEmailLogin.getEditText().addTextChangedListener(new MyTextWatcher(binding.tfEmailLogin, getResources()));
        binding.tfPasswordLogin.getEditText().addTextChangedListener(new MyTextWatcher(binding.tfPasswordLogin, getResources()));
        binding.tfPasswordRegister.getEditText().addTextChangedListener(new MyTextWatcher(binding.tfPasswordRegister, getResources()));
        binding.tfSurnameRegister.getEditText().addTextChangedListener(new MyTextWatcher(binding.tfSurnameRegister, getResources()));
        binding.tvRequired.setText("* : " + getResources().getString(R.string.prompt_required));
        SharedPreferences sp = this.getActivity().getSharedPreferences("auto_connect", Context.MODE_PRIVATE);
        String email = sp.getString("email", null);
        if (email != null) {
            binding.tfEmailLogin.getEditText().setText(email.trim());
        }

        binding.btnAskRegister.setOnClickListener((View view) -> {
            askRegisterMenu(view);
        });
        binding.btnSignIn.setOnClickListener((View view) -> {
            loginAction(view);
        });
        binding.btnRegister.setOnClickListener((View view) -> {
            registerAction(view);
        });
        binding.btnCancelRegister.setOnClickListener((View view) -> {
            askCancelRegister(view);
        });


        binding.btnSelectProfilePicture.setOnClickListener((View view) -> {
            if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openImageSelector();
            }
        });
        return root;
    }


    public void openImageSelector() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");

        photoChooser.launch(pickIntent);
    }

    public void askRegisterMenu(View view) {
        binding.linearRegister.setVisibility(View.VISIBLE);
        binding.linearLoginButton.setVisibility(View.GONE);

        binding.tfEmailLogin.setHint(getResources().getString(R.string.prompt_email) + "*");
        binding.tfPasswordLogin.setHint(getResources().getString(R.string.prompt_password) + "*");
        binding.tfPasswordRegister.setHint(getResources().getString(R.string.prompt_retype_password) + "*");
        binding.tfSurnameRegister.setHint(getResources().getString(R.string.prompt_surname) + "*");
    }

    public void loginAction(View view) {

        TextInputLayout emailTIL = binding.tfEmailLogin;
        TextInputLayout passwordTIL = binding.tfPasswordLogin;
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
        TextInputLayout emailTIL = binding.tfEmailLogin;
        TextInputLayout passwordTIL = binding.tfPasswordLogin;
        TextInputLayout passwordSecondTIL = binding.tfPasswordRegister;
        TextInputLayout surnameTIL = binding.tfSurnameRegister;

        String email = emailTIL.getEditText().getText().toString().trim();
        String password = passwordTIL.getEditText().getText().toString().trim();
        String passwordSecond = passwordSecondTIL.getEditText().getText().toString().trim();
        String surname = surnameTIL.getEditText().getText().toString().trim();
        String name = binding.tfNameRegister.getEditText().getText().toString().trim();
        String phone = binding.tfPhoneRegister.getEditText().getText().toString().trim();
        String address = binding.tfAddressRegister.getEditText().getText().toString().trim();


        String birthday = binding.tfBirthdayRegister.getEditText().getText().toString().trim();
        if(birthday.length() == 8) {
            String[] splitted = birthday.split("/");
            birthday = splitted[2] + splitted[1] + splitted[0];
        }
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
        CommunicationWebservice.getINSTANCE().inscription(c, password, imageURI, this.getActivity().getContentResolver(), this);

    }

    public void askCancelRegister(View view) {
        binding.linearRegister.setVisibility(View.GONE);
        binding.linearLoginButton.setVisibility(View.VISIBLE);
        binding.tfEmailLogin.setHint(getResources().getString(R.string.prompt_email));
        binding.tfPasswordLogin.setHint(getResources().getString(R.string.prompt_password));
    }

    private void goBack() {
        this.getActivity().runOnUiThread(() -> {
            // HIDE KEYBOARD
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), 0);
            // NAV
            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();
            navController.navigate(R.id.nav_home);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onConnectionSuccess(Client c, String hashedPassword, boolean isAutoConnect) {
        if (!isAutoConnect) {
            SharedPreferences sp = this.getActivity().getApplicationContext().getSharedPreferences("auto_connect", Context.MODE_PRIVATE);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("email", c.getEmail());
            editor.putString("password", hashedPassword);
            editor.putString("date", format.format(Calendar.getInstance().getTime()));
            editor.commit();
        }
        Metier.getINSTANCE().setUtilisateur(c);

        goBack();
    }

    @Override
    public void onConnectionFail(boolean unknown) {
        this.getActivity().runOnUiThread(() -> {
            TextView tv = binding.tvErrorLogin;
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
            goBack();
        }
    }

    @Override
    public void onRegisterClient(State state, String email, String password) {
        if (state == State.OK) {
            CommunicationWebservice.getINSTANCE().connect(email, password, this, false);
        } else if (state == State.EMAIL_ALREADY_ON_USE) {
            this.getActivity().runOnUiThread(() -> {
                TextInputLayout til = binding.tfEmailLogin;
                til.setError(getResources().getString(R.string.email_already_use));
                til.setErrorEnabled(true);
            });
        }
    }
}
