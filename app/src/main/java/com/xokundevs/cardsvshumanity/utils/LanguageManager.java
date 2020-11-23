package com.xokundevs.cardsvshumanity.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Objects;

public class LanguageManager {
    private static String language;

    public static void setLanguage(Context context,@NonNull String language){
        LanguageManager.language = language;
        Configuration config = Objects.requireNonNull(context.getResources().getConfiguration());
        config.locale = new Locale(language);
        context.getResources().updateConfiguration(
                config,
                context.getResources().getDisplayMetrics()
        );
        Locale.setDefault(new Locale(language));

        SharedPreferences.Editor editor= context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("My_Lang",language);
        editor.apply();
    }

    public static void loadLanguage(Context context){
        if(language == null){
            SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
            language = sharedPreferences.getString("My_Lang", null);
            if(language == null){
                setLanguage(context, "es");
            }
        }
        Locale.setDefault(new Locale(language));
    }

    public static String getLanguage(){
        return language;
    }
}
