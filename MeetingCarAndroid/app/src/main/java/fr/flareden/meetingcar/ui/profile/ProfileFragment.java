package fr.flareden.meetingcar.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import fr.flareden.meetingcar.databinding.FragmentProfileBinding;
import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.listener.IClientLoadingHandler;

public class ProfileFragment extends Fragment implements IClientLoadingHandler {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // BINDING FRAGMENT
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // CURRENT USER && TARGET USER
        int userID = savedInstanceState.getInt("userID", -1);
        if(userID == -1 || userID == Metier.getINSTANCE().getUtilisateur().getId()){
            onClientLoad(Metier.getINSTANCE().getUtilisateur(), true);
        }
        else{
            // TODO : Recup la target
            // mettre une var dans le bundle
            // check si la var est = current.getID()
            // si true => c'est ok
            // sinon => retire edit + query serv du mec cible
        }


        // TODO : Load Data Client (TextView && Hints)
        // TODO : Client.getClass == Professionnel || Particulier ==> binding.profileTvPro.setVisibility(View.VISIBLE);

        // FAB BINDING
        binding.profileFabEdit.setOnClickListener((View view) -> editProfile());
        binding.profileFabCheck.setOnClickListener((View view) -> checkEditProfile());
        binding.profileFabCancel.setOnClickListener((View view) -> cancelEditProfile());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void editProfile(){
        // GONE
        binding.profileTvName.setVisibility(View.GONE);
        binding.profileTvEmail.setVisibility(View.GONE);
        binding.profileTvPhone.setVisibility(View.GONE);
        binding.profileFabEdit.setVisibility(View.GONE);
        binding.profileBtnAnnounces.setVisibility(View.GONE);

        // VISIBLE
        binding.profileEditName.setVisibility(View.VISIBLE);
        binding.profileEditEmail.setVisibility(View.VISIBLE);
        binding.profileEditPhone.setVisibility(View.VISIBLE);
        binding.profileLayoutBirth.setVisibility(View.VISIBLE);
        binding.profileLayoutAddress.setVisibility(View.VISIBLE);
        binding.profileFabCancel.setVisibility(View.VISIBLE);
        binding.profileFabCheck.setVisibility(View.VISIBLE);

        binding.profileImage.setOnClickListener((View view) -> {
            // TODO : modify img
        });
    }

    public void cancelEditProfile(){
        // GONE
        binding.profileEditName.setVisibility(View.GONE);
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

    public void checkEditProfile(){
        cancelEditProfile();
        System.out.println("Checked!");
    }

    @Override
    public void onClientLoad(Client c, boolean self) {
        getActivity().runOnUiThread( () -> {
            binding.profileTvName.setText(c.getNom() + " " + c.getPrenom());
            binding.profileTvEmail.setText(c.getEmail());
            binding.profileTvPhone.setText(c.getTelephone());
            if(self){
                binding.profileFabEdit.setVisibility(View.VISIBLE);
            }
        });
    }
}