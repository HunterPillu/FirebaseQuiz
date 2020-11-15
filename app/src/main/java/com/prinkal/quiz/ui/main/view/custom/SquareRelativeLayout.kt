package com.prinkal.quiz.ui.main.view.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class SquareRelativeLayout : RelativeLayout {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (width > height) height else width
        setMeasuredDimension(size, size)
    }

    /*override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (widthMeasureSpec < heightMeasureSpec) {
            setMeasuredDimension(widthMeasureSpec, widthMeasureSpec)
            super.onMeasure(
                widthMeasureSpec,
                widthMeasureSpec
            )
        } else {
            setMeasuredDimension(heightMeasureSpec, heightMeasureSpec)
            super.onMeasure(heightMeasureSpec, heightMeasureSpec)
        }
    }*/
}