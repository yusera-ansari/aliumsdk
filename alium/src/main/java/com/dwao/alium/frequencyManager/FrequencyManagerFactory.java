package com.dwao.alium.frequencyManager;

import com.dwao.alium.models.CustomFreqSurveyData;
import com.dwao.alium.utils.preferences.AliumPreferences;

public  class FrequencyManagerFactory{
    public static SurveyFrequencyManager getFrequencyManager(AliumPreferences aliumPreferences,
                                                             String key,
                                                             String srvShowFreq, CustomFreqSurveyData customFreqSurveyData){
        if(srvShowFreq.matches("\\d+")){
            return new IntegerFrequencyManager(aliumPreferences, key, srvShowFreq);
        }
//     else if(frq.matches("\\d+-[dwm]")){
//         return new PeriodicFrequencyManager(aliumPreferences);
//     }
        else if(srvShowFreq.matches("custom")){
            if(customFreqSurveyData==null){
                throw new InvalidCustomFrequencyDataException("Custom frequency data cannot be null");
            }
            return new CustomFrequencyManager(aliumPreferences,
                    key, srvShowFreq,
                    customFreqSurveyData);
        }
        //os: Once per Submit -untilresponse
        //o: Once - onlyonce
        //rp: Repeatedly -overandover
        else  if(srvShowFreq.matches("rp")||srvShowFreq.matches("o")||
                srvShowFreq.matches("os")){
            return new BasicFrequencyManager(aliumPreferences, key, srvShowFreq);
        }
        else{
            throw new InvalidFrequencyException("The survey show frequency type: \""+srvShowFreq+ "\" doesn't exist");
        }
    }
}
