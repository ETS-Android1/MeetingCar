package fr.flareden.meetingcar.ui.announces;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.flareden.meetingcar.databinding.FragmentAnnouncesBinding;

public class AnnouncesFragment extends Fragment {

    private FragmentAnnouncesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AnnouncesViewModel galleryViewModel =
                new ViewModelProvider(this).get(AnnouncesViewModel.class);

        binding = FragmentAnnouncesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTMP;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}