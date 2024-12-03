package com.dwao.alium.survey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.dwao.alium.models.Survey;
import com.google.gson.Gson;

public class LegacySurveyDialogFragment extends android.app.DialogFragment {
    private SurveyDialog dialog ;

    boolean shouldUpdatePreferences;
    SurveyParameters surveyParameters;
    ExecutableSurveySpecs executableSurveySpecs;
    public LegacySurveyDialogFragment(){
        Log.d("LegacySurveyDialog", "outside oncreyae ");
        Log.d("LegacySurveyDialog", "outside oncreyae "+getArguments());
    }


    @Override
    public void show(android.app.FragmentManager manager, String tag) {
        super.show(manager, tag);
        Log.d("LegacySurveyDialog", "Show dialog");
    }


    public static LegacySurveyDialogFragment newInstance(ExecutableSurveySpecs executableSurveySpecs,
                                                   SurveyParameters surveyParameters, boolean shouldUpdatePreferences){
        LegacySurveyDialogFragment legacySurveyDialogFragment=new LegacySurveyDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("surveyParameters",surveyParameters);
        Gson gson=new Gson();
        bundle.putSerializable("surveyJson",gson.toJson(executableSurveySpecs.survey) );
        bundle.putSerializable("loadableSurveySpecs", executableSurveySpecs.getLoadableSurveySpecs()
        );
        bundle.putBoolean("shouldUpdatePreferences", shouldUpdatePreferences);
        legacySurveyDialogFragment.setArguments(bundle);
        return legacySurveyDialogFragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("onSaveInstanceState", "LegacySurveyDialog-onSaveInstanceState");
        outState.putSerializable("surveyParameters",surveyParameters);
        Gson gson=new Gson();
        outState.putSerializable("surveyJson",gson.toJson(executableSurveySpecs.survey) );
        outState.putSerializable("loadableSurveySpecs", executableSurveySpecs.getLoadableSurveySpecs()
        );
        outState.putBoolean("shouldUpdatePreferences", shouldUpdatePreferences);
    }

//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        Log.d("onViewStateRestored","onViewStateRestored");
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SurveyDialogFragment", "LegacySurveyDialog-outside oncreyae "+savedInstanceState);
        Log.d("SurveyDialogFragment", "LegacySurveyDialog-outside oncreyae "+getArguments());
        if(getArguments()!=null){
            Log.d("SurveyDialogFragment", "LegacySurveyDialog-inside oncreyae");
            shouldUpdatePreferences=getArguments().getBoolean("shouldUpdatePreferences");
            surveyParameters=(SurveyParameters)getArguments().getSerializable("surveyParameters");
            Gson gson=new Gson();
            executableSurveySpecs=new ExecutableSurveySpecs(
                    gson.fromJson(getArguments().getString("surveyJson"), Survey.class)
                    , (LoadableSurveySpecs)getArguments().getSerializable("loadableSurveySpecs"));

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d("SurveyDialogFragment", "LegacySurveyDialog-outside createDialog "+savedInstanceState);
        Log.d("SurveyDialogFragment", "LegacySurveyDialog-outside oncreyae "+getArguments());
        if (executableSurveySpecs != null && surveyParameters != null) {
            Log.d("SurveyDialogFragment", "LegacySurveyDialog-inside createDialog");
            dialog = new SurveyDialog(getActivity(), executableSurveySpecs, surveyParameters, savedInstanceState==null?true: false);
            setCancelable(false);
            if (savedInstanceState == null) {
                Alium.activeSurveys.add(dialog);
            }
        }
        else {
            throw new IllegalStateException("SurveyDialog cannot be initialized: missing data.");
        }
        if((getActivity()).isFinishing()||((Activity)getActivity()).isDestroyed()){
//            if(isVisible()) {
//                dismiss();
//            } throw new IllegalStateException("SurveyDialog cannot be shown: activity paused.");

        }

        return dialog.getInstance();
    }

}
