package com.material.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: keith.
 * Date: 14-10-10.
 * Time: 14:47.
 */
public class Slider extends View {

    private int mColor;
    private int mTrackColor;
    private int mRippleColor;
    private int mMax;
    private int mHeight;
    private int mThumbRadius;
    private int mThumbSelectedRadius;
    private int mThumbBorderWidth;
    private int mTrackHeight;

    private Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Slider(Context context) {
        this(context, null);
    }

    public Slider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Slider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Slider);
        mColor = attributes.getColor(R.styleable.Slider_slider_color,
                getResources().getColor(R.color.slider_color));
        mTrackColor = attributes.getColor(R.styleable.Slider_slider_track_color,
                getResources().getColor(R.color.slider_track_color));
        mRippleColor = attributes.getColor(R.styleable.Slider_slider_color,
                getResources().getColor(R.color.slider_ripple_color));
        mMax = attributes.getInteger(R.styleable.Slider_slider_max,
                getResources().getInteger(R.integer.slider_max));
        mHeight = getResources().getDimensionPixelSize(R.dimen.slider_height);
        mThumbRadius = getResources().getDimensionPixelSize(R.dimen.slider_thumb_radius);
        mThumbSelectedRadius = getResources().getDimensionPixelSize(R.dimen.slider_thumb_selected_radius);
        mThumbBorderWidth = getResources().getDimensionPixelSize(R.dimen.slider_thumb_border_width);
        mTrackHeight = attributes.getDimensionPixelSize(R.styleable.Slider_slider_track_height,
                getResources().getDimensionPixelSize(R.dimen.slider_track_height));
        attributes.recycle();

        trackPaint.setColor(mTrackColor);
        trackPaint.setStrokeWidth(mTrackHeight);

        thumbPaint.setColor(mColor);
        ripplePaint.setColor(mRippleColor);
    }

    public void setTrackColor(int color) {
        mTrackColor = color;
        trackPaint.setColor(mTrackColor);
        invalidate();
    }

    public void setTrackHeight(int height) {
        mTrackHeight = height;
        trackPaint.setStrokeWidth(mTrackHeight);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        }
        if (heightSpecSize < mHeight) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //thumbPaint.setStyle(Paint.Style.STROKE);
        //thumbPaint.setStrokeWidth(mThumbBorderWidth);
        canvas.drawCircle(mThumbRadius, getHeight() / 2, mThumbRadius, thumbPaint);
        canvas.drawLine(mThumbRadius * 2 + 1,
                getHeight() / 2,
                getWidth(),
                getHeight() / 2,
                trackPaint);
    }
}
