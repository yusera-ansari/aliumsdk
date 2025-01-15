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
    private AliumSurveyLoader.SurveyDialogCallback callback;
    ExecutableSurveySpecs executableSurveySpecs;
    private String loaderId;
    private boolean shouldCallOnStopCallback=true;
    public SurveyDialogFragment(){
    }
    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
    }

    public static SurveyDialogFragment newInstance(ExecutableSurveySpecs executableSurveySpecs,
                                                   SurveyParameters surveyParameters, boolean shouldUpdatePreferences,
                                                   String loaderId
                                                   ){
        SurveyDialogFragment surveyDialogFragment=new SurveyDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("surveyParameters",surveyParameters);
        Gson gson=new Gson();
        bundle.putSerializable("surveyJson",gson.toJson(executableSurveySpecs.survey) );
        bundle.putSerializable("loadableSurveySpecs", executableSurveySpecs.getLoadableSurveySpecs()
        );

        bundle.putBoolean("shouldUpdatePreferences", shouldUpdatePreferences);
        bundle.putString("loaderId",loaderId );

        surveyDialogFragment.setArguments(bundle);
        return surveyDialogFragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        shouldCallOnStopCallback=false;
        outState.putSerializable("surveyParameters",surveyParameters);
        Gson gson=new Gson();
        outState.putSerializable("surveyJson",gson.toJson(executableSurveySpecs.survey) );
        outState.putSerializable("loadableSurveySpecs", executableSurveySpecs.getLoadableSurveySpecs()
        );
        outState.putBoolean("shouldUpdatePreferences", shouldUpdatePreferences);
        outState.putString("loaderId", loaderId);
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
            loaderId=getArguments().getString("loaderId");
            if(loaderId!=null){
                callback=Alium.reAttachCallback(loaderId, surveyParameters.screenName);
            }

        }else if(getArguments()!=null){
       shouldUpdatePreferences=getArguments().getBoolean("shouldUpdatePreferences");
      surveyParameters=(SurveyParameters)getArguments().getSerializable("surveyParameters");
      Gson gson=new Gson();
      executableSurveySpecs=new ExecutableSurveySpecs(
              gson.fromJson(getArguments().getString("surveyJson"), Survey.class)
              , (LoadableSurveySpecs)getArguments().getSerializable("loadableSurveySpecs"));
            loaderId=getArguments().getString("loaderId");
            if(loaderId!=null){
                callback=Alium.reAttachCallback(loaderId, surveyParameters.screenName);
            }

  }
shouldCallOnStopCallback=true;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialogInstance=null;
        if (executableSurveySpecs != null && surveyParameters != null) {
            dialog = new SurveyDialog(requireContext(), executableSurveySpecs, surveyParameters,savedInstanceState==null?true: false);
            setCancelable(false);
            if(savedInstanceState==null){
             try{
                 callback.onCreate(executableSurveySpecs.getLoadableSurveySpecs().key);
             }
             catch (Exception e){
                 Log.e("callbalCreate", e.toString());
             }
            }
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


        return dialogInstance;
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
       try {
           if(shouldCallOnStopCallback) callback.onStop(executableSurveySpecs.getLoadableSurveySpecs().key);
        }catch (Exception e){
            Log.e("callbalstop", e.toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("DialogFragment", "onDestroyView called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DialogFragment", "onDestroy called");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Dialog", "detached");
    }
}
