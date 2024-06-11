package com.dwao.alium.survey;

import static com.dwao.alium.utils.Util.generateCustomerId;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.dwao.alium.R;
import com.dwao.alium.adapters.CheckBoxRecyViewAdapter;
import com.dwao.alium.adapters.NpsGridViewAdapter;
import com.dwao.alium.adapters.RadioBtnAdapter;
import com.dwao.alium.listeners.CheckBoxClickListener;
import com.dwao.alium.listeners.NpsOptionClickListener;
import com.dwao.alium.listeners.RadioClickListener;
import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.models.Question;
import com.dwao.alium.models.QuestionResponse;

import com.dwao.alium.models.Survey;
import com.dwao.alium.models.SurveyConfig;

import com.dwao.alium.network.VolleyService;
import com.dwao.alium.utils.preferences.AliumPreferences;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Alium {
     private static JSONObject surveyConfigJSON;





//        private static Map<String, SurveyConfig> surveyConfigMap;

        private  Gson gson;
        private static  Alium instance;

        private static VolleyService volleyService;
        String uuid, currentScreen;
        private static String configURL;
        private SurveyParameters surveyParameters;

        private  Alium(){
            volleyService=new VolleyService();
            surveyConfigJSON=new JSONObject();
        }
        private Alium(SurveyParameters surveyParameters){
            this.surveyParameters=surveyParameters;

        }

    public SurveyParameters getSurveyParameters() {
        return surveyParameters;
    }

    public String getUuid() {
        return uuid;
    }



//    public static Map<String, SurveyConfig> getSurveyConfigMap() {
//        return surveyConfigMap;
//    }

//    public static void setSurveyConfigMap(Map<String, SurveyConfig> surveyConfigMap) {
//        Alium.surveyConfigMap = surveyConfigMap;
//    }

    public String getCurrentScreen() {
        return currentScreen;
    }

 static AppLifeCycleListener appLifeCycleListener;

    public static void configure(Application application, String url){
            if(instance==null){
                instance=new Alium();
            }
            appLifeCycleListener=new AppLifeCycleListener();
            application.registerActivityLifecycleCallbacks(appLifeCycleListener);
            configURL=url;
//        volleyService=new VolleyService();
//        surveyConfigJSON=new JSONObject();
//            surveyConfigMap=new HashMap<>();
        }

        public static void loadAliumSurvey(Context ctx, SurveyParameters parameters){
            if (configURL == null) {
                throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
            }
            if(surveyConfigJSON.length()==0) {
                volleyService.callVolley(ctx, configURL, new ConfigURLResponseListener(ctx, parameters));
                Log.d("Alium-initialized", "calling survey on" + parameters.screenName);
            }else{
                Log.d("helllo", surveyConfigJSON.toString());
                new AliumSurveyLoader(ctx, parameters, surveyConfigJSON)
                        .showSurvey();
            }

        }
        private static class ConfigURLResponseListener implements VolleyResponseListener{
            Context context;
            SurveyParameters surveyParameters;
            ConfigURLResponseListener(Context ctx,SurveyParameters parameters){
                Log.d("context", ((Activity)ctx).getTitle().toString());
                context=ctx;
                surveyParameters=parameters;
            }
            @Override
            public void onResponseReceived(JSONObject jsonObject) {
                surveyConfigJSON=jsonObject;
                Log.d("Alium-Config", jsonObject.toString());
                new AliumSurveyLoader(context, surveyParameters, surveyConfigJSON)
                        .showSurvey();

            }
        }




}
