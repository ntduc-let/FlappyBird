package com.ntduc.flappybird.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ntduc.contextutils.inflater
import com.ntduc.flappybird.App
import com.ntduc.flappybird.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initData()
        initView()
        initEvent()
    }

    private fun initData() {

    }

    private fun initView() {
        val volumeMaster = App.getInstance().getVolume()
        binding.master.value = volumeMaster.toFloat()
        binding.numberMaster.text = "$volumeMaster"
    }

    private fun initEvent() {
        binding.master.addOnChangeListener { _, value, fromUser ->
            if (fromUser){
                binding.numberMaster.text = "${value.toInt()}"
                App.getInstance().setVolume(value.toInt())
            }
        }
    }
}