package com.example.myandroidappenhanced.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    // All utility classes use @Inject constructor, so no explicit bindings needed
    // SmartTaskParser, ExportImportUtils, and ReminderManager are @Singleton with @Inject
}
