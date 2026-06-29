package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var imagePlay: Bitmap? = null
    private var imagePause: Bitmap? = null
    private val imageRect = RectF()
    private var isPlaying: Boolean = false
    var onStateChanged: (() -> Unit)? = null
    private val defaultSizePx = (100 * resources.displayMetrics.density).toInt()

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                imagePlay = getDrawable(R.styleable.PlaybackButtonView_imagePlayResId)?.toBitmap()
                imagePause = getDrawable(R.styleable.PlaybackButtonView_imagePauseResId)?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val bitmap = imagePlay ?: imagePause
        val desiredSize = bitmap?.width ?: defaultSizePx

        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bitmapToDraw = if (isPlaying) imagePause else imagePlay
        bitmapToDraw?.let {
            canvas.drawBitmap(it, null, imageRect, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return isEnabled
            }
            MotionEvent.ACTION_UP -> {
                if (isEnabled) {
                    isPlaying = !isPlaying
                    invalidate()
                    onStateChanged?.invoke()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setIsPlaying(playing: Boolean) {
        if (isPlaying != playing) {
            isPlaying = playing
            invalidate()
        }
    }
}