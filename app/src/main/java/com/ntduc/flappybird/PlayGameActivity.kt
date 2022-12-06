package com.ntduc.flappybird

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ntduc.activityutils.enterFullScreenMode
import com.ntduc.activityutils.getStatusBarHeight

class PlayGameActivity : AppCompatActivity() {
    private lateinit var playGameView: PlayGameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playGameView = PlayGameView(this)
        setContentView(playGameView)
        getStatusBarHeight
        enterFullScreenMode()
    }
}