package com.ntduc.flappybird

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import com.ntduc.sharedpreferenceutils.get
import com.ntduc.sharedpreferenceutils.put

class App : Application(), Application.ActivityLifecycleCallbacks {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        instance = this

        initData()

        registerActivityLifecycleCallbacks(this)
    }

    private fun initData() {
        sharedPreferences = getSharedPreferences("SOUND", MODE_PRIVATE)

        mediaPlayer = MediaPlayer.create(this, R.raw.background)
        setVolume(sharedPreferences.get(VOLUME_MASTER, 100))
        mediaPlayer.isLooping = true
        mediaPlayer.start()

    }

    fun setVolume(volume: Int) {
        mediaPlayer.setVolume(volume.toFloat() / 100, volume.toFloat() / 100)
        sharedPreferences.put(VOLUME_MASTER, volume)
    }

    fun getVolume(): Int {
        return sharedPreferences.get(VOLUME_MASTER, 100)
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

    companion object {
        const val VOLUME_MASTER = "VOLUME_MASTER"

        private var instance: App? = null

        fun getInstance(): App {
            return instance!!
        }
    }
}