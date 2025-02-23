package com.example.zappycode.quizapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.animation.ValueAnimator;

public class CircularProgressView extends View {

    private Paint paint;
    private float progress = 0;
    private float maxProgress = 100;

    // Colors for the circle
    private int progressColor = Color.GREEN;
    private int backgroundColor = Color.LTGRAY;

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background circle
        paint.setColor(backgroundColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - paint.getStrokeWidth(), paint);

        // Draw progress circle
        paint.setColor(progressColor);
        canvas.drawArc(new RectF(paint.getStrokeWidth(), paint.getStrokeWidth(), getWidth() - paint.getStrokeWidth(), getHeight() - paint.getStrokeWidth()),
                -90, 360 * (progress / maxProgress), false, paint);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();  // Redraw the view
    }

    public void startProgressAnimation(float startProgress, float endProgress) {
        ValueAnimator animator = ValueAnimator.ofFloat(startProgress, endProgress);
        animator.setDuration(1000); // Duration of the animation (in ms)
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgress((float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }
}
