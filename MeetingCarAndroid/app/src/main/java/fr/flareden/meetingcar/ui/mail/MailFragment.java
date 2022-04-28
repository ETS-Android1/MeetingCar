package fr.flareden.meetingcar.ui.mail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.flareden.meetingcar.databinding.FragmentMailBinding;

public class MailFragment extends Fragment {

    private FragmentMailBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MailViewModel galleryViewModel =
                new ViewModelProvider(this).get(MailViewModel.class);

        binding = FragmentMailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMail;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}