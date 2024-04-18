package com.dwao.alium.utilities;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import  java.lang.Class;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class JSONConverter {
    public static <T> T mapToObject(Class<T> classObject, String json){
        Gson gson=new Gson();
        Type type= TypeToken.getParameterized(classObject).getType();
        return  gson.fromJson(json, type);
    }
    public static <T> List<T> mapToListObject(Class<T> classObj, String json)
            {
       try{
           Gson gson=new Gson();
//           Object object= classObj.getDeclaredConstructor().newInstance();
           Type type=new ParameterizedType() {
               @NonNull
               @Override
               public Type[] getActualTypeArguments() {
                   return new Type[] {
                           classObj
                   };
               }

               @NonNull
               @Override
               public Type getRawType() {
                   return List.class;
               }

               @Nullable
               @Override
               public Type getOwnerType() {
                   return null;
               }
           };
           return  gson.fromJson(json, type);
       }catch (Exception e){
           Log.e("JSONFromList", e.toString());
           throw e;

       }
    }
}
