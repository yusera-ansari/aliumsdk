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

import java.util.Iterator;

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
        if(savedInstanceState!=null){
            Log.d("SurveyDialogFragment", "LegacySurveyDialog-inside oncreyae");
            shouldUpdatePreferences=getArguments().getBoolean("shouldUpdatePreferences");
            surveyParameters=(SurveyParameters)getArguments().getSerializable("surveyParameters");
            Gson gson=new Gson();
            executableSurveySpecs=new ExecutableSurveySpecs(
                    gson.fromJson(getArguments().getString("surveyJson"), Survey.class)
                    , (LoadableSurveySpecs)getArguments().getSerializable("loadableSurveySpecs"));

        }else if(getArguments()!=null){
      Log.d("SurveyDialogFragment", "inside oncreyae");
       shouldUpdatePreferences=getArguments().getBoolean("shouldUpdatePreferences");
      surveyParameters=(SurveyParameters)getArguments().getSerializable("surveyParameters");
      Gson gson=new Gson();
      executableSurveySpecs=new ExecutableSurveySpecs(
              gson.fromJson(getArguments().getString("surveyJson"), Survey.class)
              , (LoadableSurveySpecs)getArguments().getSerializable("loadableSurveySpecs"));

  }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("ViewStateRestore", "Viewstate restored"+savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d("SurveyDialogFragment", "outside createDialog "+savedInstanceState);
        Log.d("SurveyDialogFragment", "outside oncreyae "+getArguments());
        Dialog dialogInstance=null;
        if (executableSurveySpecs != null && surveyParameters != null) {
            Log.d("SurveyDialogFragment", "inside createDialog");
            dialog = new SurveyDialog(requireContext(), executableSurveySpecs, surveyParameters,savedInstanceState==null?true: false);
            setCancelable(false);
//            if (savedInstanceState == null) {
//                Alium.activeSurveys.add(dialog);
//            }else{
//                boolean doesSurveyExist=false;
//                Log.d("ALium-survey", "Saved instance there, but list empty"+this.dialog);
//                if(!Alium.activeSurveys.isEmpty()){
//                    Log.d("ALium-survey", "contains-dialog"+Alium.activeSurveys.contains(dialog));
//
//                    Log.d("ActiverSurveys", "SDF"+Alium.activeSurveys);
//                    Iterator<SurveyDialog> keys= Alium.activeSurveys.iterator();
//
//                    while(keys.hasNext()){
//                        SurveyDialog currDialog=keys.next();
//                        if(currDialog.loadableSurveySpecs.key.equals( dialog.loadableSurveySpecs.key)){
//                            Log.d("activeSurvey", "SDF survey existes");
//                           doesSurveyExist=true;
//
//                        }
//
//                    }
//
//
//                }
//                if(!doesSurveyExist) Alium.activeSurveys.add(dialog);
//            }
            dialogInstance=dialog.getInstance();
        }
        else {
            throw new IllegalStateException("SurveyDialog cannot be initialized: missing data.");
        }
        if(((Activity)getContext()).isFinishing()||((Activity)getContext()).isDestroyed()){
//            if(isVisible()) {
//                dismiss();
//            } throw new IllegalStateException("SurveyDialog cannot be shown: activity paused.");

        }
dialogInstance.setOnCancelListener(new DialogInterface.OnCancelListener() {
    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d("Dialog", "dialog instanve cancelled");
    }
});
        dialogInstance.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d("Dialog", "diallog instance dissmised");
            }
        });
        return dialogInstance;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Dialog", "detached");
    }
}
