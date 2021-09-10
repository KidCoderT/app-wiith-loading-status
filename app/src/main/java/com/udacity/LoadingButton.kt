package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Creating all the styled attr color vars:
    var bgColor: Int = Color.BLACK
    var textColor: Int = Color.BLACK

    private var valueAnimator: ValueAnimator

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new -> }

    var animationProgress: Float = 0.0f
    var hasSelectedDownloadItem = false // Added so as to make it possible to do animation even when showing toast

    // The main paint value (used for all the items except arc on canvas after changing colors values)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER // button text alignment
        textSize = 55.0f //button text size
        typeface = Typeface.create("", Typeface.BOLD) // button text's font style
    }

    // The arcs paint values ( I have segregated these two paints because in the arc I need the lines curved )
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isDither = true;
        style = Paint.Style.FILL;
        color = ContextCompat.getColor(context, R.color.colorAccent);
        strokeJoin = Paint.Join.ROUND;
        strokeCap = Paint.Cap.ROUND;
        isAntiAlias = true;
    }

    init {
        isClickable = true
        valueAnimator = AnimatorInflater.loadAnimator(
            context,
            R.animator.loading_animation
        ) as ValueAnimator

        valueAnimator.addUpdateListener {
            animationProgress = it.animatedValue as Float
            if (!hasSelectedDownloadItem && animationProgress > 99) {
                stopButtonAnimation()
            }
            invalidate() // Need to update every time
            requestLayout() // Gets the layout dimensions after the new Screen redraws
        }

        // Initializing Color vars with actual colors and also getting from styled vars
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            bgColor = getColor(R.styleable.LoadingButton_bgColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }
    }

    fun stopButtonAnimation() {
        valueAnimator.cancel()
        buttonState = ButtonState.Completed
        hasSelectedDownloadItem = false
        invalidate() // Need to update every time
        requestLayout() // Gets the layout dimensions after the new Screen redraws
    }

    fun startAnimation() {
        if (buttonState == ButtonState.Completed) buttonState = ButtonState.Loading
        valueAnimator.start() // start the animation
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Drawing the button background after some changes to the paint
        paint.strokeWidth = 0f
        paint.color = bgColor
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // to show download progress with rect and arc
        if (buttonState == ButtonState.Loading) {
            // Setting the color value to primaryDark and drawing the rect width based on how much progress has been made
            paint.color = Color.parseColor("#004349")
            canvas?.drawRect(
                0f, 0f,
                (width * (animationProgress / 100)).toFloat(), height.toFloat(), paint
            )

            // Setting the color value to primaryAccent and drawing the arc angle based on how much progress has been made
            paint.color = Color.parseColor("#F9A825")
            // Extra for alignment variable was not able to put in the class for some reason
            val cx = (width / 2).toFloat() + 250
            val cy = (height / 2).toFloat()
            val radius = 50;
            val oval = RectF(cx - radius, cy - radius, cx + radius, cy + radius);
            canvas?.drawArc(oval, 0F, (360*(animationProgress / 100)).toFloat(), true, arcPaint);
        }

        // check the button state amd sets the text accordingly:
        val buttonText = if (buttonState == ButtonState.Loading)
            resources.getString(R.string.loading)
        else resources.getString(R.string.download)

        // write the text on custom button after editing the paint values
        paint.color = textColor
        // I add 30 to the height just to fine tune the height
        canvas?.drawText(buttonText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)
    }

}