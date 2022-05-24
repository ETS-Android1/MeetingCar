package fr.flareden.meetingcar.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.databinding.FragmentProfileBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.entity.client.Professionnel;
import fr.flareden.meetingcar.metier.listener.IClientLoadingHandler;

public class ProfileFragment extends Fragment implements IClientLoadingHandler {

    private FragmentProfileBinding binding;
    private boolean isSelf = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // BINDING FRAGMENT
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // FAB BINDING
        binding.profileFabEdit.setOnClickListener((View view) -> editProfile());
        binding.profileFabCheck.setOnClickListener((View view) -> checkEditProfile());
        binding.profileFabCancel.setOnClickListener((View view) -> cancelEditProfile());

        // CURRENT USER && TARGET USER
        Bundle bundle = this.getArguments();
        int userID = (bundle== null ? -1 : bundle.getInt("userID", -1));
        if (userID == -1) {
            if (Metier.getINSTANCE().getUtilisateur() != null) {
                onClientLoad(Metier.getINSTANCE().getUtilisateur(), true);
            } else {
                // NAV
                NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();
                navController.navigate(R.id.nav_home);
            }
        } else if (userID == Metier.getINSTANCE().getUtilisateur().getId()) {
            onClientLoad(Metier.getINSTANCE().getUtilisateur(), true);
        } else {
            CommunicationWebservice.getINSTANCE().getClient(userID, this);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void editProfile() {
        if (isSelf) {
            // GONE
            binding.profileTvName.setVisibility(View.GONE);
            binding.profileTvEmail.setVisibility(View.GONE);
            binding.profileTvPhone.setVisibility(View.GONE);
            binding.profileFabEdit.setVisibility(View.GONE);
            binding.profileBtnAnnounces.setVisibility(View.GONE);

            // VISIBLE
            binding.profileLayoutName.setVisibility(View.VISIBLE);
            binding.profileEditEmail.setVisibility(View.VISIBLE);
            binding.profileEditPhone.setVisibility(View.VISIBLE);
            binding.profileLayoutBirth.setVisibility(View.VISIBLE);
            binding.profileLayoutAddress.setVisibility(View.VISIBLE);
            binding.profileFabCancel.setVisibility(View.VISIBLE);
            binding.profileFabCheck.setVisibility(View.VISIBLE);

            // HINTS
            Client user = Metier.getINSTANCE().getUtilisateur();
            if (user.getNom().length() > 0 && !user.getNom().equalsIgnoreCase("null")) {
                binding.profileEditName.setHint(user.getNom());
            }
            if (user.getPrenom().length() > 0 && !user.getPrenom().equalsIgnoreCase("null")) {
                binding.profileEditSurname.setHint(user.getPrenom());
            }
            if (user.getEmail().length() > 0 && !user.getEmail().equalsIgnoreCase("null")) {
                binding.profileEditEmail.setHint(user.getEmail());
            }
            if (user.getTelephone().length() > 0 && !user.getTelephone().equalsIgnoreCase("null")) {
                binding.profileEditPhone.setHint(user.getTelephone());
            }
            if (user.getDatenaissance().length() > 0 && !user.getDatenaissance().equalsIgnoreCase("null")) {
                binding.profileEditBirth.setHint(user.getDatenaissance());
            } else {
                binding.profileEditBirth.setHint(R.string.date_format);
            }
            if (user.getAdresse().length() > 0 && !user.getAdresse().equalsIgnoreCase("null")) {
                binding.profileEditAddress.setHint(user.getAdresse());
            }

            // LISTENER IMG
            binding.profileImage.setOnClickListener((View view) -> {
                // TODO : modify img
            });
        }
    }

    public void cancelEditProfile() {
        // GONE
        binding.profileLayoutName.setVisibility(View.GONE);
        binding.profileEditEmail.setVisibility(View.GONE);
        binding.profileEditPhone.setVisibility(View.GONE);
        binding.profileLayoutBirth.setVisibility(View.GONE);
        binding.profileLayoutAddress.setVisibility(View.GONE);
        binding.profileFabCancel.setVisibility(View.GONE);
        binding.profileFabCheck.setVisibility(View.GONE);

        // VISIBLE
        binding.profileTvName.setVisibility(View.VISIBLE);
        binding.profileTvEmail.setVisibility(View.VISIBLE);
        binding.profileTvPhone.setVisibility(View.VISIBLE);
        binding.profileFabEdit.setVisibility(View.VISIBLE);
        binding.profileBtnAnnounces.setVisibility(View.VISIBLE);
    }

    public void checkEditProfile() {
        if (isSelf) {
            // GET STRING
            String nameText = binding.profileEditName.getText().toString();
            String surnameText = binding.profileEditSurname.getText().toString();
            String emailText = binding.profileEditEmail.getText().toString();
            String phoneText = binding.profileEditPhone.getText().toString();
            String addressText = binding.profileEditAddress.getText().toString();
            String birthText = binding.profileEditBirth.getText().toString();

            // UPDATE USER
            if (nameText.length() > 0) {
                Metier.getINSTANCE().getUtilisateur().setNom(nameText);
            }
            if (surnameText.length() > 0) {
                Metier.getINSTANCE().getUtilisateur().setPrenom(surnameText);
            }
            if (emailText.length() > 0) {
                Metier.getINSTANCE().getUtilisateur().setEmail(emailText);
            }
            if (phoneText.length() > 0) {
                Metier.getINSTANCE().getUtilisateur().setTelephone(phoneText);
            }
            if (addressText.length() > 0) {
                Metier.getINSTANCE().getUtilisateur().setAdresse(addressText);
            }
            if (birthText.length() > 0) {
                Metier.getINSTANCE().getUtilisateur().setDatenaissance(birthText);
            }

            onClientLoad(Metier.getINSTANCE().getUtilisateur(), true);
            cancelEditProfile();
            CommunicationWebservice.getINSTANCE().updateClient(Metier.getINSTANCE().getUtilisateur(), null, null);
        }
    }

    @Override
    public void onClientLoad(Client c, boolean self) {
        getActivity().runOnUiThread(() -> {
            if (c.getPrenom() != null) {
                binding.profileTvName.setText(c.getNom() + " " + c.getPrenom());
            } else {
                binding.profileTvName.setText(c.getNom());
            }
            binding.profileTvEmail.setText(c.getEmail());
            if (c.getTelephone().equalsIgnoreCase("null")) {
                binding.profileTvPhone.setText("");
            } else {
                binding.profileTvPhone.setText(c.getTelephone());

            }
            if (c.getClass() == Professionnel.class) {
                binding.profileTvPro.setVisibility(View.VISIBLE);
            }
            if (self) {
                binding.profileFabEdit.setVisibility(View.VISIBLE);
                isSelf = true;
            }
        });
    }
}