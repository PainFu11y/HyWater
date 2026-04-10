package com.example.hywater

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point.
 * @HiltAndroidApp triggers Hilt's code generation and creates the application-level component.
 */
@HiltAndroidApp
class HyWaterApp : Application()
