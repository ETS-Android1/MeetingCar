package fr.flareden.meetingcar.ui.annonce;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
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
                    int max = 0;
                    SortedMap<String, Integer> values = new TreeMap<>();
                    for(Visite v : visites){
                       String dateString = v.getHorodatage().substring(0,8);
                       if(values.containsKey(dateString)){
                           int val = values.get(dateString) + 1;
                           values.put(dateString, val);
                           if(max < val){
                               max = val;
                           }
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

                    int nbDate = (xVals[size-1] + 1) - (xVals[0] - 1);

                    binding.plotStats.setRangeBoundaries(0, max+1, BoundaryMode.FIXED);

                    binding.plotStats.setRangeStep(StepMode.INCREMENT_BY_FIT, (int)(max/5.0));
                    binding.plotStats.setDomainBoundaries(xVals[0] - 1, xVals[size-1] + 1, BoundaryMode.FIXED);
                    binding.plotStats.setDomainStep(StepMode.INCREMENT_BY_FIT, (int)(nbDate/5.0) +1);
                    binding.plotStats.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
                        @Override
                        public StringBuffer format(Object o, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                            stringBuffer.append(((int) Float.parseFloat(o.toString())));
                            return stringBuffer;
                        }

                        @Override
                        public Object parseObject(String s, ParsePosition parsePosition) {
                            return null;
                        }
                    });
                    binding.plotStats.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                        @Override
                        public StringBuffer format(Object o, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                            String value = "" + ((int) Float.parseFloat(o.toString()));


                            stringBuffer.append(value.substring(6,8) + "/" + value.substring(4,6));
                            return stringBuffer;
                        }

                        @Override
                        public Object parseObject(String s, ParsePosition parsePosition) {
                            return null;
                        }
                    });
                    binding.plotStats.redraw();
                    XYSeries serie = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "Visites");
                    LineAndPointFormatter format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
                    final int maxFinal = max;
                    getActivity().runOnUiThread(() -> {
                        binding.plotStats.clear();
                        binding.plotStats.addSeries(serie, format);
                        binding.plotStats.redraw();
                        binding.tvStatsMax.setText("" + maxFinal);
                    });
                });
            }
        }

        return binding.getRoot();
    }
}
