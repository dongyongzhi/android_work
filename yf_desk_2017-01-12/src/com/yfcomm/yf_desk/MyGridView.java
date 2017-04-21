package com.yfcomm.yf_desk;

import com.yfcomm.public_define.AppInfo;
import com.yfcomm.public_define.ReCreateWin;
import com.yfcomm.public_define.public_define;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
	private View dragImageView = null;
	private WindowManager windowManager = null;
	private WindowManager.LayoutParams windowParams = null;
	private int itemTotalCount;
	private int nColumns = 2;
	private int Remainder;
	private boolean isMoving = false;
	private double dragScale = 1.0D;
	private MyGridAdapter mDragAdapter = null;
	private ReCreateWin RecreateW;
	private int totalpage;
	private ViewPager vp;

	public MyGridView(Context context, ReCreateWin RecreateW, int totalpage, ViewPager vp) {
		super(context);
		this.RecreateW = RecreateW;
		this.totalpage = totalpage;
		this.vp = vp;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
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

		if (dragImageView != null && dragPosition != AdapterView.INVALID_POSITION) {
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

				if (!isMoving) {
					OnMove(x, y);
					onDrag(x, y, (int) ev.getRawX(), (int) ev.getRawY());
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
		}else{
			stopDrag();
		}
		return super.onTouchEvent(ev);
	}

	private void onDrag(int x, int y, int rawx, int rawy) {

		if (dragImageView != null) {
			windowParams.x = rawx - win_view_x;
			windowParams.y = rawy - win_view_y;

			windowManager.updateViewLayout(dragImageView, windowParams);

			if ((rawx >= 660) && (vp.getCurrentItem() < totalpage)) {// go Right Page
				//vp.setCurrentItem(vp.getCurrentItem()+ 1);
				vp.setCurrentItem(getId()+ 1);
			}
			if ((rawx < 80) && vp.getCurrentItem() > public_define.Nonslip) {// go left page
				//vp.setCurrentItem(vp.getCurrentItem()- 1);
			    vp.setCurrentItem(getId() - 1);

			}

		}
	}

	private void onDrop(int x, int y) {

		if (public_define.postion != -1) {
			int curItem = vp.getCurrentItem();

			if (public_define.pageId == curItem) {

				mDragAdapter = (MyGridAdapter) getAdapter();
				dropPosition = pointToPosition(x, y);

				if (dropPosition != -1) {
					
						RecreateW.OnOnepageSwapAppInfos(mDragAdapter, public_define.postion, dropPosition);

				} else {
					mDragAdapter.notifyDataSetChanged();
				}

			} else {
				int cur_x = 0;

				MyGridAdapter beg = (MyGridAdapter)getAdapter();
				
				if (public_define.pageId > curItem)
					cur_x = (public_define.pageId - curItem) * public_define.width + x;
				else {
					cur_x = x - (curItem - public_define.pageId) * public_define.width;
				}
				dropPosition = pointToPosition(cur_x, y);
				if (dropPosition != -1) {
					RecreateW.OnOtherpageSwapAppInfos(beg, public_define.postion,vp.getCurrentItem(),dropPosition);
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
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				mDragAdapter = (MyGridAdapter) getAdapter();
				AppInfo dragItem = mDragAdapter.getItem(position);
				if (dragItem.IsNull == 1) {
					return false;
				}
				int x = (int) ev.getX();
				int y = (int) ev.getY();

				startPosition = position;
				dragPosition = position;

				ViewGroup dragViewGroup = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
				TextView dragTextView = (TextView) dragViewGroup.findViewById(R.id.tv_item);
				dragTextView.setSelected(true);
				dragTextView.setEnabled(false);

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
					Bitmap dragBitmap = Bitmap.createBitmap(dragViewGroup.getDrawingCache());

					startDrag(dragBitmap, (int) ev.getRawX(), (int) ev.getRawY());

					dragViewGroup.setVisibility(View.VISIBLE);
					requestDisallowInterceptTouchEvent(true);

					dragItem.ismove = true;
					saveMvItemAtt(position);
					mDragAdapter.notifyDataSetChanged();
					return true;
				}
				return false;
			}
		});
	}

	public void saveMvItemAtt(int position) {
		public_define.postion = position;
		public_define.pageId = vp.getCurrentItem();
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
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

		ImageView iv = new ImageView(getContext());
		iv.setImageBitmap(dragBitmap);
		windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// "window"
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
