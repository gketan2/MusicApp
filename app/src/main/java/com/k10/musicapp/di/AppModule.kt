package com.k10.musicapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(app: Application): Context {
        return app.applicationContext
    }

    @Theme
    @Provides
    @Singleton
    fun themePreference(context: Context): SharedPreferences {
        return context.getSharedPreferences("theme", Context.MODE_PRIVATE)
    }

    @Theme
    @Provides
    @Singleton
    fun themePreferenceEditor(context: Context): SharedPreferences.Editor {
        return context.getSharedPreferences("theme", Context.MODE_PRIVATE).edit()
    }

    @LastPlayedSong
    @Provides
    @Singleton
    fun lastSongPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences("last_song", Context.MODE_PRIVATE)
    }

    @LastPlayedSong
    @Provides
    @Singleton
    fun lastSongPreferenceEditor(application: Application): SharedPreferences.Editor {
        return application.getSharedPreferences("last_song", Context.MODE_PRIVATE).edit()
    }
}