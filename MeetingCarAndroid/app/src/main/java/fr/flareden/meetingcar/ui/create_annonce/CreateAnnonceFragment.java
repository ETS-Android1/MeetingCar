package fr.flareden.meetingcar.ui.create_annonce;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.regex.Pattern;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.databinding.FragmentCreateAnnonceBinding;
import fr.flareden.meetingcar.databinding.FragmentHomeBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.metier.entity.Image;
import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.listener.IAnnonceLoaderHandler;
import fr.flareden.meetingcar.metier.listener.IConnectHandler;

public class CreateAnnonceFragment extends Fragment {

    FragmentCreateAnnonceBinding binding;
    ArrayAdapter<Uri> imageAdapter;
    ArrayList<Uri> listImage = new ArrayList<Uri>();
    Uri selectedElement = null;

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
                listImage.add(result.getData().getData());
                imageAdapter.notifyDataSetChanged();
            }
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateAnnonceBinding.inflate(inflater, container, false);
        Fragment self = this;
        Metier.getINSTANCE().isLogin(new IConnectHandler() {
            @Override
            public void onConnectionSuccess(Client c, String hashedPassword, boolean isAutoConnect) {
            }
            @Override
            public void onConnectionFail(boolean unknown) {
            }
            @Override
            public void askIsLogin(boolean isLogin) {
                if(!isLogin){
                    NavController navController = NavHostFragment.findNavController(self);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_home);
                }
            }
        });

        binding.tfCreateAnnoncePrice.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                checkPrice(editable.toString());
            }
        });

        binding.butCreateAnnonce.setOnClickListener((View view) -> {
            createAnnonceAction();
        });

        imageAdapter = new ArrayAdapter<Uri>(this.getContext(), android.R.layout.simple_list_item_1, listImage);

        binding.lvCreateAnnonceListImage.setAdapter(imageAdapter);
        binding.lvCreateAnnonceListImage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedElement = listImage.get(position);
                binding.butRemoveAnnonceImage.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.red, new ContextThemeWrapper(getContext(), R.style.Theme_MeetingCar).getTheme()));
                binding.butRemoveAnnonceImage.setImageTintList(getContext().getResources().getColorStateList(R.color.white, new ContextThemeWrapper(getContext(), R.style.Theme_MeetingCar).getTheme()));
                binding.butRemoveAnnonceImage.setClickable(true);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                binding.butRemoveAnnonceImage.setBackgroundTintList(null);
                binding.butRemoveAnnonceImage.setImageTintList(null);
                binding.butRemoveAnnonceImage.setClickable(false);
            }
        });

        binding.butRemoveAnnonceImage.setOnClickListener(view -> {
            if(selectedElement != null){
                this.listImage.remove(selectedElement);
                selectedElement = null;
                binding.lvCreateAnnonceListImage.clearChoices();
                this.imageAdapter.notifyDataSetChanged();
                binding.lvCreateAnnonceListImage.requestLayout();
            }
        });

        binding.butAddAnnonceImage.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openImageSelector();
            }
        });

        return binding.getRoot();
    }

    private void checkPrice(String entree){

       /*String withoutSpace = entree.trim().replaceAll("\\s+", "");
        if(withoutSpace.length() > 0) {
            String entier = "0";
            String decimal = "00";
            String[] splitted = withoutSpace.split("\\.");
            if (splitted.length == 1) {
                entier = splitted[0];
            } else if (splitted.length > 2) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, max = splitted.length - 1; i < max; i++) {
                    sb.append(splitted[i]);
                }
                entier = sb.toString();
                decimal = splitted[splitted.length - 1];
            } else {
                entier = splitted[0];
                decimal = splitted[1];
            }
            if (decimal.length() > 2) {
                decimal.substring(0, 2);
            } else if (decimal.length() == 1) {
                decimal += "0";
            }
            StringBuilder entierResult = new StringBuilder();
            for (int i = entier.length() - 1; i >= 3; i--) {
                entierResult.append(entier.charAt(i));
                if (i % 3 == 0) {
                    entierResult.append(" ");
                }
            }
            binding.tfCreateAnnoncePrice.getEditText().setText(entierResult.toString() + "." + decimal);
        } else {
            binding.tfCreateAnnoncePrice.getEditText().setText("0.00");
        }*/

    }

    private void createAnnonceAction(){
        String titre = binding.tfCreateAnnonceTitle.getEditText().getText().toString();
        String description = binding.tfCreateAnnonceDescription.getEditText().getText().toString();
        float price = Float.parseFloat(binding.tfCreateAnnoncePrice.getEditText().getText().toString().replaceAll("\\s+", ""));
        boolean location = binding.cbCreateAnnonceRental.isChecked();
        Annonce a = new Annonce(-1, titre, description, price ,  Metier.getINSTANCE().getUtilisateur(), null, true, null, null, location, false);
        CommunicationWebservice.getINSTANCE().createAnnonce(a, this.listImage, getContext().getContentResolver(), (idAnnonce -> {
            if(idAnnonce >= 0){
                Bundle b = new Bundle();
                b.putInt("idAnnonce", idAnnonce);
                NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();
                navController.navigate(R.id.nav_annonce, b);
            } else {
                //TODO : error
            }
        }));
    }

    private void openImageSelector() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");

        photoChooser.launch(pickIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
