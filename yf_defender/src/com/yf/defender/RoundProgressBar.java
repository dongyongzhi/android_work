package com.yf.defender;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yf.defender.R;

public class RoundProgressBar extends View {
	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	/** 圆环的颜色 */
	private int roundColor;

	/*** 圆环进度的颜色 */
	private int roundProgressColor;

	/**
	 * 中间进度百分比的字符串的颜色
	 */
	private int textColor;

	/**
	 * 中间进度百分比的字符串的字体
	 */
	private float textSize;

	/**
	 * 圆环的宽度
	 */
	// private boolean textIsDisplayable;
	private float roundWidth;
	private int max;
	private int progress;

	private boolean IsShowText = false;
	private String textString = "安装";
	private boolean IsWating = false;
	private final String TAG = "RoundProgressBar";

	/*** 进度的风格，实心或者空心 */
	private int style;

	public static final int STROKE = 0;
	public static final int FILL = 1;

	private Timer timer;
	private int count;
	private int Rotation_times = 8;

	public RoundProgressBar(Context context) {
		this(context, null);
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		paint = new Paint();

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

		// 获取自定义属性和默认值
		roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
		roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
		textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
		textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
		roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
		max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);

		// textIsDisplayable = mTypedArray.getBoolean(
		// R.styleable.RoundProgressBar_textIsDisplayable, true);

		style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
		mTypedArray.recycle();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (IsShowText) {
			onDrawsquare(canvas);
		} else {
			if (IsWating) {
				onDrawGradientcircular(canvas);
			} else {
				onDrawcircular(canvas);
			}
		}

	}

	public void onDrawGradientcircular(Canvas canvas) {

		int centre_x = getWidth() / 2;
		int centre_y = getHeight() / 2;
		int radius = (int) (centre_x - roundWidth / 2 - 50);

		paint.setAntiAlias(true);
		paint.setStrokeWidth(roundWidth);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(roundProgressColor);

		RectF rectBlackBg = new RectF(centre_x - radius, centre_y - radius, centre_x + radius, centre_y + radius);

		int Rotation_angle = 360 / Rotation_times * count;
		canvas.drawArc(rectBlackBg, Rotation_angle, 360 - 10, false, paint);
	}

	public void WaitingDownload() {
		if (!IsWating) {
			IsShowText = false;
			IsWating = true;
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					Log.i(TAG, "WaitingDownload :" + RoundProgressBar.this);
					count = count >= (Rotation_times - 1) ? 0 : ++count;
					yfpostInvalidate();
				}
			}, 100, 100);
		}
	}

	public synchronized void yfpostInvalidate() {
		postInvalidate();
	}

	public void stop() {

		Log.i(TAG, "stop:" + RoundProgressBar.this);
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		IsWating = false;
	    setProgress(0, true, "安装");
		
	}	

	public void onDrawcircular(Canvas canvas) {

		int centre_x = getWidth() / 2;
		int centre_y = getHeight() / 2;
		int radius = (int) (centre_x - 50);

		int offset = 10;
		Rect rect = new Rect(centre_x - offset, centre_y - offset, centre_x + offset, centre_y + offset);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(roundProgressColor);
		paint.setStrokeWidth(roundWidth);
		canvas.drawRect(rect, paint);

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setColor(roundProgressColor);

		radius = (int) (radius - roundWidth / 2);
		RectF oval = new RectF(centre_x - radius, centre_y - radius, centre_x + radius, centre_y + radius);
		canvas.drawArc(oval, 0, 360, false, paint);

		paint.setStrokeWidth(roundWidth + 2);
		switch (style) {

		case STROKE:
			canvas.drawArc(oval, -90, 360 * progress / max, false, paint);
			break;

		case FILL:
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if (progress != 0)
				canvas.drawArc(oval, 0, 360 * progress / max, true, paint);
			break;

		}
	}

	public void onDrawsquare(Canvas canvas) {

		int centre_X = getWidth() / 2;
		int centre_y = getHeight() / 2;
		int x_offset = 50;
		int y_offset = 30;

		paint.setAntiAlias(true);
		RectF rect = new RectF(centre_X - x_offset, centre_y - y_offset, centre_X + x_offset, centre_y + y_offset);
		paint.setColor(textColor);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) 3.0);
		canvas.drawRoundRect(rect, 10, 10, paint);

		paint.setStrokeWidth(0);
		paint.setColor(textColor);
		paint.setTextSize(textSize);

		paint.setTypeface(Typeface.DEFAULT_BOLD);

		float textWidth = paint.measureText(textString);
		canvas.drawText(textString, centre_X - textWidth / 2, centre_y + textSize / 2, paint);

	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		if (max < 0) {
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * 
	 * @return
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
	 * 
	 * @param progress
	 */
	public synchronized void setProgress(int progress, boolean IsText, String text) {
		if (progress < 0) {
			throw new IllegalArgumentException("progress not less than 0");
		}
		if (progress > max) {
			progress = max;
		}

		if (progress <= max) {
			this.IsShowText = IsText;
			this.textString = text;
			this.progress = progress;
			this.IsWating = false;
			postInvalidate();
		}

	}

	public int getCricleColor() {
		return roundColor;
	}

	public void setCricleColor(int cricleColor) {
		this.roundColor = cricleColor;
	}

	public int getCricleProgressColor() {
		return roundProgressColor;
	}

	public void setCricleProgressColor(int cricleProgressColor) {
		this.roundProgressColor = cricleProgressColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public float getRoundWidth() {
		return roundWidth;
	}
	/*
	 * public void setRoundWidth(float roundWidth) { this.roundWidth =
	 * roundWidth; }
	 */

}
