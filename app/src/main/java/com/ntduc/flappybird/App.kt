package com.ntduc.flappybird

import android.app.Activity
import android.app.Application
import android.media.MediaPlayer
import android.os.Bundle

class App : Application(), Application.ActivityLifecycleCallbacks {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()

        initData()

        registerActivityLifecycleCallbacks(this)
    }

    private fun initData() {
        mediaPlayer = MediaPlayer.create(this, R.raw.background)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        mediaPlayer.start()
    }

    override fun onActivityPaused(activity: Activity) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}
}