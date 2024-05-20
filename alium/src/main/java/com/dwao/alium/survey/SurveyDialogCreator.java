package com.dwao.alium.survey;

import org.json.JSONException;

public abstract class SurveyDialogCreator {
     abstract void generateQuestion(String responseType) throws JSONException;
}
