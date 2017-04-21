package com.yfcomm.yf_desk;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;

public class MyPageView extends ViewPager {

	private final static String TAG = "MyPageView";
	private DragController dragController;
	private launcher launcher;
	private int windowX;
	private int windowY;
	private Handler mHandler;
	private boolean IsLongPress = false;
	private boolean IsClickMove = false;
	private boolean isPressed = true;
	private boolean myexecLongPress = false;

	private final Runnable mLongPressRunnable = new Runnable() {
		@Override
		public void run() {
			if (isPressed&&!IsClickMove) {
				IsLongPress = launcher.onLongClick(windowX, windowY);
				myexecLongPress = true;
			}
		}
	};

	public MyPageView(launcher launcher, Handler mHandler) {
		super(launcher);
		this.launcher = launcher;
		this.dragController = launcher.GetDragController();
		this.mHandler = mHandler;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		final boolean isDrag = dragController.IsDragView();
		switch (ev.getAction()) {

		case MotionEvent.ACTION_UP:

			isPressed = false;
			if (!myexecLongPress)
				mHandler.removeCallbacks(mLongPressRunnable);
			if (!IsLongPress && !IsClickMove) {
				launcher.onClick(this, windowX, windowY);
			}
			if (isDrag) {

				dragController.onDrop((int) ev.getX(), (int) ev.getY());
			}
			break;

		case MotionEvent.ACTION_MOVE:

			if ((Math.abs(windowX - (int) ev.getX()) > DragController.offset
					|| Math.abs(windowY - (int) ev.getY()) > DragController.offset)) {
				IsClickMove = true;
				if (isDrag)
					dragController.onDrag((int) ev.getX(), (int) ev.getY(), (int) ev.getRawX(), (int) ev.getRawY());
			}
			break;

		case MotionEvent.ACTION_DOWN:
			IsClickMove = false;
			IsLongPress = false;
			isPressed = true;
			myexecLongPress = false;
			windowX = (int) ev.getX();
			windowY = (int) ev.getY();
			dragController.setDragDownXY(windowX, windowY);
			mHandler.postDelayed(mLongPressRunnable, 500);
			break;
		}

		return isDrag ? true : super.onTouchEvent(ev);
	}

	protected int GetDragPointX() {

		return windowX;
	}

	protected int GetDragPointY() {

		return windowY;
	}

}
