package com.ntduc.flappybird

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ntduc.contextutils.inflater
import com.ntduc.flappybird.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        initData()
        initEvent()
    }

    private fun initData() {
        mediaPlayer = MediaPlayer.create(this, R.raw.background)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    private fun initEvent() {
        binding.play.setOnClickListener {
            startActivity(Intent(this, PlayGameActivity::class.java))
        }
    }
}