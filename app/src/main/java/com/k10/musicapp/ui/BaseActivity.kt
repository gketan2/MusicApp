package com.k10.musicapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.k10.musicapp.R
import com.k10.musicapp.di.Theme
import com.k10.musicapp.utils.Constants.Companion.THEME_DARK
import com.k10.musicapp.utils.Constants.Companion.THEME_LIGHT
import javax.inject.Inject

open class BaseActivity: AppCompatActivity() {

    @Theme
    @Inject
    lateinit var themePreferences: SharedPreferences

    @Theme
    @Inject
    lateinit var themePreferencesEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(getCurrentTheme() == THEME_LIGHT){
            setTheme(R.style.LightTheme)
        } else {
            setTheme(R.style.DarkTheme)
        }
    }

    fun getCurrentTheme(): Int{
        return themePreferences.getInt("current_theme", THEME_LIGHT)
    }

    fun setCurrentTheme(theme: Int){
        if(theme == THEME_LIGHT)
            themePreferencesEditor.putInt("current_theme", THEME_LIGHT).apply()
        else
            themePreferencesEditor.putInt("current_theme", THEME_DARK).apply()
    }
}