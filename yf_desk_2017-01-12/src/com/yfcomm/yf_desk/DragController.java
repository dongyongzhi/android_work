package com.yfcomm.yf_desk;

import com.yfcomm.public_define.WinInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class DragController {

	private ImageView dragImageView = null;
	private WindowManager windowManager = null;
	private WindowManager.LayoutParams windowParams = null;
	private launcher launcher;
	private double dragScale = 1.0D;
	private int dragdownX = 0;
	private int dragdownY = 0;

	private int prevMoveX = 0;
	private int prevMoveY = 0;
    private WinInfo dragWin;
    private View  dragV;
    private int  dragChildId;
	private boolean isMove = false;
	private int win_view_x;
	private int win_view_y;
	private final static String TAG = "DragController";
	protected static final int offset = 10;
	private boolean IsSwithpage = false;
	private Handler handler;

	DragController(launcher launcher, Handler hander) {
		this.launcher = launcher;
		this.handler = hander;
	}

	protected boolean IsDragView() {
		return isMove;
	}

	protected void setDragDownXY(int dragdownX, int dragdownY) {
		this.dragdownX = dragdownX;
		this.dragdownY = dragdownY;
	}

	protected void startDrag(View v,int id,WinInfo dragWin , Bitmap dragBitmap, int x, int y) {
		stopDrag();
 
		this.dragWin=dragWin;
		this.dragV=v;
		this.dragChildId=id;
		isMove = true;
		prevMoveX = dragdownX;
		prevMoveY = dragdownY;

		if (windowParams == null) {
			windowParams = new WindowManager.LayoutParams();
		}
		windowParams.gravity = Gravity.TOP | Gravity.START;

		win_view_x = dragBitmap.getWidth();
		win_view_y = dragBitmap.getHeight();

		windowParams.x = dragdownX - win_view_x / 2;
		windowParams.y = dragdownY - win_view_y / 2;

		windowParams.width = (int) (dragScale * win_view_x);
		windowParams.height = (int) (dragScale * win_view_y);

		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

		if (dragImageView == null) {
			dragImageView = new ImageView(launcher);
		}
		dragImageView.setImageBitmap(dragBitmap);
		windowManager = (WindowManager) launcher.getSystemService(Context.WINDOW_SERVICE);// "window"
		windowManager.addView(dragImageView, windowParams);

	}

	protected void stopDrag() {
		if (isMove) {
			windowManager.removeView(dragImageView);
			isMove = false;
		}
	}

	protected void onDrop(int x, int y) {
		stopDrag();
		launcher.OnDrop(dragV,dragChildId,dragWin,x, y);
		
	}

	protected void onDrag(int rawx, int rawy) {
		onDrag(dragdownX, dragdownY, rawx, rawy);
	}

	protected void onDrag(int x, int y, int rawx, int rawy) {

		if (isMove && (Math.abs(prevMoveX - x) > offset || Math.abs(prevMoveY - y) > offset)) {
			prevMoveX = x;
			prevMoveY = y;
			windowParams.x = x - win_view_x / 2;
			windowParams.y = y - win_view_y / 2;

			if ((rawx >= 660) || (rawx < 60)) {
				if (!IsSwithpage) {
					IsSwithpage = true;
					launcher.SwitchPage(rawx < 60 ? true : false);
					handler.postDelayed(new Runnable() {
						public void run() {
							IsSwithpage = false;
						}
					}, 1000);
				}
			}
			windowManager.updateViewLayout(dragImageView, windowParams);
		}
	}

}
