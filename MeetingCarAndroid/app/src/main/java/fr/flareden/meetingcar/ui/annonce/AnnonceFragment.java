package fr.flareden.meetingcar.ui.annonce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.databinding.FragmentAnnonceBinding;
import fr.flareden.meetingcar.databinding.FragmentCreateAnnonceBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.metier.entity.Image;
import fr.flareden.meetingcar.metier.listener.IAnnonceLoaderHandler;
import fr.flareden.meetingcar.ui.home.AdvertViewModel;

public class AnnonceFragment extends Fragment {
    FragmentAnnonceBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnnonceBinding.inflate(inflater, container, false);
        Bundle args = this.getArguments();
        if(args== null){
            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();
            navController.navigate(R.id.nav_home);
        } else {
            int idAnnonce = args.getInt("idAnnonce", -1);
            if(idAnnonce < 0){
                NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();
                navController.navigate(R.id.nav_home);
            } else {
                CommunicationWebservice.getINSTANCE().getAnnonce(idAnnonce, new IAnnonceLoaderHandler() {
                    @Override
                    public void onAnnonceLoad(Annonce a) {
                        if(a != null){
                            getActivity().runOnUiThread(() -> {
                                binding.tvAnnonceTitre.setText(a.getTitle());
                                binding.tvAnnonceLoc.setText(a.getVendeur().getAdresse());
                                binding.tvAnnoncePrice.setText(""+ a.getPrix());
                                binding.tvAnnonceType.setText((a.isLocation() ? "RENT" : "SELL"));
                                binding.tvAnnonceDesc.setText((a.getDesc()));
                                
                            });
                        }
                    }

                    @Override
                    public void onImageAnnonceLoad(Annonce a, Image i) {
                    }
                });
            }
        }

        return binding.getRoot();
    }
}
