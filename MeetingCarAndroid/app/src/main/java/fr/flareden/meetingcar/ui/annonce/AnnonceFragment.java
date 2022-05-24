package fr.flareden.meetingcar.ui.annonce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.databinding.FragmentAnnonceBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.metier.entity.Image;
import fr.flareden.meetingcar.metier.listener.IAnnonceLoaderHandler;

public class AnnonceFragment extends Fragment {

    private FragmentAnnonceBinding binding;
    private Annonce annonce = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnnonceBinding.inflate(inflater, container, false);
        Bundle args = this.getArguments();

        // FAB BINDING
        binding.announceFabEdit.setOnClickListener((View view) -> editAnnounce());
        binding.announceFabCheck.setOnClickListener((View view) -> checkEditAnnounce());
        binding.announceFabCancel.setOnClickListener((View view) -> cancelEditAnnounce());

        if (args == null) {
            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();
            navController.navigate(R.id.nav_home);
        } else {
            int idAnnonce = args.getInt("idAnnonce", -1);
            if (idAnnonce < 0) {
                NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();
                navController.navigate(R.id.nav_home);
            } else {
                CommunicationWebservice.getINSTANCE().getAnnonce(idAnnonce, new IAnnonceLoaderHandler() {
                    @Override
                    public void onAnnonceLoad(Annonce a) {
                        if (a != null) {
                            annonce = a;
                            getActivity().runOnUiThread(() -> {
                                binding.tvAnnonceTitre.setText(a.getTitle());
                                binding.tvAnnonceLoc.setText(a.getVendeur().getAdresse());
                                binding.tvAnnoncePrice.setText("" + a.getPrix());
                                binding.tvAnnonceType.setText((a.isLocation() ? getResources().getString(R.string.rent) : getResources().getString(R.string.sell)));
                                binding.tvAnnonceDesc.setText((a.getDesc()));

                                binding.vpAnnonceImages.setAdapter(new ViewPagerAdapter(
                                        getContext(),
                                        new Image[]{
                                                new Image(0, getResources().getDrawable(R.drawable.ic_launcher_foreground, getActivity().getTheme())),
                                                new Image(0, getResources().getDrawable(R.drawable.ic_launcher_background, getActivity().getTheme()))
                                        })
                                );
                                if(Metier.getINSTANCE().getUtilisateur().getId() == annonce.getVendeur().getId()){
                                    binding.announceFabEdit.setVisibility(View.VISIBLE);
                                    binding.butAnnonceFollow.setVisibility(View.GONE);
                                    binding.butAnnonceFollow2.setVisibility(View.GONE);
                                }
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


    public void editAnnounce() {
        if (Metier.getINSTANCE().getUtilisateur().getId() == annonce.getVendeur().getId()) {
            // GONE
            binding.tvAnnonceTitre.setVisibility(View.GONE);
            binding.tvAnnoncePrice.setVisibility(View.GONE);
            binding.tvAnnonceType.setVisibility(View.GONE);
            binding.tvAnnonceDesc.setVisibility(View.GONE);
            binding.announceFabEdit.setVisibility(View.GONE);

            // VISIBLE
            binding.announceEditTitle.setVisibility(View.VISIBLE);
            binding.announceEditPrice.setVisibility(View.VISIBLE);
            binding.announceEditType.setVisibility(View.VISIBLE);
            binding.announceEditDesc.setVisibility(View.VISIBLE);
            binding.announceFabCheck.setVisibility(View.VISIBLE);
            binding.announceFabCancel.setVisibility(View.VISIBLE);

            // HINTS
            binding.announceEditTitle.setText(this.annonce.getTitle());
            binding.announceEditPrice.setText(this.annonce.getPrix() + "");

            String[] types = {getResources().getString(R.string.sell), getResources().getString(R.string.rent)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, types);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.announceEditType.setAdapter(adapter);
            binding.announceEditType.setSelection(this.annonce.isLocation() ? 1 : 0);

            binding.announceEditDesc.setText(this.annonce.getDesc());

        }
    }

    public void cancelEditAnnounce() {
        // GONE
        binding.announceEditTitle.setVisibility(View.GONE);
        binding.announceEditPrice.setVisibility(View.GONE);
        binding.announceEditType.setVisibility(View.GONE);
        binding.announceEditDesc.setVisibility(View.GONE);
        binding.announceFabCheck.setVisibility(View.GONE);
        binding.announceFabCancel.setVisibility(View.GONE);

        // VISIBLE
        binding.tvAnnonceTitre.setVisibility(View.VISIBLE);
        binding.tvAnnoncePrice.setVisibility(View.VISIBLE);
        binding.tvAnnonceType.setVisibility(View.VISIBLE);
        binding.tvAnnonceDesc.setVisibility(View.VISIBLE);
        binding.announceFabEdit.setVisibility(View.VISIBLE);
    }

    public void checkEditAnnounce() {
        if (Metier.getINSTANCE().getUtilisateur().getId() == annonce.getVendeur().getId()) {
            // GET STRING
            String title = binding.announceEditTitle.getText().toString().trim();
            String price = binding.announceEditPrice.getText().toString().trim();
            Boolean type = binding.announceEditType.getSelectedItemPosition() == 1;
            String desc = binding.announceEditDesc.getText().toString().trim();

            // UPDATE USER
            if (title.length() > 0) {
                annonce.setTitle(title);
            }
            if (price.length() > 0) {
                try {
                    float priceF = Float.parseFloat(price);
                    annonce.setPrix(priceF);
                } catch (NumberFormatException e) {
                }
            }
            annonce.setLocation(type);
            if (desc.length() > 0) {
                annonce.setDesc(desc);
            }

            cancelEditAnnounce();
            CommunicationWebservice.getINSTANCE().updateAnnonce(annonce, null, null);
            binding.tvAnnonceTitre.setText(annonce.getTitle());
            binding.tvAnnoncePrice.setText("" + annonce.getPrix());
            binding.tvAnnonceType.setText((annonce.isLocation() ? getResources().getString(R.string.rent) : getResources().getString(R.string.sell)));
            binding.tvAnnonceDesc.setText((annonce.getDesc()));
        }
    }
}
