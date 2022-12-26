package com.ntduc.flappybird.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ntduc.contextutils.inflater
import com.ntduc.flappybird.App
import com.ntduc.flappybird.databinding.ActivityChooseLevelBinding

class ChooseLevelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLevelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLevelBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        initEvent()
    }

    private fun initEvent() {
        binding.easy.setOnClickListener {
            App.getInstance().startEffect()
            startGame(PlayGameActivity.LEVEL_EASY)
            finish()
        }

        binding.medium.setOnClickListener {
            App.getInstance().startEffect()
            startGame(PlayGameActivity.LEVEL_MEDIUM)
            finish()
        }

        binding.hard.setOnClickListener {
            App.getInstance().startEffect()
            startGame(PlayGameActivity.LEVEL_HARD)
            finish()
        }
    }

    private fun startGame(level: Int){
        val intent = Intent(this, PlayGameActivity::class.java)
        intent.putExtra(PlayGameActivity.LEVEL_GAME, level)
        startActivity(intent)
    }

    override fun onBackPressed() {
        App.getInstance().startEffect()
        super.onBackPressed()
    }
}