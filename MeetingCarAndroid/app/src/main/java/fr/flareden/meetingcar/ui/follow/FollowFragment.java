package fr.flareden.meetingcar.ui.follow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.flareden.meetingcar.databinding.FragmentFollowBinding;

public class FollowFragment extends Fragment {

    private FragmentFollowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FollowViewModel galleryViewModel =
                new ViewModelProvider(this).get(FollowViewModel.class);

        binding = FragmentFollowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textFollow;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}