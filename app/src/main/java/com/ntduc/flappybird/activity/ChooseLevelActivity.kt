package com.ntduc.flappybird.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.ntduc.contextutils.inflater
import com.ntduc.flappybird.App
import com.ntduc.flappybird.R
import com.ntduc.flappybird.adapter.SliderBirdsAdapter
import com.ntduc.flappybird.databinding.ActivityChooseLevelBinding
import com.ntduc.flappybird.model.Bird
import kotlin.math.abs

class ChooseLevelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLevelBinding
    private val listBird = arrayListOf<Bird>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLevelBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initData()
        initView()
        initEvent()
    }

    private fun initView() {
        binding.vpBirds.clipToPadding = false
        binding.vpBirds.clipChildren = false
        binding.vpBirds.offscreenPageLimit = 3
        binding.vpBirds.currentItem = 1

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        binding.vpBirds.setPageTransformer(compositePageTransformer)
    }

    private fun initData() {
        initListBird()

        binding.vpBirds.adapter = SliderBirdsAdapter(this, listBird)
    }

    private fun initListBird() {
        listBird.add(Bird(style = 1, bird1 = R.drawable.bird_1a, bird2 = R.drawable.bird_1b))
        listBird.add(Bird(style = 2, bird1 = R.drawable.bird_2a, bird2 = R.drawable.bird_2b))
        listBird.add(Bird(style = 3, bird1 = R.drawable.bird_3a, bird2 = R.drawable.bird_3b))
        listBird.add(Bird(style = 4, bird1 = R.drawable.bird_4a, bird2 = R.drawable.bird_4b))
        listBird.add(Bird(style = 5, bird1 = R.drawable.bird_5a, bird2 = R.drawable.bird_5b))
        listBird.add(Bird(style = 6, bird1 = R.drawable.bird_6a, bird2 = R.drawable.bird_6b))
        listBird.add(Bird(style = 7, bird1 = R.drawable.bird_7a, bird2 = R.drawable.bird_7b))
        listBird.add(Bird(style = 8, bird1 = R.drawable.bird_8a, bird2 = R.drawable.bird_8b))
        listBird.add(Bird(style = 9, bird1 = R.drawable.bird_9a, bird2 = R.drawable.bird_9b))
        listBird.add(Bird(style = 10, bird1 = R.drawable.bird_10a, bird2 = R.drawable.bird_10b))
        listBird.add(Bird(style = 11, bird1 = R.drawable.bird_11a, bird2 = R.drawable.bird_11b))
        listBird.add(Bird(style = 12, bird1 = R.drawable.bird_12a, bird2 = R.drawable.bird_12b))
    }

    private fun initEvent() {
        val recyclerView = binding.vpBirds.getChildAt(0) as RecyclerView
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val itemCount = binding.vpBirds.adapter?.itemCount ?: 0

        // attach scroll listener
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstItemVisible = layoutManager.findFirstVisibleItemPosition()
                val lastItemVisible = layoutManager.findLastVisibleItemPosition()
                if (firstItemVisible == (itemCount - 1) && dx > 0) {
                    recyclerView.scrollToPosition(1)
                } else if (lastItemVisible == 0 && dx < 0) {
                    recyclerView.scrollToPosition(itemCount - 2)
                }
            }
        })

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

        binding.veryHard.setOnClickListener {
            App.getInstance().startEffect()
            startGame(PlayGameActivity.LEVEL_VERY_HARD)
            finish()
        }
    }

    private fun startGame(level: Int) {
        val intent = Intent(this, PlayGameActivity::class.java)
        intent.putExtra(PlayGameActivity.LEVEL_GAME, level)
        intent.putExtra(PlayGameActivity.TYPE_BIRD, listBird[binding.vpBirds.currentItem - 1])
        startActivity(intent)
    }

    override fun onBackPressed() {
        App.getInstance().startEffect()
        super.onBackPressed()
    }
}