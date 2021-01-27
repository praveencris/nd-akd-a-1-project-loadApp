package com.udacity

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnRepeat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


private const val DURATION: Long = 4000

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var buttonBackgroundColor=0
    private var buttonTextColor=0
    private var buttonLoadingBackgroundColor=0
    private var buttonArchColor=0



    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator: ValueAnimator = ValueAnimator()
    private var animatedWidth: Float = 0.0f;
    private lateinit var rectF: RectF


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        Log.d("LoadingButton", new.toString())
        when (new) {
            ButtonState.Loading -> {
                valueAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
                    animatedWidth = valueAnimator.animatedValue as Float;
                    /*valueAnimator.duration =
                        DURATION - ((animatedWidth / widthSize) * DURATION).toLong()*/
                    invalidate()
                })
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                valueAnimator.duration = 0
                animatedWidth = 0.0f
                invalidate()
            }
            ButtonState.Clicked -> {
                valueAnimator.duration = DURATION
                rectF = RectF(widthSize.toFloat() - 200F, heightSize.toFloat()/2-40F, widthSize.toFloat() - 120F, heightSize.toFloat()/2+40F)
            }
        }
    }


    init {
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.repeatCount=Animation.INFINITE
        valueAnimator.duration = DURATION

        context.withStyledAttributes(attrs,R.styleable.LoadingButton){
          buttonBackgroundColor=getColor(R.styleable.LoadingButton_backgroundColor,0)
          buttonLoadingBackgroundColor=getColor(R.styleable.LoadingButton_loadingBackgroundColor,0)
          buttonTextColor=getColor(R.styleable.LoadingButton_textColor,0)
          buttonArchColor=getColor(R.styleable.LoadingButton_loadingArchColor,0)
        }
    }


    private val paintRect: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = buttonBackgroundColor
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = buttonTextColor
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    private val paintLoadingRect = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = buttonLoadingBackgroundColor
    }

    private val paintLoadingArch = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = buttonArchColor
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        when (buttonState) {
            ButtonState.Completed -> {
                canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintRect)

                val textHeight = paintText.descent() - paintText.ascent()
                //Then, based on the height, we calculate the offset:
                val textOffset = textHeight / 2 - paintText.descent()
                //And finally, we add the offset to the y coordinate when drawing the text:
                canvas?.drawText(
                    context.getString(R.string.download),
                    widthSize.toFloat() / 2,
                    heightSize.toFloat() / 2 + textOffset,
                    paintText
                )
            }
            ButtonState.Loading -> {
                canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintRect)
                canvas?.drawRect(0f, 0f, animatedWidth, height.toFloat(), paintLoadingRect)
                val textHeight = paintText.descent() - paintText.ascent()
                //Then, based on the height, we calculate the offset:
                val textOffset = textHeight / 2 - paintText.descent()
                //And finally, we add the offset to the y coordinate when drawing the text:
                canvas?.drawText(
                    context.getString(R.string.button_loading),
                    widthSize.toFloat() / 2,
                    heightSize.toFloat() / 2 + textOffset,
                    paintText
                )
                canvas?.drawArc(
                    rectF,
                    0F, (animatedWidth / widthSize) * 360,
                    true,
                    paintLoadingArch
                )
            }
        }

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
        valueAnimator.setFloatValues(0.0f, widthSize.toFloat())
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }


}

