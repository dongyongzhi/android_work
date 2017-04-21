package com.yfcomm.yf_desk;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyPageView extends SmoothPagedView {

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
	private int curpage=2;

	
	private final Runnable mLongPressRunnable = new Runnable() {
		@Override
		public void run() {
			if (isPressed && !IsClickMove) {
				Log.e(TAG, "onLongClick x=" + windowX + "y=" + windowY);
				// IsLongPress = launcher.onLongClick(windowX, windowY);
				myexecLongPress = true;
			}
		}
	};

	public MyPageView(launcher launcher, Handler mHandler) {
		super(launcher,null);
		this.launcher = launcher;
		this.dragController = launcher.GetDragController();
		this.mHandler = mHandler;
		this.setBackgroundColor(0xFF0EC5D9);
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
				Log.e(TAG, "onClick x=" + ev.getX() + "y=" + ev.getY());
				// launcher.onClick(this, windowX, windowY);
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

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		MyRelFragment child = (MyRelFragment) this.getChildAt(curpage);
		child.measure(widthMeasureSpec, heightMeasureSpec);
		
		//for (int i = 0; i < getChildCount(); i++) {
		//	MyRelFragment child = (MyRelFragment) this.getChildAt(2);
		//	child.measure(widthMeasureSpec, heightMeasureSpec);
		//}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		
		MyRelFragment child = (MyRelFragment) this.getChildAt(curpage);
		int x = left;
		int y = top;
		child.layout(x, y, x + getWidth(), y + getHeight());
		/*
		for (int i = 0; i < getChildCount(); i++) {
			MyRelFragment child = (MyRelFragment) this.getChildAt(i);
			int x = left;
			int y = top;
			child.layout(x, y, x + getWidth(), y + getHeight());
		}*/
	}

	@Override
	public void onChildViewAdded(View parent, View child) {
		super.onChildViewAdded(parent, child);
		/*
		 * if (!(child instanceof MyRelFragment)) { throw new
		 * IllegalArgumentException("A Workspace can only have CellLayout children."
		 * ); } MyRelFragment cl = ((MyRelFragment) child);
		 * //cl.setOnInterceptTouchListener(this); cl.setClickable(false);
		 * cl.setContentDescription(getContext().getString(
		 * R.string.workspace_description_format, getChildCount()));
		 */
	}

	protected boolean shouldDrawChild(View child) {
	   // final MyRelFragment cl = (MyRelFragment) child;
		return super.shouldDrawChild(child);
	}

	@Override
	public void syncPages() {
		// TODO Auto-generated method stub

	}

	@Override
	public void syncPageItems(int page, boolean immediate) {
		// TODO Auto-generated method stub

	}

}
