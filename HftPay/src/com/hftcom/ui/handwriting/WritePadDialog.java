package com.hftcom.ui.handwriting;

import com.hftcom.R;
import com.hftcom.ui.pay.SwipingCardActivity;
import com.yifengcom.yfpos.bank.command.CmdPrint.signData;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WritePadDialog extends Dialog {

	SwipingCardActivity context;
	WindowManager.LayoutParams lp;
	DialogListener dialogListener;
	FrameLayout frameLayout;

	public WritePadDialog(SwipingCardActivity context, DialogListener dialogListener) {
		super(context);
		this.context = context;
		this.dialogListener = dialogListener;
	}

	static final int BACKGROUND_COLOR = Color.WHITE;

	static final int BRUSH_COLOR = Color.BLACK;

	PaintView mView = null;

	/** The index of the current color to use. */
	int mColorIndex;
	private boolean isMove = false;
	Activity activity;

	private Button btnOk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sign_dialog_pic);
		frameLayout = (FrameLayout) findViewById(R.id.tablet_view);

		Button btnClear = (Button) findViewById(R.id.tablet_clear);
		btnClear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mView.clear();
				isMove = false;
				btnOk.setBackground(context.getResources().getDrawable(R.drawable.settext_bg_gray, context.getTheme()));
			}
		});

		btnOk = (Button) findViewById(R.id.tablet_ok);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isMove) {
					try {
						dialogListener.refreshActivity(mView.getCachebBitmap());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(context, "请先签名", Toast.LENGTH_SHORT).show();
				   
				}
			}
		});

		initView();

		/*
		 * Button btnCancel = (Button)findViewById(R.id.tablet_cancel);
		 * btnCancel.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { cancel(); } });
		 */
	}

	private void initView() {
		signData data = this.context.getSignData();
		if (data != null) {

			TextView mTextCard = (TextView) findViewById(R.id.mTextCard);
			mTextCard.setText(data.cardno);

			TextView mTextDate = (TextView) findViewById(R.id.mTextDate);
			mTextDate.setText(data.Timestamp);

			TextView mTextMoney = (TextView) findViewById(R.id.mTextMoney);
			mTextMoney.setText(data.Amount + "元");
		}

	}

	public void sendSignPictoServer() {

	}

	private int width = 0;
	private int height = 0;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		width = frameLayout.getWidth();
		height = frameLayout.getHeight();
		if (mView == null) {
			mView = new PaintView(context);
			frameLayout.addView(mView);
			mView.requestFocus();
		}
	}

	/**
	 * This view implements the drawing canvas.
	 * 
	 * It handles all of the input events and drawing functions.
	 */
	class PaintView extends View {
		private Paint paint;
		private Canvas cacheCanvas;
		private Bitmap cachebBitmap;
		private Path path;

		public Bitmap getCachebBitmap() {
			return cachebBitmap;
		}

		public PaintView(Context context) {
			super(context);
			init();
		}

		private void init() {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(5);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			path = new Path();

			cachebBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			cacheCanvas = new Canvas(cachebBitmap);
			cacheCanvas.drawColor(Color.WHITE);
		}

		public void clear() {
			if (cacheCanvas != null) {
				paint.setColor(BACKGROUND_COLOR);
				cacheCanvas.drawPaint(paint);
				paint.setColor(Color.BLACK);
				cacheCanvas.drawColor(Color.WHITE);
				invalidate();
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(cachebBitmap, 0, 0, null);
			canvas.drawPath(path, paint);
			if (btnOk != null && isMove) {
				btnOk.setBackground(context.getResources().getDrawable(R.drawable.settext_bg, context.getTheme()));
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {

			int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
			int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
			if (curW >= w && curH >= h) {
				return;
			}

			if (curW < w)
				curW = w;
			if (curH < h)
				curH = h;

			Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
			Canvas newCanvas = new Canvas();
			newCanvas.setBitmap(newBitmap);
			if (cachebBitmap != null) {
				newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
			}
			cachebBitmap = newBitmap;
			cacheCanvas = newCanvas;
		}

		private int cur_x, cur_y;

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			int x = (int) event.getX();
			int y = (int) event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				cur_x = x;
				cur_y = y;
				path.moveTo(cur_x, cur_y);

				break;
			}

			case MotionEvent.ACTION_MOVE: {
				path.quadTo(cur_x, cur_y, x, y);
				cur_x = x;
				cur_y = y;
				isMove = true;
				invalidate();
				break;
			}

			case MotionEvent.ACTION_UP: {
				cacheCanvas.drawPath(path, paint);
				path.reset();
				break;
			}
			}
			return true;
		}
	}
}
