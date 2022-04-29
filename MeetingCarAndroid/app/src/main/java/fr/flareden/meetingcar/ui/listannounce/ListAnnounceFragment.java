package fr.flareden.meetingcar.ui.listannounce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.flareden.meetingcar.databinding.FragmentListannounceBinding;

public class ListAnnounceFragment extends Fragment {

    private FragmentListannounceBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ListAnnounceViewModel galleryViewModel =
                new ViewModelProvider(this).get(ListAnnounceViewModel.class);

        binding = FragmentListannounceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAd;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
