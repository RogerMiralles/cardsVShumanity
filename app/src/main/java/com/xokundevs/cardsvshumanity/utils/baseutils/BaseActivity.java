package com.xokundevs.cardsvshumanity.utils.baseutils;

import androidx.appcompat.app.AppCompatActivity;

import com.xokundevs.cardsvshumanity.utils.LanguageManager;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        LanguageManager.loadLanguage(this);
    }
}
