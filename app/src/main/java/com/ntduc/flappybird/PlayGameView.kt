package com.ntduc.flappybird

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import com.ntduc.contextutils.displayHeight
import com.ntduc.contextutils.displayWidth

public class PlayGameView(context: Context) : View(context) {

    companion object {
        private const val UPDATE_DELAY = 30L
    }

    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mRunnable: Runnable = Runnable {
        invalidate()    //call onDraw()
    }

    private var mBackground: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg)
    private var displayWidth: Int   //Width screen
    private var displayHeight: Int  //Height screen
    private var mRect: Rect

    private var mBirds: List<Bitmap>            //List bitmap for bird
    private var birdFrame: Int = 0              //Theo dõi trạng thái của bird

    private var veclocity: Int = 0  //Vận tốc rơi
    private var gravity: Int = 3

    private var birdX: Float
    private var birdY: Float

    init {
        val resources = context.resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val heightNavigationBar = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0

        displayWidth = context.displayWidth
        displayHeight = context.displayHeight + heightNavigationBar
        mRect = Rect(0, 0, displayWidth, displayHeight)
        mBirds = listOf(
            BitmapFactory.decodeResource(resources, R.drawable.bird),
            BitmapFactory.decodeResource(resources, R.drawable.bird2)
        )

        //draw birds in the center of the screen
        birdX = (displayWidth - mBirds[0].width).toFloat() / 2
        birdY = (displayHeight - mBirds[0].height).toFloat() / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(mBackground, null, mRect, null)   //draw background

        birdFrame = if (birdFrame == 0) 1 else 0

        if (birdY < displayHeight - mBirds[0].height || veclocity < 0) {     //Xét bird không rơi khỏi màn hình
            //Xét bird đang rơi, càng rơi càng nhanh
            veclocity += gravity
            birdY += veclocity
        }

        canvas.drawBitmap(mBirds[birdFrame], birdX, birdY, null)    //draw bird
        mHandler.postDelayed(mRunnable, UPDATE_DELAY)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.action

        if (action == MotionEvent.ACTION_DOWN) {
            veclocity = -30
        }
        return true
    }
}