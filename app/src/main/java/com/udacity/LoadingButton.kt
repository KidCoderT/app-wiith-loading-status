package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bgColor: Int = Color.BLACK
    private var textColor: Int = Color.BLACK // default color

    // tells the compiler that the value of a variable
    // must never be cached as its value may change outside
    @Volatile
    private var progress: Double = 0.0
    private var valueAnimator: ValueAnimator

    // observes the state of button
    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
    }

    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()
        invalidate()    // redraw the screen
        requestLayout() // when rectangular progress dimension changes
    }

    // call after downloading is completed
    fun hasCompletedDownload() {
        // cancel the animation when file is downloaded
        valueAnimator.cancel()
        buttonState = ButtonState.Completed
        invalidate()
        requestLayout()
    }

    // initialize
    init {
        isClickable = true
        valueAnimator = AnimatorInflater.loadAnimator(
            context,
            // properties for downloading progress is defined
            R.animator.loading_animation
        ) as ValueAnimator

        valueAnimator.addUpdateListener(updateListener)

        // initialize custom attributes of the button
        val attr = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,
            0
        )
        try {

            // button back-ground color
            bgColor = attr.getColor(
                R.styleable.LoadingButton_bgColor,
                ContextCompat.getColor(context, R.color.colorPrimary)
            )

            // button text color
            textColor = attr.getColor(
                R.styleable.LoadingButton_textColor,
                ContextCompat.getColor(context, R.color.white)
            )
        } finally {
            // clearing all the data associated with attribute
            attr.recycle()
        }
    }

    // set attributes of paint
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER // button text alignment
        textSize = 55.0f //button text size
        typeface = Typeface.create("", Typeface.BOLD) // button text's font style
    }

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isDither = true;                    // set the dither to true
        style = Paint.Style.FILL;       // set to STOKE
        color = ContextCompat.getColor(context, R.color.colorAccent);
        strokeJoin = Paint.Join.ROUND;    // set the join to round you want
        strokeCap = Paint.Cap.ROUND;      // set the paint cap to round too
        pathEffect = CornerPathEffect(50F);   // set the path effect when they join.
        isAntiAlias = true;
    }

    fun startDownload() {
        if (buttonState == ButtonState.Completed) buttonState = ButtonState.Loading
        animation()
    }

    // start the animation when button is clicked
    private fun animation() {
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.strokeWidth = 0f
        paint.color = bgColor
        // draw custom button
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // to show rectangular progress on custom button while file is downloading
        if (buttonState == ButtonState.Loading) {
            paint.color = Color.parseColor("#004349")
            canvas.drawRect(
                0f, 0f,
                (width * (progress / 100)).toFloat(), height.toFloat(), paint
            )
            paint.color = Color.parseColor("#F9A825")

            val cx = (width / 2).toFloat() + 250
            val cy = (height / 2).toFloat()
            val radius = 50;
            val oval = RectF(cx - radius, cy - radius, cx + radius, cy + radius);
            canvas.drawArc(oval, 0F, (360*(progress / 100)).toFloat(), true, arcPaint);
        }

        // check the button state
        val buttonText = if (buttonState == ButtonState.Loading)
            resources.getString(R.string.loading)  // We are loading as button text
        else resources.getString(R.string.download)// download as button text

        // write the text on custom button
        paint.color = textColor
        canvas.drawText(buttonText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)
    }

}