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
import android.view.View.MeasureSpec;
import android.widget.ImageButton;
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

	/*
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}*/
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
			Log.e(TAG, "ID=" + win.id + " title=" + win.title + " package=" + win.Packetname + " item=" + win.id
					+ " pager=" + win.pageId);
		}
	}

	protected void removeChild(int dropid) {
		printChildInfo("removeChild 1 first");
		View view = getchildView(dropid);
		removeView(view);
		remove(dropid);
		printChildInfo("removeChild 2 second");
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
			if (win_child.get(i).id == dropid) {
				win_child.remove(i);
				cell_occupied[dropid] = false;
			}
		}
	}

	protected boolean add(WinInfo mWin) {
		return win_child.add(mWin);
	}

	protected void AddchildtoIdle(WinInfo winInfo, int item) {
		AddChildViewstandard(winInfo, item);
		win_child.add(winInfo);
		printChildInfo("AddchildtoIdle");
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
		// printWinInfo();

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
			AddChildViewstandard(wi.get(i), i);
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
			if (index == win_child.get(i).id) {
				return win_child.get(i);
			}
		}
		return null;
	}

	@SuppressLint("InflateParams")
	protected View AddChildViewstandard(WinInfo wi, int i) {

		RelativeLayout.LayoutParams param;

		param = new RelativeLayout.LayoutParams(cell[i].right - cell[i].left, cell[i].bottom - cell[i].top);
		param.topMargin = cell[i].top;
		param.leftMargin = cell[i].left;

		cell_occupied[i] = true;
		wi.setWinCoordinate(cell[i].left, cell[i].top, cell[i].right - cell[i].left, cell[i].bottom - cell[i].top);

		View child = launcher.GetLauncherInflater().inflate(R.layout.child_item, null);
		wi.id = i;
		wi.pageId = pageId;
		child.setTag(wi.id);
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
			wi.get(i).id = i;
			wi.get(i).pageId = pageId;
			child_v.setTag(i);
			child_v.setClickable(false);
			child_v.setBackgroundColor(wi.get(i).color);
			addView(child_v, param);
			addcustomChildView(child_v, wi.get(i).child_view, wi.get(i).child_text);
		}
	}

	public void addcustomChildView(RelativeLayout m_rl, List<Postion> icon, List<Postion> text) {

		RelativeLayout.LayoutParams param;
		param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		if (icon != null) {
			for (int i = 0; i < icon.size(); i++)
				set_Child_Icon(m_rl, param, icon.get(i));
		}
		if (text != null) {
			for (int i = 0; i < text.size(); i++)
				Set_Child_Text(m_rl, param, text.get(i));
		}
	}

	public void Set_Child_Text(Object m_r, Object m_parm, Postion text) {

		RelativeLayout m_rl = (RelativeLayout) m_r;
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) m_parm;
		TextView tv = new TextView(launcher);
		tv.setPadding(text.left, text.top, text.right, text.bottom);
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
		m_rl.addView(tv, param);
	}

	public void set_Child_Icon(Object m_r, Object m_parm, Postion icon) {

		RelativeLayout m_rl = (RelativeLayout) m_r;
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) m_parm;
		ImageButton btn;
		btn = new ImageButton(launcher);
		btn.setPadding(icon.left, icon.top, icon.right, icon.bottom);
		btn.setBackgroundColor(0x00000000);

		if (icon.buff.equals("shaoyishao.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.shaoyishao, launcher.getTheme()));
		else if (icon.buff.equals("fukuan.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.fukuan, launcher.getTheme()));
		else if (icon.buff.equals("shuaiyishua.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.shuayishua, launcher.getTheme()));
		else if (icon.buff.equals("huiyuanka.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.huiyuanka, launcher.getTheme()));
		else if (icon.buff.equals("licai.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.licai, launcher.getTheme()));
		else if (icon.buff.equals("syt.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.syt, launcher.getTheme()));
		else if (icon.buff.equals("tuangou.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.tuangou, launcher.getTheme()));
		else if (icon.buff.equals("kajuan.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.kajuan, launcher.getTheme()));
		else if (icon.buff.equals("bq.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.bq, launcher.getTheme()));
		else if (icon.buff.equals("ppsh.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.ppsh, launcher.getTheme()));
		else if (icon.buff.equals("huiming.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.huiming, launcher.getTheme()));
		} else if (icon.buff.equals("kdb.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.kdb, launcher.getTheme()));
		} else if (icon.buff.equals("hsy.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.hsy, launcher.getTheme()));
		} else if (icon.buff.equals("fc.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.fc, launcher.getTheme()));
		} else if (icon.buff.equals("hjy.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.hjy, launcher.getTheme()));
		} else if (icon.buff.equals("hfthuiyuanka.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.hfthuiyuanka, launcher.getTheme()));
		} else if (icon.buff.equals("busnisshall.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.busnisshall, launcher.getTheme()));
		}
		btn.setClickable(false);
		m_rl.addView(btn, param);
	}
}
