package com.dwao.alium.frequencyManager;

import com.dwao.alium.survey.CustomFreqSurveyData;
import com.dwao.alium.utils.preferences.AliumPreferences;

public  class FrequencyManagerFactory{
    public static SurveyFrequencyManager getFrequencyManager(AliumPreferences aliumPreferences,
                                                             String frq, CustomFreqSurveyData customFreqSurveyData){
        if(frq.matches("\\d+")){
            return new IntegerFrequencyManager(aliumPreferences);
        }
//     else if(frq.matches("\\d+-[dwm]")){
//         return new PeriodicFrequencyManager(aliumPreferences);
//     }
        else if(frq.matches("custom")){
            return new CustomFrequencyManager(aliumPreferences, customFreqSurveyData);
        }
        else  if(frq.matches("overandover")||frq.matches("onlyonce")||
                frq.matches("untilresponse")){
            return new BasicFrequencyManager(aliumPreferences);
        }
        else{
            throw new InvalidFrequencyException("The survey show frequency type: \""+frq+ "\" doesn't exist");
        }
    }
}
