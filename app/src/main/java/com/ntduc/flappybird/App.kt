package com.ntduc.flappybird

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ntduc.sharedpreferenceutils.get
import com.ntduc.sharedpreferenceutils.put

class App : Application(), Application.ActivityLifecycleCallbacks {
    private lateinit var mediaPlayerMusic: MediaPlayer
    private lateinit var mediaPlayerEffect: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        instance = this
        firebaseDatabase = Firebase.database

        initData()

        registerActivityLifecycleCallbacks(this)
    }

    private fun initData() {
        sharedPreferences = getSharedPreferences("SOUND", MODE_PRIVATE)

        mediaPlayerMusic = MediaPlayer.create(this, R.raw.background)
        mediaPlayerMusic.isLooping = true
        mediaPlayerMusic.start()

        mediaPlayerEffect = MediaPlayer.create(this, R.raw.swoosh)

        setVolumeMaster(getVolumeMaster())
    }

    fun startEffect(){
        mediaPlayerEffect.reset()
        mediaPlayerEffect = MediaPlayer.create(this, R.raw.swoosh)
        setVolumeMaster(getVolumeMaster())
        mediaPlayerEffect.start()
    }

    fun setVolumeMaster(volume: Int) {
        val resultMusic = (volume * getVolumeMusic()).toFloat() / 100
        val resultEffect = (volume * getVolumeEffect()).toFloat() / 100

        mediaPlayerMusic.setVolume(resultMusic / 100, resultMusic / 100)
        mediaPlayerEffect.setVolume(resultEffect / 100, resultEffect / 100)
        sharedPreferences.put(VOLUME_MASTER, volume)
    }

    fun getVolumeMaster(): Int {
        return sharedPreferences.get(VOLUME_MASTER, 100)
    }

    fun setVolumeMusic(volume: Int) {
        val result = (volume * getVolumeMaster()).toFloat() / 100
        mediaPlayerMusic.setVolume(result / 100, result / 100)
        sharedPreferences.put(VOLUME_MUSIC, volume)
    }

    fun getVolumeMusic(): Int {
        return sharedPreferences.get(VOLUME_MUSIC, 100)
    }

    fun setVolumeEffect(volume: Int) {
        val result = (volume * getVolumeMaster()).toFloat() / 100
        mediaPlayerEffect.setVolume(result / 100, result / 100)
        sharedPreferences.put(VOLUME_EFFECT, volume)
    }

    fun getVolumeEffect(): Int {
        return sharedPreferences.get(VOLUME_EFFECT, 100)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        mediaPlayerMusic.start()
    }

    override fun onActivityPaused(activity: Activity) {
        if (mediaPlayerMusic.isPlaying) {
            mediaPlayerMusic.pause()
        }
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    companion object {
        const val VOLUME_MASTER = "VOLUME_MASTER"
        const val VOLUME_MUSIC = "VOLUME_MUSIC"
        const val VOLUME_EFFECT = "VOLUME_EFFECT"

        private var instance: App? = null
        private var firebaseDatabase: FirebaseDatabase? = null

        fun getInstance(): App {
            return instance!!
        }

        fun getDatabase(): FirebaseDatabase {
            return firebaseDatabase!!
        }
    }
}