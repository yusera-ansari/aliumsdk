package com.dwao.alium.survey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleObserver;

import com.dwao.alium.R;
import com.dwao.alium.models.Survey;
import com.google.gson.Gson;

public class SurveyDialogFragment extends DialogFragment implements LifecycleObserver {
    private SurveyDialog dialog ;

    boolean shouldUpdatePreferences;
    SurveyParameters surveyParameters;
    ExecutableSurveySpecs executableSurveySpecs;
   public SurveyDialogFragment(){
        Log.d("SurveyDialogFragment", "outside oncreyae ");
        Log.d("SurveyDialogFragment", "outside oncreyae "+getArguments());
    }
    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {


        super.show(manager, tag);


        Log.d("SurveyDialogFragment", "Show dialog");
    }
    public static SurveyDialogFragment newInstance(ExecutableSurveySpecs executableSurveySpecs,
                                                   SurveyParameters surveyParameters, boolean shouldUpdatePreferences){
        SurveyDialogFragment surveyDialogFragment=new SurveyDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("surveyParameters",surveyParameters);
        Gson gson=new Gson();
        bundle.putSerializable("surveyJson",gson.toJson(executableSurveySpecs.survey) );
        bundle.putSerializable("loadableSurveySpecs", executableSurveySpecs.getLoadableSurveySpecs()
        );
        bundle.putBoolean("shouldUpdatePreferences", shouldUpdatePreferences);
        surveyDialogFragment.setArguments(bundle);
        return surveyDialogFragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("onSaveInstanceState", "onSaveInstanceState");
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
        Log.d("SurveyDialogFragment", "outside oncreyae "+savedInstanceState);
        Log.d("SurveyDialogFragment", "outside oncreyae "+getArguments());
  if(getArguments()!=null){
      Log.d("SurveyDialogFragment", "inside oncreyae");
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
        Log.d("SurveyDialogFragment", "outside createDialog "+savedInstanceState);
        Log.d("SurveyDialogFragment", "outside oncreyae "+getArguments());
        if (executableSurveySpecs != null && surveyParameters != null) {
            Log.d("SurveyDialogFragment", "inside createDialog");
            dialog = new SurveyDialog(requireContext(), executableSurveySpecs, surveyParameters,savedInstanceState==null?true: false);
setCancelable(false);
            if (savedInstanceState == null) {
                Alium.activeSurveys.add(dialog);
            }
        }
        else {
            throw new IllegalStateException("SurveyDialog cannot be initialized: missing data.");
        }
        if(((Activity)getContext()).isFinishing()||((Activity)getContext()).isDestroyed()){
//            if(isVisible()) {
//                dismiss();
//            } throw new IllegalStateException("SurveyDialog cannot be shown: activity paused.");

        }

        return dialog.getInstance();
    }


}
