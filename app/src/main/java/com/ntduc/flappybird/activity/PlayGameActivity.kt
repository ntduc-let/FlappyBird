package com.ntduc.flappybird.activity

import android.content.SharedPreferences
import android.graphics.*
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
import com.ntduc.flappybird.App
import com.ntduc.flappybird.R
import com.ntduc.flappybird.databinding.ActivityPlayGameBinding
import com.ntduc.flappybird.model.*
import com.ntduc.sharedpreferenceutils.get
import com.ntduc.sharedpreferenceutils.put
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

                clearBackGroundGame(canvas)

                when (gameState) {
                    STATE_GAME_NOT_STARTED -> {
                        drawCloud(canvas)
                        drawBird(canvas)

                        showLevel()
                        hideScoreBoard()
                        hideScore()
                        hidePaused()
                    }
                    STATE_GAME_PLAYING -> {
                        updateCloud(canvas)
                        updateTube(canvas)
                        updateCoin(canvas)

                        updateBird(canvas)

                        showLevel()
                        showScore()
                        hidePaused()

                        if (isBirdHitCoin()) {
                            updateScoreCoin()
                        }

                        if (isBirdHitTube()) {
                            startMediaHit()

                            gameState = STATE_GAME_OVER
                        } else {
                            updateScoreTube()
                        }
                    }
                    STATE_GAME_PAUSED -> {
                        drawCloud(canvas)
                        drawTube(canvas)
                        drawCoin(canvas)

                        drawBird(canvas)

                        showPaused()
                    }
                    STATE_GAME_OVER -> {
                        drawCloud(canvas)
                        drawTube(canvas)
                        drawCoin(canvas)

                        drawBird(canvas)

                        hideLevel()
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

    private suspend fun hideLevel() {
        withContext(Dispatchers.Main) {
            binding.lever.visibility = View.GONE
        }
    }

    private suspend fun showLevel() {
        withContext(Dispatchers.Main) {
            binding.lever.visibility = View.VISIBLE
            when (level) {
                LEVEL_EASY -> binding.lever.text = "Easy"
                LEVEL_MEDIUM -> binding.lever.text = "Medium"
                LEVEL_HARD -> binding.lever.text = "Hard"
                LEVEL_VERY_HARD -> binding.lever.text = "Very Hard"
            }
        }
    }

    private suspend fun hidePaused() {
        withContext(Dispatchers.Main) {
            binding.paused.root.visibility = View.GONE
        }
    }

    private suspend fun showPaused() {
        withContext(Dispatchers.Main) {
            binding.paused.root.visibility = View.VISIBLE
        }
    }

    private suspend fun hideScore() {
        withContext(Dispatchers.Main) {
            binding.score.visibility = View.GONE
        }
    }

    private suspend fun showScore() {
        withContext(Dispatchers.Main) {
            binding.score.visibility = View.VISIBLE
        }
    }

    private suspend fun hideScoreBoard() {
        withContext(Dispatchers.Main) {
            binding.surfaceView.setZOrderOnTop(true)

            binding.scoreboard.root.visibility = View.GONE
        }
    }

    private suspend fun showScoreBoard() {
        withContext(Dispatchers.Main) {
            binding.surfaceView.setZOrderOnTop(false)

            binding.scoreboard.score.text = "${score!!.score}"
            binding.scoreboard.root.visibility = View.VISIBLE

            var best = when (level) {
                LEVEL_EASY -> sharedPreferences.get(BEST_SCORE_EASY, 0)
                LEVEL_MEDIUM -> sharedPreferences.get(BEST_SCORE_MEDIUM, 0)
                LEVEL_HARD -> sharedPreferences.get(BEST_SCORE_HARD, 0)
                LEVEL_VERY_HARD -> sharedPreferences.get(BEST_SCORE_VERY_HARD, 0)
                else -> 0
            }
            if (best < score!!.score) {
                best = score!!.score
                when (level) {
                    LEVEL_EASY -> sharedPreferences.put(BEST_SCORE_EASY, best)
                    LEVEL_MEDIUM -> sharedPreferences.put(BEST_SCORE_MEDIUM, best)
                    LEVEL_HARD -> sharedPreferences.put(BEST_SCORE_HARD, best)
                    LEVEL_VERY_HARD -> sharedPreferences.put(BEST_SCORE_VERY_HARD, best)
                }
            }

            binding.scoreboard.best.text = when (level) {
                LEVEL_EASY -> "${sharedPreferences.get(BEST_SCORE_EASY, 0)}"
                LEVEL_MEDIUM -> "${sharedPreferences.get(BEST_SCORE_MEDIUM, 0)}"
                LEVEL_HARD -> "${sharedPreferences.get(BEST_SCORE_HARD, 0)}"
                LEVEL_VERY_HARD -> "${sharedPreferences.get(BEST_SCORE_VERY_HARD, 0)}"
                else -> "0"
            }
        }
    }

    private fun isBirdHitTube(): Boolean {
        for (i in 0 until numberOfTubes) {
            if (bird!!.birdX.toInt() + mBirds[0].width >= tube!!.tubeX[i]
                && (bird!!.birdY.toInt() <= tube!!.topTubeY[i] || bird!!.birdY.toInt() + mBirds[0].height >= tube!!.topTubeY[i] + tube!!.gap)
                && bird!!.birdX.toInt() <= tube!!.tubeX[i] + mTopTube!!.width
            ) return true
        }
        return false
    }

    private fun isBirdHitCoin(): Boolean {
        for (i in 0 until numberOfTubes) {
            if (bird!!.birdX.toInt() + mBirds[0].width >= coin!!.coinX[i]
                && (bird!!.birdY.toInt() + mBirds[0].height >= coin!!.coinY[i] && bird!!.birdY.toInt() <= coin!!.coinY[i] + mCoin!!.height)
                && bird!!.birdX.toInt() <= coin!!.coinX[i] + mCoin!!.width
            ) {
                coin!!.coinShowing[i] = false
                return true
            }
        }
        return false
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        val action = event?.action

        if (action == MotionEvent.ACTION_DOWN) {

            when (gameState) {
                STATE_GAME_NOT_STARTED -> {
                    startMediaWing()

                    bird!!.velocity = -30
                    gameState = STATE_GAME_PLAYING
                }
                STATE_GAME_PLAYING -> {
                    startMediaWing()

                    bird!!.velocity = -30
                }
                STATE_GAME_PAUSED -> {

                }
                STATE_GAME_OVER -> {

                }
            }
        }
        return true
    }

    private suspend fun updateScoreTube() {
        if (tube!!.tubeX[score!!.scoringTube] < (bird!!.birdX - mTopTube!!.width)) {
            score!!.score += 1
            score!!.scoringTube =
                if (score!!.scoringTube < numberOfTubes - 1) score!!.scoringTube + 1 else 0

            startMediaPoint()

            withContext(Dispatchers.Main) {
                binding.score.text = "${score!!.score}"
            }
        }
    }

    private suspend fun updateScoreCoin() {
        if (score!!.scoringCoin) {
            score!!.score += 5
            score!!.scoringCoin = false

            startMediaPoint()

            withContext(Dispatchers.Main) {
                binding.score.text = "${score!!.score}"
            }
        }
    }

    private suspend fun updateCloud(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            if (cloud!!.cloudX[i] < -mCloud!!.width) {   //Xét tube ra ngoài màn hình
                cloud!!.cloudX[i] = numberOfTubes * mDisplayWidth + random.nextInt(mDisplayWidth)
                cloud!!.cloudY[i] = random.nextInt(mDisplayHeight)
            } else {
                cloud!!.cloudX[i] -= cloud!!.cloudVelocity
            }
        }
        drawCloud(canvas)
    }

    private suspend fun updateCoin(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            if (coin!!.coinX[i] < -mCoin!!.width) {   //Xét coin ra ngoài màn hình
                score!!.scoringCoin = true
                coin!!.coinShowing[i] = true
                coin!!.coinX[i] =
                    tube!!.tubeX[i] + tube!!.distanceBetweenTubes / 2 + random.nextInt(tube!!.distanceBetweenTubes - tube!!.distanceBetweenTubes / 2 - (2 * mCoin!!.width) + 1)
                coin!!.coinY[i] =
                    tube!!.minTubeOffset + random.nextInt(tube!!.maxTubeOffset - tube!!.minTubeOffset + 1)
            } else {
                coin!!.coinX[i] -= tube!!.tubeVelocity
            }
        }
        drawCoin(canvas)
    }


    private suspend fun updateTube(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            if (tube!!.tubeX[i] < -mTopTube!!.width) {   //Xét tube ra ngoài màn hình
                tube!!.tubeX[i] += numberOfTubes * tube!!.distanceBetweenTubes
                tube!!.topTubeY[i] =
                    tube!!.minTubeOffset + random.nextInt(tube!!.maxTubeOffset - tube!!.minTubeOffset + 1)

                if (level == LEVEL_VERY_HARD) {
                    tube!!.tubeMove[i] = tube!!.tubeMoveDefault
                }
            } else {
                tube!!.tubeX[i] -= tube!!.tubeVelocity
                if (level == LEVEL_VERY_HARD) {
                    tube!!.tubeMove[i] =
                        if ((tube!!.topTubeY[i] + tube!!.tubeMove[i] <= tube!!.minTubeOffset)
                            || (tube!!.topTubeY[i] + tube!!.tubeMove[i] >= tube!!.maxTubeOffset)
                        )
                            0 - tube!!.tubeMove[i]
                        else tube!!.tubeMove[i]
                    tube!!.topTubeY[i] += tube!!.tubeMove[i]
                }
            }
        }
        drawTube(canvas)
    }

    private suspend fun updateBird(canvas: Canvas) {
        if (bird!!.birdY < mDisplayHeight - mBirds[0].height || bird!!.velocity < 0) {     //Xét bird không rơi khỏi màn hình
            //Xét bird đang rơi, càng rơi càng nhanh
            bird!!.velocity += bird!!.gravity

            bird!!.birdY =
                if (bird!!.birdY + bird!!.velocity < 0) 0f else bird!!.birdY + bird!!.velocity
        } else {
            startMediaHit()

            gameState = STATE_GAME_OVER
        }
        updateDatabaseBird()
        drawBird(canvas)
    }

    private suspend fun drawCloud(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            withContext(Dispatchers.Main) {
                canvas.drawBitmap(
                    mCloud!!,
                    cloud!!.cloudX[i].toFloat(),
                    cloud!!.cloudY[i].toFloat(),
                    null
                )
            }
        }
    }

    private suspend fun drawCoin(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            if (coin!!.coinShowing[i]) {
                withContext(Dispatchers.Main) {
                    canvas.drawBitmap(
                        mCoin!!,
                        coin!!.coinX[i].toFloat(),
                        coin!!.coinY[i].toFloat(),
                        null
                    )
                }
            }
        }
    }

    private suspend fun drawTube(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            withContext(Dispatchers.Main) {
                canvas.drawBitmap(
                    mTopTube!!,
                    tube!!.tubeX[i].toFloat(),
                    (tube!!.topTubeY[i] - mTopTube!!.height).toFloat(),
                    null
                )
                canvas.drawBitmap(
                    mBottomTube!!,
                    tube!!.tubeX[i].toFloat(),
                    (tube!!.topTubeY[i] + tube!!.gap).toFloat(),
                    null
                )
            }
        }
    }

    private val delay = 100L
    private var current = System.currentTimeMillis()
    private suspend fun drawBird(canvas: Canvas) {
        if (System.currentTimeMillis() - current >= delay) {
            current = System.currentTimeMillis()
            birdFrame = if (birdFrame == 0) 1 else 0
        }
        withContext(Dispatchers.Main) {
            canvas.drawBitmap(mBirds[birdFrame], bird!!.birdX, bird!!.birdY, null)
        }
    }

    private suspend fun clearBackGroundGame(canvas: Canvas) {
        val clearPaint = Paint()
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        withContext(Dispatchers.Main) {
            canvas.drawRect(
                0f,
                0f,
                mDisplayWidth.toFloat(),
                mDisplayHeight.toFloat(),
                clearPaint
            )
        }
    }

    private fun startMediaPoint() {
        createMediaPoint()
        mediaPoint?.start()
    }

    private fun createMediaPoint() {
        mediaPoint?.reset()
        mediaPoint = MediaPlayer.create(this, R.raw.point)
        mediaPoint?.setVolume(volume / 100, volume / 100)
    }

    private fun startMediaHit() {
        createMediaHit()
        mediaHit?.start()
    }

    private fun createMediaHit() {
        mediaHit?.reset()
        mediaHit = MediaPlayer.create(this, R.raw.hit)
        mediaHit?.setVolume(volume / 100, volume / 100)
    }

    private fun startMediaWing() {
        createMediaWing()
        mediaWing?.start()
    }

    private fun createMediaWing() {
        mediaWing?.reset()
        mediaWing = MediaPlayer.create(this, R.raw.wing)
        mediaWing?.setVolume(volume / 100, volume / 100)
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
        level = intent.getIntExtra(LEVEL_GAME, LEVEL_EASY)
        bird = intent.getParcelableExtra(TYPE_BIRD)
        tube = Tube(1, R.drawable.toptube, R.drawable.bottomtube)
        cloud = Cloud(1, R.drawable.cloud)
        coin = Coin(1, R.drawable.coin)
        score = Score()
        sharedPreferences = getSharedPreferences("SCORE_GAME", MODE_PRIVATE)

        createDataGame()
    }

    private fun init() {
        initData()
        initView()
        initEvent()
    }

    private fun setupSurfaceHolder() {
        binding.surfaceView.setZOrderOnTop(true)

        surfaceHolder = binding.surfaceView.holder
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT)
        surfaceHolder.addCallback(this)
    }

    private fun resetData() {
        gameState = STATE_GAME_NOT_STARTED

        score!!.score = 0
        binding.score.text = "${score!!.score}"
        score!!.scoringTube = 0
        score!!.scoringCoin = true

        bird!!.birdX = (mDisplayWidth - mBirds[0].width).toFloat() / 2
        bird!!.birdY = (mDisplayHeight - mBirds[0].height).toFloat() / 2

        updateDatabaseBird()
        updateDataLevel(level)

        tube!!.tubeX.clear()
        tube!!.topTubeY.clear()
        for (i in 0 until numberOfTubes) {
            tube!!.tubeX.add(mDisplayWidth + tube!!.distanceBetweenTubes * i)
            tube!!.topTubeY.add(tube!!.minTubeOffset + random.nextInt(tube!!.maxTubeOffset - tube!!.minTubeOffset + 1))

            if (level == LEVEL_VERY_HARD) {
                tube!!.tubeMove.add(tube!!.tubeMoveDefault)
            }
        }

        coin!!.coinX.clear()
        coin!!.coinY.clear()
        coin!!.coinShowing.clear()
        for (i in 0 until numberOfTubes) {
            coin!!.coinX.add(tube!!.tubeX[i] + tube!!.distanceBetweenTubes / 2 + random.nextInt(tube!!.distanceBetweenTubes - tube!!.distanceBetweenTubes / 2 - (2 * mCoin!!.width) + 1))
            coin!!.coinY.add(tube!!.minTubeOffset + random.nextInt(tube!!.maxTubeOffset - tube!!.minTubeOffset + 1))
            coin!!.coinShowing.add(true)
        }

        cloud!!.cloudX.clear()
        cloud!!.cloudY.clear()
        for (i in 0 until numberOfTubes) {
            cloud!!.cloudX.add(mDisplayWidth * (i + 1) + random.nextInt(mDisplayWidth))
            cloud!!.cloudY.add(random.nextInt(mDisplayHeight))
        }
    }

    private fun updateDataLevel(level: Int) {
        when (level) {
            LEVEL_EASY -> {
                tube!!.gap = GAP_EASY
                tube!!.distanceBetweenTubes = mDisplayWidth * 3 / 4 + DISTANCE_EASY
            }
            LEVEL_MEDIUM -> {
                tube!!.gap = GAP_MEDIUM
                tube!!.distanceBetweenTubes = mDisplayWidth * 3 / 4 + DISTANCE_MEDIUM
            }
            LEVEL_HARD, LEVEL_VERY_HARD -> {
                tube!!.gap = GAP_HARD
                tube!!.distanceBetweenTubes = mDisplayWidth * 3 / 4 + DISTANCE_HARD
            }
        }
        tube!!.minTubeOffset = tube!!.gap / 2
        tube!!.maxTubeOffset = mDisplayHeight - tube!!.minTubeOffset - tube!!.gap
    }

    private fun createDataGame() {
        mCloud = BitmapFactory.decodeResource(resources, cloud!!.cloudRes)
        mTopTube = BitmapFactory.decodeResource(resources, tube!!.tubeTopRes)
        mBottomTube = BitmapFactory.decodeResource(resources, tube!!.tubeBottomRes)
        mCoin = BitmapFactory.decodeResource(resources, coin!!.coinRes)
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val heightNavigationBar = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0

        mDisplayWidth = displayWidth
        mDisplayHeight = displayHeight + heightNavigationBar
        mRect = Rect(0, 0, mDisplayWidth, mDisplayHeight)

        mBirds = listOf(
            BitmapFactory.decodeResource(resources, bird!!.bird1Res),
            BitmapFactory.decodeResource(resources, bird!!.bird2Res)
        )

        tube!!.distanceBetweenTubes = mDisplayWidth * 3 / 4
        tube!!.maxTubeOffset = mDisplayHeight - tube!!.minTubeOffset - tube!!.gap

        resetData()
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        runGame()

    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {

    }

    private fun updateDatabaseBird() {
        App.getDatabase().getReference("bird").setValue(bird)
    }

    companion object {
        private const val STATE_GAME_NOT_STARTED = 0
        private const val STATE_GAME_PLAYING = 1
        private const val STATE_GAME_PAUSED = 2
        private const val STATE_GAME_OVER = 3

        const val LEVEL_GAME = "LEVEL_GAME"
        const val TYPE_BIRD = "TYPE_BIRD"

        const val LEVEL_EASY = 0
        const val LEVEL_MEDIUM = 1
        const val LEVEL_HARD = 2
        const val LEVEL_VERY_HARD = 3

        private const val GAP_EASY = 600
        private const val GAP_MEDIUM = 500
        private const val GAP_HARD = 400

        private const val DISTANCE_EASY = 300
        private const val DISTANCE_MEDIUM = 100
        private const val DISTANCE_HARD = 0

        private const val BEST_SCORE_EASY = "BEST_SCORE_EASY"
        private const val BEST_SCORE_MEDIUM = "BEST_SCORE_MEDIUM"
        private const val BEST_SCORE_HARD = "BEST_SCORE_HARD"
        private const val BEST_SCORE_VERY_HARD = "BEST_SCORE_VERY_HARD"
    }

    private lateinit var binding: ActivityPlayGameBinding
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var sharedPreferences: SharedPreferences
    private var level: Int = LEVEL_EASY
    private var gameState: Int = STATE_GAME_NOT_STARTED

    private var mRect: Rect? = null
    private var mDisplayWidth: Int = 0              //Width screen
    private var mDisplayHeight: Int = 0             //Height screen

    private var mediaHit: MediaPlayer? = null
    private var mediaPoint: MediaPlayer? = null
    private var mediaWing: MediaPlayer? = null
    private val volume =
        (App.getInstance().getVolumeEffect() * App.getInstance().getVolumeMaster()).toFloat() / 100

    private var random: Random = Random()

    private var bird: Bird? = null
    private var mBirds: List<Bitmap> = listOf()     //List bitmap for bird
    private var birdFrame: Int = 0                  //Theo dõi trạng thái của bird

    private var tube: Tube? = null
    private var mTopTube: Bitmap? = null
    private var mBottomTube: Bitmap? = null
    private var numberOfTubes: Int = 2

    private var cloud: Cloud? = null
    private var mCloud: Bitmap? = null

    private var coin: Coin? = null
    private var mCoin: Bitmap? = null

    private var score: Score? = null
}