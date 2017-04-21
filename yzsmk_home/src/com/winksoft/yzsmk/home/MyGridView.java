package com.winksoft.yzsmk.home;

import com.winksoft.yzsmk.public_define.AppInfo;
import com.winksoft.yzsmk.public_define.ReCreateWin;
import com.winksoft.yzsmk.public_define.public_define;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint({ "ClickableViewAccessibility", "RtlHardcoded" })
public class MyGridView extends GridView {

	public int downX;

	public int downY;

	public int windowX;

	public int windowY;

	private int win_view_x;

	private int win_view_y;

	int dragOffsetX;

	int dragOffsetY;

	public int dragPosition;

	private int dropPosition;

	private int startPosition;

	private int itemHeight;

	private int itemWidth;

	private View dragImageView = null;

	private String TAG = "MyGridView";
	private WindowManager windowManager = null;

	private WindowManager.LayoutParams windowParams = null;
	private int itemTotalCount;

	private int nColumns = 2;

	private int Remainder;

	private boolean isMoving = false;
	private double dragScale = 1.0D;

	private MyGridAdapter mDragAdapter = null;
	private ReCreateWin RecreateW;

	public MyGridView(Context context, ReCreateWin RecreateW) {
		super(context);
		this.RecreateW = RecreateW;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			windowX = (int) ev.getX();
			windowY = (int) ev.getY();
			setOnItemClickListener(ev);
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (dragImageView != null
				&& dragPosition != AdapterView.INVALID_POSITION) {
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			switch (ev.getAction()) {

			case MotionEvent.ACTION_DOWN:

				downX = (int) ev.getX();
				windowX = (int) ev.getX();
				downY = (int) ev.getY();
				windowY = (int) ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:

				onDrag(x, y, (int) ev.getRawX(), (int) ev.getRawY());
				if (!isMoving) {
					OnMove(x, y);
				}
				if (pointToPosition(x, y) != AdapterView.INVALID_POSITION) {
					break;
				}
				break;
			case MotionEvent.ACTION_UP:

				stopDrag();
				onDrop(x, y);
				requestDisallowInterceptTouchEvent(false);
				break;

			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	private void onDrag(int x, int y, int rawx, int rawy) {

		if (dragImageView != null) {
			// windowParams.alpha = 0.6f;
			windowParams.x = rawx - win_view_x;
			windowParams.y = rawy - win_view_y;

			windowManager.updateViewLayout(dragImageView, windowParams);
			if ((rawx >= 660) && (getId() < public_define.total_page)) {
				public_define.vp.setCurrentItem(getId() + 1);
			}
			if ((rawx < 60) && (getId() - 1) > 0) {

				public_define.vp.setCurrentItem(getId() - 1);

			}

		}
	}

	private void onDrop(int x, int y) {
		
		if ((public_define.postion != -1) && (public_define.pageId > 0)) {

			int curItem = public_define.vp.getCurrentItem();

			if (public_define.pageId == public_define.vp.getCurrentItem()) {
	
				mDragAdapter = (MyGridAdapter) getAdapter();

				dropPosition = pointToPosition(x, y);
				
				if (dropPosition != -1) {
					mDragAdapter.exchange(public_define.postion, dropPosition);
					mDragAdapter.notifyDataSetChanged();
					RecreateW.swapAppInfo(mDragAdapter
							.getPostionAppInfo(public_define.postion),
							mDragAdapter.getPostionAppInfo(dropPosition));
					public_define.my_sqlite.swappostion(mDragAdapter
							.getPostionAppInfo(public_define.postion),
							mDragAdapter.getPostionAppInfo(dropPosition));
				}else{
					mDragAdapter.notifyDataSetChanged();
				}

			} else {
				int cur_x = 0;
				MyGridView m_beg = public_define.myView
						.get(public_define.pageId - 1);
				MyGridAdapter beg = (MyGridAdapter) m_beg.getAdapter();
				MyGridView m_after = public_define.myView.get(curItem - 1);
				MyGridAdapter End = (MyGridAdapter) m_after.getAdapter();

				if (public_define.pageId > curItem)
					cur_x = (public_define.pageId - curItem)
							* public_define.width + x;

				else {
					cur_x = x - (curItem - public_define.pageId)
							* public_define.width;
				}

				dropPosition = pointToPosition(cur_x, y);
				if (dropPosition != -1) {
					AppInfo appi_b = beg
							.getPostionAppInfo(public_define.postion);
					AppInfo appi_after = End.getPostionAppInfo(dropPosition);

					int id = appi_b.id;
					int pageid = appi_b.curpageid;
					int pageitem = appi_b.curindex;

					appi_b.id = appi_after.id;
					appi_b.curpageid = appi_after.curpageid;
					appi_b.curindex = appi_after.curindex;

					appi_after.curpageid = pageid;
					appi_after.id = id;
					appi_after.curindex = pageitem;

					beg.changeAppInfoData(appi_after, public_define.postion);
					End.changeAppInfoData(appi_b, dropPosition);
					public_define.postion = -1;
					public_define.my_sqlite.swappostion(appi_b, appi_after);
					boolean IsNeadCreate = true;
					for (int i = 0; i < beg.appinfo.size(); i++) {
						if (beg.appinfo.get(i).IsNull == 0) {
							IsNeadCreate = false;
							break;
						}
					}
					if (IsNeadCreate) {
						RecreateW.recreateWin(beg.appinfo);
					} else {
						RecreateW.swapAppInfo(appi_b, appi_after);
					}

				} else {
					beg.notifyDataSetChanged();
				}
			}
		}

	}

	public int getPostion(int x, int y) {

		int postion = -1;// hight 1090-80-30=1000 width= 680

		if (public_define.width / 2 > x) {// item 0,2,4
			if (public_define.hight / 3 > y) {
				postion = 0;
			} else if (public_define.hight / 3 * 2 > y) {
				postion = 2;
			} else if (public_define.hight / 3 * 3 > y) {
				postion = 4;
			}
		} else { // item 1,3,5
			if (public_define.hight / 3 > y) {
				postion = 1;
			} else if (public_define.hight / 3 * 2 > y) {
				postion = 3;
			} else if (public_define.hight / 3 * 3 > y) {
				postion = 5;
			}
		}
		return postion;
	}

	public void setOnItemClickListener(final MotionEvent ev) {
		setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				mDragAdapter = (MyGridAdapter) getAdapter();
				AppInfo dragItem = mDragAdapter.getItem(position);
				if (dragItem.IsNull == 1) {
					return false;
				}
				int x = (int) ev.getX();
				int y = (int) ev.getY();

				startPosition = position;
				dragPosition = position;

				ViewGroup dragViewGroup = (ViewGroup) getChildAt(dragPosition
						- getFirstVisiblePosition());
				TextView dragTextView = (TextView) dragViewGroup
						.findViewById(R.id.tv_item);
				dragTextView.setSelected(true);
				dragTextView.setEnabled(false);
				itemHeight = dragViewGroup.getHeight();
				itemWidth = dragViewGroup.getWidth();
				itemTotalCount = MyGridView.this.getCount();

				Remainder = (itemTotalCount % nColumns);
				if (Remainder != 0) {
				} else {
				}
				if (dragPosition != AdapterView.INVALID_POSITION) {

					win_view_x = windowX - dragViewGroup.getLeft();
					win_view_y = windowY - dragViewGroup.getTop();

					dragOffsetX = (int) (ev.getRawX() - x);
					dragOffsetY = (int) (ev.getRawY() - y);

					dragViewGroup.destroyDrawingCache();
					dragViewGroup.setDrawingCacheEnabled(true);
					Bitmap dragBitmap = Bitmap.createBitmap(dragViewGroup
							.getDrawingCache());

					startDrag(dragBitmap, (int) ev.getRawX(),
							(int) ev.getRawY());

					dragViewGroup.setVisibility(View.VISIBLE);
					requestDisallowInterceptTouchEvent(true);

					dragItem.ismove = true;

					public_define.postion = position;
					public_define.pageId = public_define.vp.getCurrentItem();
					// Log.e(TAG, "PageId=" + public_define.pageId);
					mDragAdapter.notifyDataSetChanged();
					// mDragAdapter.changeAppInfoData(dragItem, position);
					return true;
				}
				return false;
			}
		});
	}

	public void startDrag(Bitmap dragBitmap, int x, int y) {
		stopDrag();
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;

		windowParams.x = x - win_view_x;
		windowParams.y = y - win_view_y;

		windowParams.width = (int) (dragScale * dragBitmap.getWidth());
		windowParams.height = (int) (dragScale * dragBitmap.getHeight());

		this.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

		ImageView iv = new ImageView(getContext());
		iv.setImageBitmap(dragBitmap);
		windowManager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);// "window"
		windowManager.addView(iv, windowParams);
		dragImageView = iv;
	}

	private void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	public void OnMove(int x, int y) {

		int dPosition = pointToPosition(x, y);

		if (dPosition > -1) {
			if ((dPosition == -1) || (dPosition == dragPosition)) {
				return;
			}
			dropPosition = dPosition;
			if (dragPosition != startPosition) {
				dragPosition = startPosition;
			}
			int movecount;
			if ((dragPosition == startPosition)
					|| (dragPosition != dropPosition)) {
				movecount = dropPosition - dragPosition;
			} else {
				movecount = 0;
			}
			if (movecount == 0) {
				return;
			}
			// mDragAdapter = (MyGridAdapter) getAdapter();
			// mDragAdapter.exchange(startPosition, dropPosition);
			startPosition = dropPosition;
			dragPosition = dropPosition;
		}
	}

}
