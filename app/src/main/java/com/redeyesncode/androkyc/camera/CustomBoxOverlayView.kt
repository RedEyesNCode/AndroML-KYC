package com.redeyesncode.androkyc.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class CustomBoxOverlayView(context: Context, private val boxLeft: Float, private val boxTop: Float, private val boxRight: Float, private val boxBottom: Float) : View(context) {
    private val paint: Paint = Paint()

    init {
        // Set paint properties for drawing the box overlay
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the box overlay on the canvas

        // Draw the box overlay on the canvas
        canvas.drawRect(boxLeft, boxTop, boxRight, boxBottom, paint)
//        canvas.drawColor(Color.BLACK)

    }
}