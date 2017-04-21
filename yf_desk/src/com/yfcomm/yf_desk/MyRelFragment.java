package com.yfcomm.yf_desk;

import java.util.List;

import com.yfcomm.public_define.WinInfo;
import com.yfcomm.public_define.public_define;
import com.yfcomm.public_define.WinInfo.Postion;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyRelFragment extends RelativeLayout {

	private launcher launcher;
	private List<WinInfo> win_child;

	private final static String TAG = "MyRelFragment";
	protected final static int custom = 0;
	protected final static int standard = 1;

	// standard 3*2 rectangle
	protected final static int num_hight = 3;
	protected final static int num_with = 2;
	private final static int Item_left = 20; // All around spacing
	private final static int Item_spacing = 5; // item spacing
	private final static int Item_hight = (1080 - Item_left - 2 * Item_spacing) / 3;
	private final static int Item_with = (720 - 2 * Item_left - Item_spacing) / 2;
	private int color[] = { 0xFFFF668B, 0xFFFFA500, 0xFFFD3A58, 0xFF0EC5D9, 0xFF91D92A, 0xFF4EC72E };
	// custom
	private Rect[] cell;
	private boolean[] cell_occupied;
	private int type;
	private Rect mTouchFrame;
	protected static final int INVALID_POSITION = -1;
	protected static final int NO_WININFO = -2;
	private WinInfo win_i;
	private int dropId;
	private int pageId;

	public MyRelFragment(launcher context, int pageCurId, List<WinInfo> wi, int type) {
		super(context);

		this.launcher = context;
		this.win_child = wi;
		this.type = type;
		this.pageId = pageCurId;
		setTag(pageCurId);
		if (type == custom) {
			addCutomview(wi);
		} else {
			cell = new Rect[num_hight * num_with];
			cell_occupied = new boolean[num_hight * num_with];

			int left, top;
			for (int i = 0; i < cell.length; i++) {
				left = i % 2 == 0 ? Item_left : (Item_with + Item_spacing + Item_left);
				top = (i == 0 || i == 1) ? Item_left : ((i / 2) * Item_hight + (i / 2) * Item_spacing) + Item_left;
				cell[i] = new Rect(left, top, Item_with + left, Item_hight + top);
				cell_occupied[i] = false;
			}
			addChildstandard(wi);
		}
		setBackgroundColor(AppXmltran.backcolor);
		setClickable(false);
	}

	protected int getIdleItem() {

		int item = INVALID_POSITION;
		if (type != standard || getChildCount() >= num_hight * num_with) {
			return item;
		}
		for (item = 0; item < cell_occupied.length; item++) {
			if (!cell_occupied[item]) {
				break;
			}
		}
		return item;
	}

	protected int getRelType() {
		return type;
	}
    /*
	private void printChildInfo(String str) {

		int Tag, i;
		Log.e(TAG, "-------------" + str + "-----------");
		for (i = 0; i < getChildCount(); i++) {
			Tag = (Integer) getChildAt(i).getTag();
			Log.e(TAG, "TAG=" + Tag);
		}
		for (i = 0; i < cell_occupied.length; i++) {
			Log.e(TAG, "boolean=" + cell_occupied[i]);
		}
		for (WinInfo win : win_child) {
			Log.e(TAG," title=" + win.title + " package=" + win.Packetname + " item=" + win.item
					+ " pager=" + win.pageId);
		}
	}*/

	protected void removeChild(int dropid) {
		
		View view = getchildView(dropid);
		removeView(view);
		remove(dropid);
		
	}

	protected View getchildView(int pos) {
		View view;
		for (int i = 0; i < getChildCount(); i++) {
			view=getChildAt(i);
			if ((Integer)view.getTag() == pos) {
                return view;
			}
		}
       return null;
	}

	protected void remove(int dropid) {

		for (int i = 0; i < win_child.size(); i++) {
			if (win_child.get(i).item == dropid) {
				win_child.remove(i);
				cell_occupied[dropid] = false;
			}
		}
	}

	protected boolean add(WinInfo mWin) {
		return win_child.add(mWin);
	}

	protected void AddchildtoIdle(WinInfo winInfo, int item) {
		winInfo.item=item;
		AddChildViewstandard(winInfo);
		win_child.add(winInfo);
	}

	protected int DropToCellPoint(int dropx, int dropy, WinInfo wi) {

		for (int i = 0; i < cell.length; i++) {

			if (cell[i].contains(dropx, dropy)) {

				if (cell_occupied[i]) {
					win_i = getWinInfo(i);
					return i;
				} else {
					dropId = i;
					return NO_WININFO;
				}
			}
		}
		return INVALID_POSITION;
	}

	protected WinInfo getDropWinInfo() {
		return win_i;
	}

	protected int getDropID() {
		return dropId;
	}

	protected void switchInfo(int position, WinInfo src) {
		
		WinInfo winInfo = getWinInfo(position);
		switchInfo(src, winInfo);
	}

	protected void onDrop(View child, WinInfo wi) {

		ImageView ImView = (ImageView) child.findViewById(R.id.child_iv);
		TextView txView = (TextView) child.findViewById(R.id.child_tv);
		if (wi.Packetname.equals(public_define.settingname)) {
			ImView.setImageDrawable(launcher.getResources().getDrawable(R.drawable.sz, launcher.getTheme()));
		} else {
			ImView.setImageDrawable(wi.icon);
		}
		txView.setText(wi.title);
		child.setVisibility(View.VISIBLE);
	}

	protected void switchInfo(WinInfo src, WinInfo des) {

		Drawable icon;
		String title;
		String Packetname;
		int pid;

		icon = des.icon;
		title = des.title;
		Packetname = des.Packetname;
		pid = des.pageId;
	
		des.icon = src.icon;
		des.title = src.title;
		des.Packetname = src.Packetname;
		des.pageId = src.pageId;

		src.icon = icon;
		src.title = title;
		src.Packetname = Packetname;
		src.pageId = pid;
	}

	protected int pointToPosition(int dropx, int dropy, boolean IsAllVisible) {

		Rect frame = mTouchFrame;
		if (frame == null) {
			mTouchFrame = new Rect();
			frame = mTouchFrame;
		}
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == View.VISIBLE || IsAllVisible) {
				child.getHitRect(frame);
				if (frame.contains(dropx, dropy)) {
					return (Integer) child.getTag();
				}
			}
		}
		return INVALID_POSITION;
	}

	protected void addChildstandard(List<WinInfo> wi) {

		for (int i = 0; i < wi.size(); i++) {
			AddChildViewstandard(wi.get(i));
		}
	}

	protected WinInfo getWinInfo(int dropx, int dropy) {

		final int pos = pointToPosition(dropx, dropy, false);
		if (pos != INVALID_POSITION) {
			return getWinInfo(pos);
		}
		return null;
	}

	protected WinInfo getWinInfo(int index) {

		for (int i = 0; i < win_child.size(); i++) {
			if (index == win_child.get(i).item) {
				return win_child.get(i);
			}
		}
		return null;
	}

	@SuppressLint("InflateParams")
	protected View AddChildViewstandard(WinInfo wi) {

		RelativeLayout.LayoutParams param;
        final int i=wi.item;
		
		param = new RelativeLayout.LayoutParams(cell[i].right - cell[i].left, cell[i].bottom - cell[i].top);
		param.topMargin = cell[i].top;
		param.leftMargin = cell[i].left;

		cell_occupied[i] = true;
		wi.setWinCoordinate(cell[i].left, cell[i].top, cell[i].right - cell[i].left, cell[i].bottom - cell[i].top);

		View child = launcher.GetLauncherInflater().inflate(R.layout.child_item, null);
		wi.pageId = pageId;
		child.setTag(wi.item);
		ImageView ImView = (ImageView) child.findViewById(R.id.child_iv);
		TextView txView = (TextView) child.findViewById(R.id.child_tv);

		child.setClickable(false);
		if (wi.Packetname.equals(public_define.settingname)) {
			ImView.setImageDrawable(launcher.getResources().getDrawable(R.drawable.sz, launcher.getTheme()));
		} else {
			ImView.setImageDrawable(wi.icon);
		}
		txView.setText(wi.title);
		wi.color = color[i];
		child.setBackgroundColor(wi.color);
		addView(child, param);
		return child;
	}

	public void addCutomview(List<WinInfo> wi) {
		RelativeLayout.LayoutParams param;
		RelativeLayout child_v;

		for (int i = 0; i < wi.size(); i++) {
			child_v = new RelativeLayout(launcher);
			param = new RelativeLayout.LayoutParams(wi.get(i).right, wi.get(i).bottom);
			param.topMargin = wi.get(i).top;
			param.leftMargin = wi.get(i).left;
			wi.get(i).pageId = pageId;
			child_v.setTag(i);
			child_v.setClickable(false);
			child_v.setBackgroundColor(wi.get(i).color);
			addView(child_v, param);
			addcustomChildView(child_v, wi.get(i).child_view, wi.get(i).child_text);
		}
	}

	public void addcustomChildView(RelativeLayout m_rl, List<Postion> icon, List<Postion> text) {

		if (icon != null) {
			for (int i = 0; i < icon.size(); i++)
				set_Child_Icon(m_rl, icon.get(i));
		}
		if (text != null) {
			for (int i = 0; i < text.size(); i++)
				Set_Child_Text(m_rl, text.get(i));
		}
	}

	public void Set_Child_Text(RelativeLayout m_r, Postion text) {

		TextView tv = new TextView(launcher);
		tv.setText(text.buff);
		if (text.diplaymode == 0)
			tv.setGravity(Gravity.CENTER);
		else if (text.diplaymode == 1)
			tv.setGravity(Gravity.START);
		else if (text.diplaymode == 4)
			tv.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		tv.setClickable(false);
		if (text.size != 0)
			tv.setTextSize(text.size);
		tv.setTextColor(text.color);
		
		RelativeLayout.LayoutParams param;
		param = new RelativeLayout.LayoutParams(text.right, text.bottom);
		param.topMargin = text.top;
		param.leftMargin = text.left;
		
		m_r.addView(tv, param);
	}

	public void set_Child_Icon(RelativeLayout m_r, Postion icon) {

		ImageView btn = new ImageView(launcher);
		btn.setImageDrawable(icon.icon);
		RelativeLayout.LayoutParams param;
		param = new RelativeLayout.LayoutParams(icon.right, icon.bottom);
		param.topMargin = icon.top;
		param.leftMargin = icon.left;
		btn.setClickable(false);
		m_r.addView(btn,param);
	}
}
