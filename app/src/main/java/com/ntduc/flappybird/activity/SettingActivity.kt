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
        val volumeMaster = App.getInstance().getVolumeMaster()
        binding.master.value = volumeMaster.toFloat()
        binding.numberMaster.text = "$volumeMaster"

        val volumeMusic = App.getInstance().getVolumeMusic()
        binding.music.value = volumeMusic.toFloat()
        binding.numberMusic.text = "$volumeMusic"

        val volumeEffect = App.getInstance().getVolumeEffect()
        binding.effects.value = volumeEffect.toFloat()
        binding.numberEffects.text = "$volumeEffect"
    }

    private fun initEvent() {
        binding.master.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                binding.numberMaster.text = "${value.toInt()}"
                App.getInstance().setVolumeMaster(value.toInt())
            }
        }

        binding.music.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                binding.numberMusic.text = "${value.toInt()}"
                App.getInstance().setVolumeMusic(value.toInt())
            }
        }

        binding.effects.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                binding.numberEffects.text = "${value.toInt()}"
                App.getInstance().setVolumeEffect(value.toInt())
            }
        }
    }

    override fun onBackPressed() {
        App.getInstance().startEffect()
        super.onBackPressed()
    }
}