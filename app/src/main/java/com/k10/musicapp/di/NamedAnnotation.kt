package com.k10.musicapp.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LastPlayedSong

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Theme