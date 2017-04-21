package com.yifengcom.yfpos.utils;

import com.yifengcom.yfpos.R;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 密码键盘弹窗
 */
public class WindowUtils {

    private static final String LOG_TAG = "WindowUtils";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;

    public static Boolean isShown = false;

    /**
     * 显示弹出框
     * @param context
     */
    public static void showPopupWindow(final Context context,byte[] key) {
        if (isShown) {
            Log.i(LOG_TAG, "return cause already shown");
            return;
        }

        isShown = true;
        Log.i(LOG_TAG, "showPopupWindow");

        // 获取应用的Context
        mContext = context.getApplicationContext();
        // 获取WindowManager
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mView = setUpView(context,key);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        // 类型
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
 
        // 设置flag
        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
        // 不设置这个flag的话，home页的划屏会有问题
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        params.x = 0; 
        params.y = 0;
        mWindowManager.addView(mView, params);
        Log.i(LOG_TAG, "add view");
    }

    /**
     * 隐藏弹出框
     */
    public static void hidePopupWindow() {
        Log.i(LOG_TAG, "hide " + isShown + ", " + mView);
        if (isShown && null != mView) {
            Log.i(LOG_TAG, "hidePopupWindow");
            mWindowManager.removeView(mView);
            isShown = false;
            txt_pwd = null;
            sb.setLength(0);
        }

    }
    
    private static TextView txt_pwd;
    public static StringBuilder sb = new StringBuilder();

    private static View setUpView(final Context context,byte[] key) {
        Log.i(LOG_TAG, "setUp view");
        View view = LayoutInflater.from(context).inflate(R.layout.pop_key_pad , null);
        txt_pwd = (TextView)view.findViewById(R.id.txt_pwd);
        Button button;
		for (int j = 0; j < key.length; j++) {
			switch (j) {
			case 0:
				button = (Button) view.findViewById(R.id.btn_1);
				button.setText(key[j] +"");
				break;
			case 1:
				button = (Button) view.findViewById(R.id.btn_2);
				button.setText(key[j] +"");
				break;
			case 2:
				button = (Button) view.findViewById(R.id.btn_3);
				button.setText(key[j] +"");
				break;
			case 3:
				button = (Button) view.findViewById(R.id.btn_4);
				button.setText(key[j] +"");
				break;
			case 4:
				button = (Button) view.findViewById(R.id.btn_5);
				button.setText(key[j] +"");
				break;
			case 5:
				button = (Button) view.findViewById(R.id.btn_6);
				button.setText(key[j] +"");
				break;
			case 6:
				button = (Button) view.findViewById(R.id.btn_7);
				button.setText(key[j] +"");
				break;
			case 7:
				button = (Button) view.findViewById(R.id.btn_8);
				button.setText(key[j] +"");
				break;
			case 8:
				button = (Button) view.findViewById(R.id.btn_9);
				button.setText(key[j] +"");
				break;
			case 9:
				button = (Button) view.findViewById(R.id.btn_0);
				button.setText(key[j] +"");
				break;
			default:
				break;
			}

		}
        return view;
    }
    
    public static void setPwd(int cmd){
		if(cmd == 0x01){
			sb.append("*");
		}else{
			sb.setLength(sb.length()-1);
		}
		txt_pwd.setText(sb);
	}
}