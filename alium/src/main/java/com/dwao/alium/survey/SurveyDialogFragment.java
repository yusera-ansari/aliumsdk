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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleObserver;

import com.dwao.alium.models.ExecutableSurveySpecs;
import com.dwao.alium.models.LoadableSurveySpecs;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.SurveyParameters;
import com.dwao.alium.services.Logger;

public class SurveyDialogFragment extends DialogFragment implements LifecycleObserver {
    private SurveyDialog dialog ;
    boolean shouldUpdatePreferences;
    SurveyParameters surveyParameters;
//    private AliumSurveyLoader.SurveyDialogCallback callback;
    ExecutableSurveySpecs executableSurveySpecs;
    private String loaderId;
    private boolean shouldCallOnStopCallback=true;
    QuestionResponse currentQuestionResponse;
    public SurveyDialogFragment(){
    }
    @Override
    public void onStart() {
        super.onStart();
        shouldCallOnStopCallback = true;
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

        bundle.putSerializable("surveyJson", executableSurveySpecs.getSurvey() );
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

        outState.putSerializable("surveyJson",executableSurveySpecs.getSurvey());
        outState.putSerializable("loadableSurveySpecs",
                executableSurveySpecs.getLoadableSurveySpecs()
        );
        outState.putBoolean("shouldUpdatePreferences", shouldUpdatePreferences);
        outState.putString("loaderId", loaderId);
        outState.putSerializable("currentQuestionResponse", currentQuestionResponse);
        Logger.log(Logger.LogLevel.INFO, "Dial", "Instance State Saved");

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         if(savedInstanceState!=null){
            shouldUpdatePreferences=savedInstanceState.getBoolean("shouldUpdatePreferences");
            surveyParameters=(SurveyParameters)savedInstanceState.getSerializable("surveyParameters");
             if(Alium.isShouldResetOnBackground() &&!AliumRequestManager.getManager().isSurveyPresentInLoader(surveyParameters.screenName)){
                 Logger.log(Logger.LogLevel.INFO,"Dialog","config url is null" );

                 dismissAllowingStateLoss();
             }

            executableSurveySpecs=new ExecutableSurveySpecs(
                    (Survey)savedInstanceState.getSerializable("surveyJson")
                    , (LoadableSurveySpecs)savedInstanceState.getSerializable("loadableSurveySpecs"));

            loaderId=savedInstanceState.getString("loaderId");
            if(loaderId!=null){
//                callback= AliumRequestManager.reAttachCallback(loaderId, surveyParameters.screenName);
            }
            currentQuestionResponse = (QuestionResponse) savedInstanceState.getSerializable("currentQuestionResponse");

         }else if(getArguments()!=null){
       shouldUpdatePreferences=getArguments().getBoolean("shouldUpdatePreferences");
      surveyParameters=(SurveyParameters)getArguments().getSerializable("surveyParameters");

      executableSurveySpecs=new ExecutableSurveySpecs(
              (Survey)getArguments().getSerializable("surveyJson")
              , (LoadableSurveySpecs)getArguments().getSerializable("loadableSurveySpecs"));
            loaderId=getArguments().getString("loaderId");
            if(loaderId!=null){
//                callback= AliumRequestManager.reAttachCallback(loaderId, surveyParameters.screenName);
            }
            currentQuestionResponse = new QuestionResponse();

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

       if(getDialog()!=null){
           getDialog().setCancelable(true);
           getDialog().setCanceledOnTouchOutside(true);
       }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialogInstance=null;

        try{
            if (executableSurveySpecs != null && surveyParameters != null) {
                dialog = new SurveyDialog(requireContext(), executableSurveySpecs, surveyParameters, savedInstanceState == null ? true : false);
                dialog.currentQuestionResponse = currentQuestionResponse;
                setCancelable(false);
//                if (savedInstanceState == null) {
//                    try {
//                        callback.onCreate(executableSurveySpecs.getLoadableSurveySpecs().key);
//                    } catch (Exception e) {
//                        Logger.log(Logger.LogLevel.ERROR, "Dial-call-back", e.toString());
//                    }
//                }
                dialogInstance = dialog.getInstance();
            } else {
                throw new IllegalStateException("SurveyDialog cannot be initialized: missing data.");
            }
        }catch (Exception e){
            Logger.log(Logger.LogLevel.ERROR, "Dial-on-create", e.toString());
            dismissAllowingStateLoss();
            dialogInstance = new Dialog(requireContext()); // return an empty fallback dialog
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
        Logger.log(Logger.LogLevel.DEBUG,"Dial", "onDestroyView called");

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
//                AliumRequestManager.getManager().stop(surveyParameters.screenName);
            }
//            callback=null;
            dialog=null;
        }catch (Exception e){
            Logger.log(Logger.LogLevel.ERROR,"Dial-dest", e.toString());
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.log(Logger.LogLevel.DEBUG,"Dialog", "detached");
    }
}
