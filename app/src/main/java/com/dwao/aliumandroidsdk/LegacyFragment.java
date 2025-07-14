package com.dwao.aliumandroidsdk;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.dwao.alium.models.SurveyParameters;
import com.dwao.alium.survey.Alium;

public class LegacyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate your layout
        return inflater.inflate(R.layout.fragment_legacy, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(savedInstanceState == null){
            Log.e("oncreate", "dialog created legacy");
            Alium.trigger(getActivity(), new SurveyParameters("screen4"));

//        }
        Log.e("ONCREATE", "vre ted");
         }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("frag", "calling on stop on self...");
//        Alium.stop("screen4");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
