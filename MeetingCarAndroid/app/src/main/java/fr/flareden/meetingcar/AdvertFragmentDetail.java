package fr.flareden.meetingcar;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AdvertFragmentDetail extends Fragment {

    private TextView textTitle;
    private TextView textLoc;
    private TextView textType;
    private TextView textPrice;
    private TextView textDesc;
    private Button buttonCall;
    private Button buttonFollow;

    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_advert_detail, container, false);

        // DATA
        textTitle = view.findViewById(R.id.fragAdDetail_tv_title);
        textLoc = view.findViewById(R.id.fragAdDetail_tv_loc);
        textType = view.findViewById(R.id.fragAdDetail_tv_type);
        textPrice = view.findViewById(R.id.fragAdDetail_tv_price);
        textDesc = view.findViewById(R.id.fragAdDetail_tv_desc);
        buttonCall = view.findViewById(R.id.fragAdDetail_btn_call);
        buttonFollow = view.findViewById(R.id.fragAdDetail_btn_follow);

        // ACTION
        // buttonCall.setOnClickListener(v -> );
        // buttonFollow.setOnClickListener(v -> );

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
    }
}