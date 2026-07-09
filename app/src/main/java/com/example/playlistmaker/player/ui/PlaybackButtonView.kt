package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var imagePlay: Drawable? = null
    private var imagePause: Drawable? = null
    private val drawableBounds = Rect()
    private var isPlaying: Boolean = false
    var onStateChanged: (() -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                imagePlay = getDrawable(R.styleable.PlaybackButtonView_imagePlayResId)
                imagePause = getDrawable(R.styleable.PlaybackButtonView_imagePauseResId)
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val drawable = imagePlay ?: imagePause
        val intrinsicWidth = drawable?.intrinsicWidth ?: suggestedMinimumWidth
        val intrinsicHeight = drawable?.intrinsicHeight ?: suggestedMinimumHeight

        val width = resolveSize(intrinsicWidth, widthMeasureSpec)
        val height = resolveSize(intrinsicHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawableBounds.set(0, 0, w, h)

        imagePlay?.bounds = drawableBounds
        imagePause?.bounds = drawableBounds
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val drawableToDraw = if (isPlaying) imagePause else imagePlay
        drawableToDraw?.draw(canvas)
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