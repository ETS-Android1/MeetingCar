package fr.flareden.meetingcar.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.flareden.meetingcar.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Load Data Client
        // TextView ...
        // Hints ...

        // If Client ID = Profile ID
        // binding.profileFabEdit.setVisibility(View.VISIBLE);

        // If professional
        // binding.profileTvPro.setVisibility(View.VISIBLE);
        // Else ...

        // FAB BINDING
        binding.profileFabEdit.setOnClickListener((View view) -> {
            editProfile();
        });
        binding.profileFabCheck.setOnClickListener((View view) -> {
            checkEditProfile();
        });
        binding.profileFabCancel.setOnClickListener((View view) -> {
            cancelEditProfile();
        });

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
            // Modifiy img?
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
}