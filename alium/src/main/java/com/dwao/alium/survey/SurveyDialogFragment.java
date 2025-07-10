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

import com.dwao.alium.models.Survey;

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
        Log.d("onsave", "on save instnace state");
        shouldCallOnStopCallback=false;
        outState.putSerializable("surveyParameters",surveyParameters);

        outState.putSerializable("surveyJson",executableSurveySpecs.getSurvey());
        outState.putSerializable("loadableSurveySpecs",
                executableSurveySpecs.getLoadableSurveySpecs()
        );
        Log.e("onSaveInstanceState",  " "+executableSurveySpecs.getLoadableSurveySpecs().currentIndex);
        outState.putBoolean("shouldUpdatePreferences", shouldUpdatePreferences);
        outState.putString("loaderId", loaderId);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SurveyDialogFragment", "outside oncreyae "+savedInstanceState);
        Log.d("SurveyDialogFragment", "outside oncreyae "+getArguments());
        if(savedInstanceState!=null){
            Log.d("SurveyDialogFragment", "instance saved-inside oncreyae");
            shouldUpdatePreferences=savedInstanceState.getBoolean("shouldUpdatePreferences");
            surveyParameters=(SurveyParameters)savedInstanceState.getSerializable("surveyParameters");

            executableSurveySpecs=new ExecutableSurveySpecs(
                    (Survey)savedInstanceState.getSerializable("surveyJson")
                    , (LoadableSurveySpecs)savedInstanceState.getSerializable("loadableSurveySpecs"));
            Log.e("savedInstanceState",  " "+executableSurveySpecs.getLoadableSurveySpecs().currentIndex);

            loaderId=savedInstanceState.getString("loaderId");
            if(loaderId!=null){
                callback= AliumRequestManager.reAttachCallback(loaderId, surveyParameters.screenName);
            }

        }else if(getArguments()!=null){
       shouldUpdatePreferences=getArguments().getBoolean("shouldUpdatePreferences");
      surveyParameters=(SurveyParameters)getArguments().getSerializable("surveyParameters");

      executableSurveySpecs=new ExecutableSurveySpecs(
              (Survey)getArguments().getSerializable("surveyJson")
              , (LoadableSurveySpecs)getArguments().getSerializable("loadableSurveySpecs"));
            loaderId=getArguments().getString("loaderId");
            if(loaderId!=null){
                callback= AliumRequestManager.reAttachCallback(loaderId, surveyParameters.screenName);
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
            dialogInstance=dialog.getInstance();
        }
        else {
            throw new IllegalStateException("SurveyDialog cannot be initialized: missing data.");
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
