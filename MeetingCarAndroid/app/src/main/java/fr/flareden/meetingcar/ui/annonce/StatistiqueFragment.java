package fr.flareden.meetingcar.ui.annonce;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import fr.flareden.meetingcar.R;
import fr.flareden.meetingcar.databinding.FragmentStatistiqueBinding;
import fr.flareden.meetingcar.metier.CommunicationWebservice;
import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.metier.entity.Visite;

public class StatistiqueFragment extends Fragment {
    private FragmentStatistiqueBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatistiqueBinding.inflate(inflater, container, false);
        Bundle args = this.getArguments();

        if(args == null){
            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();
            navController.navigate(R.id.nav_home);
        } else {
            Annonce a = (Annonce) args.getSerializable("annonce");
            if(a == null){
                NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();
                navController.navigate(R.id.nav_home);
            } else {
                binding.tvStatsTitre.setText(a.getTitle());
                CommunicationWebservice.getINSTANCE().getVisites(a, visites -> {
                    SortedMap<String, Integer> values = new TreeMap<>();
                    for(Visite v : visites){
                       String dateString = v.getHorodatage().substring(0,8);
                       if(values.containsKey(dateString)){
                           values.put(dateString, values.get(dateString) + 1);
                       } else {
                           values.put(dateString, 1);
                       }
                    }

                    ArrayList<Map.Entry<String,Integer>> entry = new ArrayList<>(values.entrySet());
                    int size = entry.size();

                    Integer[] xVals = new Integer[size];
                    Integer[] yVals = new Integer[size];

                    for(int i = 0; i < size;i++ ){
                       xVals[i] = Integer.parseInt(entry.get(i).getKey());
                        yVals[i] = entry.get(i).getValue();
                    }
                    XYSeries serie = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "oui");
                    LineAndPointFormatter format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
                    getActivity().runOnUiThread(() -> {
                        binding.plotStats.clear();
                        binding.plotStats.addSeries(serie, format);
                        binding.plotStats.redraw();
                    });
                });
            }
        }

        return binding.getRoot();
    }
}
