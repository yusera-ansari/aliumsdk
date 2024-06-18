package com.dwao.alium.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class AliumPreferences {
    private static AliumPreferences instance;
    private static final String ALIUM_PREFS = "AliumPrefs";
    private SharedPreferences.Editor editor;
    private SharedPreferences aliumSharedPreferences;

    public SharedPreferences getAliumSharedPreferences() {
        return aliumSharedPreferences;
    }
    public void setCustomerId(String customerId){
      editor.putString("customerId", customerId);
      editor.apply();
      Log.i("customerId", "Customer Id generated "+
              aliumSharedPreferences.getString("customerId",""));
    }
    public String getCustomerId(){
        return aliumSharedPreferences.getString("customerId", "");
    }

    public static AliumPreferences getInstance(Context ctx) {
        if (instance == null) {
            synchronized (AliumPreferences.class) {
                instance = new AliumPreferences(ctx);
            }
        }
        return instance;
    }

    private AliumPreferences() {}

    private AliumPreferences(Context context) {
        aliumSharedPreferences = context.getSharedPreferences(ALIUM_PREFS, Context.MODE_PRIVATE);
        editor = aliumSharedPreferences.edit();
    }

    public boolean checkForBasicFrequencyUpdate(String key, String freq) {
        if (!aliumSharedPreferences.getString(key, "").isEmpty()) {
            Log.i("srvshowfrq-changed", aliumSharedPreferences.getString(key, "")+" "+key+ freq);
            if (!aliumSharedPreferences.getString(key, "").equals(freq)) {
                   Log.i("srvshowfrq-changed", "updating stored preferences data"+key+ freq);
                   editor.remove(key);
                   editor.apply();
                   return true;

            } else {
                return false;
            }
        }
        return true;
    }
    public void addToAliumPreferences(String key, String value){
        Log.d("key", key+" "+value);
        editor.putString(key,value);
        editor.apply();

    }
    public void handleSimpleFrequencyCount(int freq, String key) throws JSONException {
        JSONObject freqObj=new JSONObject();
        Log.d("showFreq", " "+freq);
        freqObj.put("showFreq",freq);
        freqObj.put("counter", 0);

        if (!aliumSharedPreferences.getString(key, "").isEmpty()) {
            JSONObject storedFreq=
                    new JSONObject(aliumSharedPreferences.getString(key, ""))
                    ;

            if(storedFreq.getInt("showFreq")!=freq){
                freqObj.put("counter", 1);
            }else if(storedFreq.getInt("counter")!=freq){
                freqObj.put("counter", storedFreq.getInt("counter")+1);
            }else{
                return;
            }
        } else {
            freqObj.put("counter",1);
        }
        addToAliumPreferences(key, freqObj.toString());
        Log.i("showFreq-changed", ""+aliumSharedPreferences.getString(key, "")
                +" "+freqObj);
    }


    public void handlePeriodicFrequencyCount(String[] freqData, String key)  {
    try{
        Log.d("handlePeriodicFreqCount", "frequency pppp"+ Arrays.toString(freqData));
        switch (freqData[1]){
            case "d": {
                Log.d("frequency[1]", "frequency[1] is   in range..." + freqData[1]);
                String freq = Arrays.toString(freqData);
                Log.d("frequency[1]", "frequency[1] is   in range..." + freqData[1]);
                JSONObject freqObj = new JSONObject();
                Log.d("showFreq", "" + freqData[0] + "-" + freqData[1]);

                freqObj.put("showFreq", freqData[0] + "-" + freqData[1]);
                freqObj.put("counter", 0);
                freqObj.put("maxCount", Integer.parseInt(freqData[0]));
                freqObj.put("period", freqData[1]);
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String date = format.format(today);
                freqObj.put("lastShownOn", date);
                Log.d("frequency", date);
                Log.d("frequency", freqObj.toString());
                if (!aliumSharedPreferences.getString(key, "").isEmpty()) {
                    JSONObject storedFreq =
                            new JSONObject(aliumSharedPreferences.getString(key, ""));
                    Log.d("frequency", "alium preference does have your key " + storedFreq);
                    if (!storedFreq.getString("lastShownOn").equals(date)) {
                        freqObj.put("counter", 1);
                    }else
                    if (!storedFreq.getString("showFreq").equals(freqData[0] + "-" + freqData[1])) {
                        freqObj.put("counter", 1);
                        Log.d("frequency", "stored freq and current freq are not same " + storedFreq.getString("showFreq")
                                + " current " + freqData[0] + "-" + freqData[1]);
                    } else if (storedFreq.getInt("counter") != storedFreq.getInt("maxCount")) {
                        Log.d("frequency", "max-count: " + storedFreq.getInt("maxCount") +
                                " counter: " + storedFreq.getInt("counter"));
                        freqObj.put("counter", storedFreq.getInt("counter") + 1);

                    }    else {
                        Log.d("sfinal-frequency", storedFreq.toString());
                        return;
                    }
                } else {
                    Log.d("frequency", "alium preference does not have your key ");
                    freqObj.put("counter", 1);
                }
                Log.d("final-frequency", freqObj.toString());
                addToAliumPreferences(key, freqObj.toString());


                break;}
            case "w": {
                Log.d("frequency[1]", "frequency[1] is   in range..." + freqData[1]);
                String freq = Arrays.toString(freqData);
                Log.d("frequency[1]", "frequency[1] is   in range..." + freqData[1]);
                JSONObject freqObj = new JSONObject();
                Log.d("showFreq", "" + freqData[0] + "-" + freqData[1]);

                freqObj.put("showFreq", freqData[0] + "-" + freqData[1]);
                freqObj.put("counter", 0);
                freqObj.put("maxCount", Integer.parseInt(freqData[0]));
                freqObj.put("period", freqData[1]);
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String date = format.format(today);

                Calendar nextShowOn = Calendar.getInstance();
                nextShowOn.add(Calendar.DAY_OF_MONTH, 7);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                String nextShowOnDate = format1.format(nextShowOn.getTime());
                Log.d("date", nextShowOnDate);

                freqObj.put("lastShownOn", date);
                freqObj.put("nextShowOn", nextShowOnDate);

                Log.d("frequency", today.toString());
                Log.d("frequency", freqObj.toString());
                //check if key exists
                if (!aliumSharedPreferences.getString(key, "").isEmpty()) {
                    JSONObject storedFreq =
                            new JSONObject(aliumSharedPreferences.getString(key, ""));
                    Log.d("frequency", "alium preference does have your key " + storedFreq);
                    //check if show frequency has changed
                    if (!storedFreq.getString("showFreq").equals(freqData[0] + "-" + freqData[1])) {
                        //if changed update to new
                        freqObj.put("counter", 1);
                        Log.d("frequency", "stored freq and current freq are not same " + storedFreq.getString("showFreq")
                                + " current " + freqData[0] + "-" + freqData[1]);
                    } else if (storedFreq.getString("lastShownOn").equals(date)) {
                        //check if last shown is equals to today's date
                        if (storedFreq.getInt("counter") != storedFreq.getInt("maxCount")) {
                            //check if counter has reached max value, if not update!
                            Log.d("frequency", "max-count: " + storedFreq.getInt("maxCount") +
                                    " counter: " + storedFreq.getInt("counter"));
                            freqObj.put("counter", storedFreq.getInt("counter") + 1);

                        }
                    } else if (storedFreq.getString("nextShowOn").equals(date)) {
                        //check if reset date and today's date are equal
                        freqObj.put("counter", 1);
                    } else {
                        Log.d("frequency", "else");
                        Date todaysDate=format.parse(date);
                        Date nextShowOnDateParsed=format.parse(storedFreq.getString("nextShowOn"));
                        if(todaysDate.compareTo(nextShowOnDateParsed)>0){//today is after next show

                            freqObj.put("counter", 1);
                            Log.d("frequency", "else---"+freqObj);

                            } else if(todaysDate.compareTo(nextShowOnDateParsed)<0){//today is before next show
                                if (storedFreq.getInt("counter") != storedFreq.getInt("maxCount")) {
                                    //check if counter has reached max value, if not update!
                                    Log.d("frequency", "max-count: " + storedFreq.getInt("maxCount") +
                                            " counter: " + storedFreq.getInt("counter"));
                                    freqObj.put("counter", storedFreq.getInt("counter") + 1);
                                    freqObj.put("lastShownOn", storedFreq.getString("lastShownOn"));
                                    freqObj.put("nextShowOn", storedFreq.getString("nextShowOn"));
                                }else{
                                    return;
                                }
                            }else {
                                Log.d("sfinal-frequency", storedFreq.toString());
                                return;
                            }
                    }
                } else { //key doesn't exists
                    Log.d("frequency", "alium preference does not have your key ");
                    freqObj.put("counter", 1);
                }
                Log.d("final-frequency", freqObj.toString());
                addToAliumPreferences(key, freqObj.toString());


                break;
            }
            case "m":
            {
                Log.d("frequency[1]", "frequency[1] is   in range..." + freqData[1]);
                String freq = Arrays.toString(freqData);
                Log.d("frequency[1]", "frequency[1] is   in range..." + freqData[1]);
                JSONObject freqObj = new JSONObject();
                Log.d("showFreq", "" + freqData[0] + "-" + freqData[1]);

                freqObj.put("showFreq", freqData[0] + "-" + freqData[1]);
                freqObj.put("counter", 0);
                freqObj.put("maxCount", Integer.parseInt(freqData[0]));
                freqObj.put("period", freqData[1]);
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String date = format.format(today);

                Calendar nextShowOn= Calendar.getInstance();
                nextShowOn.add(Calendar.DAY_OF_MONTH,30 );

                String nextShowOnDate=format.format(nextShowOn.getTime());
                Log.d("date", nextShowOnDate);

                freqObj.put("lastShownOn", date);
                freqObj.put("nextShowOn", nextShowOnDate);

                Log.d("frequency", today.toString());
                Log.d("frequency", freqObj.toString());
                //check if key exists
                if (!aliumSharedPreferences.getString(key, "").isEmpty()) {
                    JSONObject storedFreq =
                            new JSONObject(aliumSharedPreferences.getString(key, ""));
                    Log.d("frequency", "alium preference does have your key " + storedFreq);
                    //check if show frequency has changed
                    if (!storedFreq.getString("showFreq").equals(freqData[0] + "-" + freqData[1])) {
                        //if changed update to new
                        freqObj.put("counter", 1);
                        Log.d("frequency", "stored freq and current freq are not same " + storedFreq.getString("showFreq")
                                + " current " + freqData[0] + "-" + freqData[1]);
                    }else if(storedFreq.getString("lastShownOn").equals(date)){
                        if (storedFreq.getInt("counter") != storedFreq.getInt("maxCount")) {
                            //check if counter has reached max value, if not update!
                            Log.d("frequency", "max-count: " + storedFreq.getInt("maxCount") +
                                    " counter: " + storedFreq.getInt("counter"));
                            freqObj.put("counter", storedFreq.getInt("counter") + 1);

                        }
                    }
                    else if (storedFreq.getString("nextShowOn").equals(date)) {
                        freqObj.put("counter", 1);
                    }

                    else {
                        Log.d("frequency", "else");
                        Date todaysDate=format.parse(date);
                        Date nextShowOnDateParsed=format.parse(storedFreq.getString("nextShowOn"));
                        if(todaysDate.compareTo(nextShowOnDateParsed)>0){//today is after next show

                            freqObj.put("counter", 1);
                            Log.d("frequency", "else---"+freqObj);
                        }
                        else if(todaysDate.compareTo(nextShowOnDateParsed)<0){//today is before next show
                            Log.d("frequency", "today is before " + storedFreq.getInt("maxCount") +
                                    " counter: " + storedFreq.getInt("counter"));

                            if (storedFreq.getInt("counter") != storedFreq.getInt("maxCount")) {
                                //check if counter has reached max value, if not update!
                                Log.d("frequency", "max-count: " + storedFreq.getInt("maxCount") +
                                        " counter: " + storedFreq.getInt("counter"));
                                freqObj.put("counter", storedFreq.getInt("counter") + 1);
                                freqObj.put("lastShownOn", storedFreq.getString("lastShownOn"));
                                freqObj.put("nextShowOn", storedFreq.getString("nextShowOn"));
                            }
                            else{
                                return;
                            }
                        }else {
                            Log.d("sfinal-frequency", storedFreq.toString());
                            break;
                        }
                    }
                } else { //key doesn't exists
                    Log.d("frequency", "alium preference does not have your key ");
                    freqObj.put("counter", 1);
                }
                Log.d("final-frequency", freqObj.toString());
                addToAliumPreferences(key, freqObj.toString());
                break
                        ;
            }

            case "y":
                Log.d("frequency[1]", "frequency[1] is   in range..."+ freqData[1]);
                break;
            default:
                Log.d("frequency[1]", "frequency[1] is not in range..."+ freqData[1]);
                break;

        }
    }catch (Exception e){
        Log.e("handlePeriodicFrq", e.toString());
    }
    }

    public boolean shouldSurveyLoad(String key, String srvshowfrq) throws JSONException, ParseException {
        String freqDetailString=aliumSharedPreferences.getString(key,"");
        Log.d("showFreq", "outside frequency comparison"+ freqDetailString);
        JSONObject freqDetailJsonObject=new JSONObject();
        if(!freqDetailString.isEmpty()&&(srvshowfrq.matches("\\d+")||
                srvshowfrq.matches("\\d+-[dwm]")
                )) {
            try {
                freqDetailJsonObject = new JSONObject(freqDetailString);
            } catch (JSONException e) {
                Log.e("Exception", " removing the key: " + freqDetailString + e.toString());
                editor.remove(key);
                editor.apply();
                return true;
            }
        }else if (!checkForBasicFrequencyUpdate(key, srvshowfrq)) {
                return false;
        }

        if(srvshowfrq.matches("\\d+")) {
            int freq=Integer.parseInt(srvshowfrq);
            Log.d("showFreq", "outside frequency comparison");

            //this only checks if survey has reached its frequency count
                   Log.d("showFreq", "outside frequency comparison"+freqDetailJsonObject);
                   if(freqDetailJsonObject.getInt("showFreq")==freq){
                       if(freqDetailJsonObject.getInt("counter")==freq){
                           Log.d("showFreq", "compared and equal");
                           return false;
                       }
                   }

//            }
            Log.d("showFreq", "after frequency comparison");

        }
        else if(srvshowfrq.matches("\\d+-[dwm]")) {
            //for periodic freq
            String[] periodicFreq=srvshowfrq.split("-");
            int freqCount=Integer.parseInt(periodicFreq[0]);

                   if(freqDetailJsonObject.has("showFreq")){
                       if(freqDetailJsonObject.getString("showFreq").equals(srvshowfrq)){

                           if(freqDetailJsonObject.has("counter")){
                               if( freqCount==freqDetailJsonObject.getInt("counter")){
                                   Date today= Calendar.getInstance().getTime();
                                   SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                                   String date=format.format(today);
                                   if(freqDetailJsonObject.getString("lastShownOn").equals(date)){
                                       return false;
                                   }
                                   if(periodicFreq[1].equals("m")||periodicFreq[1].equals("w")){
                                       if(
                                               today.compareTo(format.parse(freqDetailJsonObject.getString("nextShowOn")))
                                                       <0
                                       ){
                                           return false;
                                       }
                                   }

                               }
                           }
                       }
                   }

        }

        return true;
    }
}
