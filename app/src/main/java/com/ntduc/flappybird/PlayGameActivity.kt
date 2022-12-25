package com.ntduc.flappybird

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Bundle
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

        init()
    }

    override fun onResume() {
        super.onResume()

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

    override fun onPause() {
        super.onPause()

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
                } ?: continue

                drawBackGroundGame(canvas)

                when (gameState) {
                    STATE_GAME_NOT_STARTED -> {
                        drawBird(canvas)
                        hideScoreBoard()
                        hideScore()
                        hidePaused()
                    }
                    STATE_GAME_PLAYING -> {
                        updateTube(canvas)

                        updateBird(canvas)

                        showScore()
                        hidePaused()

                        if (isBirdHitTube()) {
                            startMediaHit()

                            gameState = STATE_GAME_OVER
                        } else {
                            updateScore()
                        }
                    }
                    STATE_GAME_PAUSED -> {
                        drawTube(canvas)

                        drawBird(canvas)

                        showPaused()
                    }
                    STATE_GAME_OVER -> {
                        drawTube(canvas)

                        drawBird(canvas)

                        hideScore()
                        showScoreBoard()
                        hidePaused()
                    }
                }

                withContext(Dispatchers.Main) {
                    if (surfaceHolder.surface.isValid) surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private suspend fun hidePaused() {
        withContext(Dispatchers.Main){
            binding.paused.root.visibility = View.GONE
        }
    }

    private suspend fun showPaused() {
        withContext(Dispatchers.Main){
            binding.paused.root.visibility = View.VISIBLE
        }
    }

    private suspend fun hideScore() {
        withContext(Dispatchers.Main){
            binding.score.visibility = View.GONE
        }
    }

    private suspend fun showScore() {
        withContext(Dispatchers.Main){
            binding.score.visibility = View.VISIBLE
        }
    }

    private suspend fun hideScoreBoard() {
        withContext(Dispatchers.Main){
            binding.scoreboard.root.visibility = View.GONE
        }
    }

    private suspend fun showScoreBoard() {
        withContext(Dispatchers.Main){
            binding.scoreboard.score.text = "$score"
            binding.scoreboard.root.visibility = View.VISIBLE
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
                    startMediaWing()

                    velocity = -30
                    gameState = STATE_GAME_PLAYING
                }
                STATE_GAME_PLAYING -> {
                    startMediaWing()

                    velocity = -30
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

            startMediaPoint()

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

            birdY = if (birdY + velocity < 0) 0f else birdY + velocity
        } else {
            startMediaHit()

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

    private suspend fun drawBackGroundGame(canvas: Canvas) {
        withContext(Dispatchers.Main) {
            canvas.drawBitmap(mBackground!!, null, mRect!!, null)
        }
    }

    private fun startMediaPoint() {
        createMediaPoint()
        mediaPoint?.start()
    }

    private fun createMediaPoint() {
        mediaPoint?.reset()
        mediaPoint = MediaPlayer.create(this, R.raw.point)
    }

    private fun startMediaHit() {
        createMediaHit()
        mediaHit?.start()
    }

    private fun createMediaHit() {
        mediaHit?.reset()
        mediaHit = MediaPlayer.create(this, R.raw.hit)
    }

    private fun startMediaWing() {
        createMediaWing()
        mediaWing?.start()
    }

    private fun createMediaWing() {
        mediaWing?.reset()
        mediaWing = MediaPlayer.create(this, R.raw.wing)
    }

    private fun initEvent() {
        binding.surfaceView.setOnTouchListener(this)
        binding.scoreboard.exit.setOnClickListener {
            finish()
        }
        binding.scoreboard.playAgain.setOnClickListener {
            resetData()
        }
        binding.paused.resume.setOnClickListener {

        }
        binding.paused.exit.setOnClickListener {
            finish()
        }
    }

    private fun initView() {
        enterFullScreenMode()

        setupSurfaceHolder()
    }

    private fun initData() {
        createDataGame()
    }

    private fun init() {
        initData()
        initView()
        initEvent()
    }

    private fun setupSurfaceHolder() {
        surfaceHolder = binding.surfaceView.holder
        surfaceHolder.addCallback(this)
    }

    private fun resetData() {
        gameState = STATE_GAME_NOT_STARTED

        score = 0
        scoringTube = 0

        birdX = (mDisplayWidth - mBirds[0].width).toFloat() / 2
        birdY = (mDisplayHeight - mBirds[0].height).toFloat() / 2

        tubeX.clear()
        topTubeY.clear()
        for (i in 0 until numberOfTubes) {
            tubeX.add(mDisplayWidth + distanceBetweenTubes * i)
            topTubeY.add(minTubeOffset + random.nextInt(maxTubeOffset!! - minTubeOffset + 1))
        }
    }

    private fun createDataGame() {
        mBackground = BitmapFactory.decodeResource(resources, R.drawable.bg)
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

        distanceBetweenTubes = mDisplayWidth * 3 / 4
        maxTubeOffset = mDisplayHeight - minTubeOffset - gap

        resetData()
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        runGame()

    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {

    }

    companion object {
        private const val STATE_GAME_NOT_STARTED = 0
        private const val STATE_GAME_PLAYING = 1
        private const val STATE_GAME_PAUSED = 2
        private const val STATE_GAME_OVER = 3
    }

    private lateinit var binding: ActivityPlayGameBinding
    private lateinit var surfaceHolder: SurfaceHolder

    private var mediaHit: MediaPlayer? = null
    private var mediaPoint: MediaPlayer? = null
    private var mediaWing: MediaPlayer? = null

    private var mBackground: Bitmap? = null
    private var mTopTube: Bitmap? = null
    private var mBottomTube: Bitmap? = null
    private var mDisplayWidth: Int = 0              //Width screen
    private var mDisplayHeight: Int = 0             //Height screen
    private var mRect: Rect? = null

    private var mBirds: List<Bitmap> = listOf()     //List bitmap for bird
    private var birdFrame: Int = 0                  //Theo dõi trạng thái của bird
    private var velocity: Int = 0                   //Vận tốc rơi
    private var gravity: Int = 2

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