package com.dwao.alium.survey;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.dwao.alium.models.Survey;
import com.google.gson.Gson;

import java.util.Iterator;

public class LegacySurveyDialogFragment extends android.app.DialogFragment {
    private SurveyDialog dialog ;
    private String loaderId;
    boolean shouldUpdatePreferences;
    SurveyParameters surveyParameters;
    ExecutableSurveySpecs executableSurveySpecs;
    private boolean shouldCallOnStopCallback=true;
    private AliumSurveyLoader.SurveyDialogCallback callback;

    public LegacySurveyDialogFragment(){

    }

    @Override
    public void show(android.app.FragmentManager manager, String tag) {
        super.show(manager, tag);
        Log.d("LegacySurveyDialog", "Show dialog");
    }

        public static LegacySurveyDialogFragment newInstance(ExecutableSurveySpecs executableSurveySpecs,
                                                             SurveyParameters surveyParameters, boolean shouldUpdatePreferences,
                                                             String loaderId
        ){
        LegacySurveyDialogFragment legacySurveyDialogFragment=new LegacySurveyDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("surveyParameters",surveyParameters);
        Gson gson=new Gson();
        bundle.putSerializable("surveyJson",gson.toJson(executableSurveySpecs.survey) );
        bundle.putSerializable("loadableSurveySpecs", executableSurveySpecs.getLoadableSurveySpecs()
        );
        bundle.putBoolean("shouldUpdatePreferences", shouldUpdatePreferences);
        bundle.putString("loaderId",loaderId );
        legacySurveyDialogFragment.setArguments(bundle);
        return legacySurveyDialogFragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("onsave", "on save instnace state");
        shouldCallOnStopCallback=false;
        outState.putSerializable("surveyParameters",surveyParameters);
        Gson gson=new Gson();
        outState.putSerializable("surveyJson",gson.toJson(executableSurveySpecs.survey) );
        outState.putSerializable("loadableSurveySpecs", executableSurveySpecs.getLoadableSurveySpecs()
        );
        Log.d("onSaveInstanceState", "saved state"+executableSurveySpecs.getLoadableSurveySpecs().getCurrentIndex());
        outState.putBoolean("shouldUpdatePreferences", shouldUpdatePreferences);
        outState.putString("loaderId", loaderId);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("SurveyDialogFragment", " savedInstanceStateLegacySurveyDialog-outside oncreyae "+savedInstanceState);
        Log.d("SurveyDialogFragment", "LegacySurveyDialog-outside oncreyae "+getArguments());

        if(savedInstanceState!=null){
            Log.d("SurveyDialogFragment", "LegacySurveyDialog-inside oncreyae");
            shouldUpdatePreferences=getArguments().getBoolean("shouldUpdatePreferences");
            surveyParameters=(SurveyParameters)getArguments().getSerializable("surveyParameters");
            Gson gson=new Gson();
            executableSurveySpecs=new ExecutableSurveySpecs(
                    gson.fromJson(getArguments().getString("surveyJson"), Survey.class)
                    , (LoadableSurveySpecs)getArguments().getSerializable("loadableSurveySpecs"));
            Log.d("SurveyDialogFragment", "saved state"+executableSurveySpecs.getLoadableSurveySpecs().getCurrentIndex());
            loaderId=getArguments().getString("loaderId");
            if(loaderId!=null){
                callback=SLQHandlerManager.reAttachCallback(loaderId, surveyParameters.screenName);
            }
        }else if(getArguments()!=null){
            Log.d("SurveyDialogFragment", "LegacySurveyDialog-inside oncreyae");
            shouldUpdatePreferences=getArguments().getBoolean("shouldUpdatePreferences");
            surveyParameters=(SurveyParameters)getArguments().getSerializable("surveyParameters");
            Gson gson=new Gson();
            executableSurveySpecs=new ExecutableSurveySpecs(
                    gson.fromJson(getArguments().getString("surveyJson"), Survey.class)
                    , (LoadableSurveySpecs)getArguments().getSerializable("loadableSurveySpecs"));
            loaderId=getArguments().getString("loaderId");
            if(loaderId!=null){
                callback=SLQHandlerManager.reAttachCallback(loaderId, surveyParameters.screenName);
            }
        }
        shouldCallOnStopCallback=true;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("ViewStateRestore", "Viewstate restored"+savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialogInstance=null;
        if (executableSurveySpecs != null && surveyParameters != null) {
            dialog = new SurveyDialog(getActivity(), executableSurveySpecs,
                    surveyParameters,savedInstanceState==null?true: false);
            setCancelable(false);
            if(savedInstanceState==null){
                try{
                    callback.onCreate(executableSurveySpecs.getLoadableSurveySpecs().key);
                }
                catch (Exception e){
                    Log.e("callbalCreate", e.toString());
                }
            }
            dialogInstance=dialog.getInstance();
        }
        else {
            throw new IllegalStateException("SurveyDialog cannot be initialized: missing data.");
        }



        return dialogInstance;
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("DialogFragment", "onDestroyView called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if(shouldCallOnStopCallback) callback.onStop(executableSurveySpecs.getLoadableSurveySpecs().key);
            callback=null;
            dialog=null;
        }catch (Exception e){
            Log.e("callbalstop", e.toString());
        }
        Log.d("DialogFragment", "onDestroy called");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Dialog", "detached");
    }
}
