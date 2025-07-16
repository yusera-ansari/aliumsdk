package com.dwao.alium.survey;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dwao.alium.models.ExecutableSurveySpecs;
import com.dwao.alium.models.LoadableSurveySpecs;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.SurveyParameters;
import com.dwao.alium.services.Logger;

public class LegacySurveyDialogFragment extends android.app.DialogFragment {
    private SurveyDialog dialog ;
    private String loaderId;
    boolean shouldUpdatePreferences;
    SurveyParameters surveyParameters;
    ExecutableSurveySpecs executableSurveySpecs;
    private boolean shouldCallOnStopCallback=true;
    private AliumSurveyLoader.SurveyDialogCallback callback;
    QuestionResponse currentQuestionResponse;

    public LegacySurveyDialogFragment(){

    }

    @Override
    public void show(android.app.FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

        public static LegacySurveyDialogFragment newInstance(ExecutableSurveySpecs executableSurveySpecs,
                                                             SurveyParameters surveyParameters, boolean shouldUpdatePreferences,
                                                             String loaderId
        ){
        LegacySurveyDialogFragment legacySurveyDialogFragment=new LegacySurveyDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("surveyParameters",surveyParameters);

        bundle.putSerializable("surveyJson" ,executableSurveySpecs.getSurvey() );
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
        shouldCallOnStopCallback=false;
        outState.putSerializable("surveyParameters",surveyParameters);

        outState.putSerializable("surveyJson",executableSurveySpecs.getSurvey());
        outState.putSerializable("loadableSurveySpecs", executableSurveySpecs.getLoadableSurveySpecs()
        );
        outState.putBoolean("shouldUpdatePreferences", shouldUpdatePreferences);
        outState.putString("loaderId", loaderId);
        outState.putSerializable("currentQuestionResponse", currentQuestionResponse);
        Logger.log(Logger.LogLevel.INFO, "LDial", "Instance State Saved");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(savedInstanceState!=null){

            shouldUpdatePreferences=savedInstanceState.getBoolean("shouldUpdatePreferences");
            surveyParameters=(SurveyParameters)savedInstanceState.getSerializable("surveyParameters");

            executableSurveySpecs=new ExecutableSurveySpecs( (Survey)savedInstanceState.getSerializable("surveyJson")
                    , (LoadableSurveySpecs)savedInstanceState.getSerializable("loadableSurveySpecs"));

            loaderId=savedInstanceState.getString("loaderId");
            currentQuestionResponse = (QuestionResponse) savedInstanceState.getSerializable("currentQuestionResponse");
            if(loaderId!=null){
//                callback= AliumRequestManager.reAttachCallback(loaderId, surveyParameters.screenName);
            }
            Logger.log(Logger.LogLevel.INFO, "LDial-create", "Saved Instance State retrieved");
        }else if(getArguments()!=null){
             shouldUpdatePreferences=getArguments().getBoolean("shouldUpdatePreferences");
            surveyParameters=(SurveyParameters)getArguments().getSerializable("surveyParameters");

            executableSurveySpecs=new ExecutableSurveySpecs( (Survey)getArguments().getSerializable("surveyJson")
                    ,(LoadableSurveySpecs)getArguments().getSerializable("loadableSurveySpecs"));
            loaderId=getArguments().getString("loaderId");
            if(loaderId!=null){
//                 callback= AliumRequestManager.reAttachCallback(loaderId, surveyParameters.screenName);
            }
            currentQuestionResponse = new QuestionResponse();
            Logger.log(Logger.LogLevel.INFO, "LDial-create", "retrieved arguments");
        }
        shouldCallOnStopCallback=true;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialogInstance=null;
        try{
            if (executableSurveySpecs != null && surveyParameters != null) {
                dialog = new SurveyDialog(getActivity(), executableSurveySpecs,
                        surveyParameters, savedInstanceState == null);
                dialog.currentQuestionResponse = currentQuestionResponse;
                setCancelable(false);
//                if(savedInstanceState==null){
//                    try{
//                         callback.onCreate(executableSurveySpecs.getLoadableSurveySpecs().key);
//                    }
//                    catch (Exception e){
//                        Logger.log(Logger.LogLevel.ERROR,"LDial-call-back", e.toString());
//                    }
//                }
                dialogInstance=dialog.getInstance();
            }
            else {
                throw new IllegalStateException("SurveyDialog cannot be initialized: missing data.");
            }
        }catch (Exception e){
            Logger.log(Logger.LogLevel.ERROR, "LDial-on-create", e.toString());
            dismissAllowingStateLoss();
            dialogInstance = new Dialog(getActivity()); // return an empty fallback dialog
            dialogInstance.setCancelable(true);
            dialogInstance.setOnShowListener(d -> dismissAllowingStateLoss());
        }

        setCancelable(true);

        return dialogInstance;
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.log(Logger.LogLevel.DEBUG,"LDial", "onDestroyView called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {

//            if (callback != null && executableSurveySpecs != null && shouldCallOnStopCallback) {
//                callback.onStop(executableSurveySpecs.getLoadableSurveySpecs().key);
//            }else
            if(surveyParameters != null&& shouldCallOnStopCallback){
                Alium.stop(surveyParameters.screenName);
            }
            callback=null;
            dialog=null;
        }catch (Exception e){
            Logger.log(Logger.LogLevel.ERROR,"LDial-dest", e.toString());
        }

    }
    @Override
    public void onDetach() {
        super.onDetach();
        Logger.log(Logger.LogLevel.DEBUG,"LDialog", "detached");
    }
}
