package com.ntduc.flappybird.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ntduc.contextutils.inflater
import com.ntduc.flappybird.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        initEvent()
    }

    private fun initEvent() {
        binding.play.setOnClickListener {
            startActivity(Intent(this, ChooseLevelActivity::class.java))
        }

        binding.rank.setOnClickListener {

        }
    }
}