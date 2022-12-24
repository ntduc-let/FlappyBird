package com.ntduc.flappybird

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ntduc.activityutils.enterFullScreenMode
import com.ntduc.contextutils.displayHeight
import com.ntduc.contextutils.displayWidth
import com.ntduc.contextutils.inflater
import com.ntduc.flappybird.databinding.ActivityPlayGameBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class PlayGameActivity : AppCompatActivity(), SurfaceHolder.Callback, View.OnTouchListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayGameBinding.inflate(inflater)
        setContentView(binding.root)

        initData()
        initView()
        initEvent()
    }

    override fun onStart() {
        super.onStart()

        when (gameState) {
            STATE_GAME_NOT_STARTED -> {

            }
            STATE_GAME_PLAYING -> {

            }
            STATE_GAME_PAUSED -> {

            }
            STATE_GAME_OVER -> {

            }
        }
    }

    override fun onStop() {
        super.onStop()

        when (gameState) {
            STATE_GAME_NOT_STARTED -> {

            }
            STATE_GAME_PLAYING -> {

            }
            STATE_GAME_PAUSED -> {

            }
            STATE_GAME_OVER -> {

            }
        }
    }

    private fun runGame() {
        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                val canvas: Canvas = withContext(Dispatchers.Main) {
                    surfaceHolder.lockCanvas()
                }

                drawBackGroundGame(canvas)

                when (gameState) {
                    STATE_GAME_NOT_STARTED -> {
                        drawBird(canvas)
                    }
                    STATE_GAME_PLAYING -> {
                        updateBird(canvas)

                        updateTube(canvas)

                        if (isBirdHitTube()) {
                            gameState = STATE_GAME_OVER
                        } else {
                            updateScore()
                        }
                    }
                    STATE_GAME_PAUSED -> {

                    }
                    STATE_GAME_OVER -> {
                        drawBird(canvas)

                        drawTube(canvas)

                        drawGameOver(canvas)
                    }
                }

                withContext(Dispatchers.Main) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private fun isBirdHitTube(): Boolean {
        for (i in 0 until numberOfTubes) {
            if (birdX.toInt() + mBirds[0].width >= tubeX[i]
                && (birdY.toInt() <= topTubeY[i] || birdY.toInt() + mBirds[0].height >= topTubeY[i] + gap)
                && birdX.toInt() <= tubeX[i] + mTopTube!!.width
            ) return true
        }
        return false
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        val action = event?.action

        if (action == MotionEvent.ACTION_DOWN) {
            when (gameState) {
                STATE_GAME_NOT_STARTED -> {
                    velocity = -35
                    gameState = STATE_GAME_PLAYING
                }
                STATE_GAME_PLAYING -> {
                    velocity = -35
                }
                STATE_GAME_PAUSED -> {

                }
                STATE_GAME_OVER -> {

                }
            }
        }
        return true
    }

    private suspend fun updateScore() {
        if (tubeX[scoringTube!!] < (birdX - mTopTube!!.width)) {
            score += 1
            scoringTube =
                if (scoringTube!! < numberOfTubes - 1) scoringTube!! + 1 else 0

            withContext(Dispatchers.Main) {
                binding.score.text = "$score"
            }
        }
    }

    private suspend fun updateTube(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            if (tubeX[i] < -mTopTube!!.width) {   //Xét tube ra ngoài màn hình
                tubeX[i] += numberOfTubes * distanceBetweenTubes
                topTubeY[i] =
                    minTubeOffset + random.nextInt(maxTubeOffset!! - minTubeOffset + 1)
            } else {
                tubeX[i] -= tubeVelocity
            }
        }
        drawTube(canvas)
    }

    private suspend fun updateBird(canvas: Canvas) {
        if (birdY < mDisplayHeight - mBirds[0].height || velocity < 0) {     //Xét bird không rơi khỏi màn hình
            //Xét bird đang rơi, càng rơi càng nhanh
            velocity += gravity
            birdY += velocity
        } else {
            gameState = STATE_GAME_OVER
        }
        drawBird(canvas)
    }

    private suspend fun drawTube(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            withContext(Dispatchers.Main) {
                canvas.drawBitmap(
                    mTopTube!!,
                    tubeX[i].toFloat(),
                    (topTubeY[i] - mTopTube!!.height).toFloat(),
                    null
                )
                canvas.drawBitmap(
                    mBottomTube!!,
                    tubeX[i].toFloat(),
                    (topTubeY[i] + gap).toFloat(),
                    null
                )
            }
        }
    }

    private suspend fun drawBird(canvas: Canvas) {
        birdFrame = if (birdFrame == 0) 1 else 0
        withContext(Dispatchers.Main) {
            canvas.drawBitmap(mBirds[birdFrame], birdX, birdY, null)
        }
    }

    private suspend fun drawGameOver(canvas: Canvas) {
        withContext(Dispatchers.Main) {
            canvas.drawBitmap(
                mGameOver!!,
                (mDisplayWidth - mGameOver!!.width).toFloat() / 2,
                (mDisplayHeight - mGameOver!!.height).toFloat() * 1 / 4,
                null
            )
        }
    }

    private suspend fun drawBackGroundGame(canvas: Canvas) {
        withContext(Dispatchers.Main) {
            canvas.drawBitmap(mBackground!!, null, mRect!!, null)
        }
    }

    private fun initEvent() {
        binding.surfaceView.setOnTouchListener(this)
    }

    private fun initView() {
        enterFullScreenMode()

        setupSurfaceHolder()
    }

    private fun initData() {
        createDataGame()
    }

    private fun setupSurfaceHolder() {
        surfaceHolder = binding.surfaceView.holder
        surfaceHolder.addCallback(this)
    }

    private fun createDataGame() {
        mBackground = BitmapFactory.decodeResource(resources, R.drawable.bg)
        mGameOver = BitmapFactory.decodeResource(resources, R.drawable.gameover)
        mTopTube = BitmapFactory.decodeResource(resources, R.drawable.toptube)
        mBottomTube = BitmapFactory.decodeResource(resources, R.drawable.bottomtube)
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val heightNavigationBar = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0

        mDisplayWidth = displayWidth
        mDisplayHeight = displayHeight + heightNavigationBar
        mRect = Rect(0, 0, mDisplayWidth, mDisplayHeight)

        mBirds = listOf(
            BitmapFactory.decodeResource(resources, R.drawable.bird),
            BitmapFactory.decodeResource(resources, R.drawable.bird2)
        )

        birdX = (mDisplayWidth - mBirds[0].width).toFloat() / 2
        birdY = (mDisplayHeight - mBirds[0].height).toFloat() / 2

        distanceBetweenTubes = mDisplayWidth * 3 / 4
        maxTubeOffset = mDisplayHeight - minTubeOffset - gap

        for (i in 0 until numberOfTubes) {
            tubeX.add(mDisplayWidth + distanceBetweenTubes * i)
            topTubeY.add(minTubeOffset + random.nextInt(maxTubeOffset!! - minTubeOffset + 1))
        }
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        runGame()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder) {}

    companion object {
        private const val STATE_GAME_NOT_STARTED = 0
        private const val STATE_GAME_PLAYING = 1
        private const val STATE_GAME_PAUSED = 2
        private const val STATE_GAME_OVER = 3
    }

    private lateinit var binding: ActivityPlayGameBinding
    private lateinit var surfaceHolder: SurfaceHolder

    private var mBackground: Bitmap? = null
    private var mGameOver: Bitmap? = null
    private var mTopTube: Bitmap? = null
    private var mBottomTube: Bitmap? = null
    private var mDisplayWidth: Int = 0              //Width screen
    private var mDisplayHeight: Int = 0             //Height screen
    private var mRect: Rect? = null

    private var mBirds: List<Bitmap> = listOf()     //List bitmap for bird
    private var birdFrame: Int = 0                  //Theo dõi trạng thái của bird
    private var velocity: Int = 0                   //Vận tốc rơi
    private var gravity: Int = 3

    private var birdX: Float = 0f
    private var birdY: Float = 0f

    private var gameState: Int = STATE_GAME_NOT_STARTED

    private var gap: Int = 400                      //Khoảng cách giữa tube trên và dưới
    private var distanceBetweenTubes: Int = 0       //Khoảng cách giữa các tube
    private var numberOfTubes: Int = 4
    private var minTubeOffset: Int = gap / 2
    private var maxTubeOffset: Int? = null
    private var tubeX: ArrayList<Int> = arrayListOf()
    private var topTubeY: ArrayList<Int> = arrayListOf()
    private var random: Random = Random()
    private var tubeVelocity: Int = 8               //Vận tốc tube

    private var score: Int = 0
    private var scoringTube: Int? = 0
}