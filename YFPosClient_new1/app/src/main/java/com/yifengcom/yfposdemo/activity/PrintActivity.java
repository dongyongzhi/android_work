package com.yifengcom.yfposdemo.activity;

import com.yifengcom.yfpos.model.PrintType;
import com.yifengcom.yfpos.print.Print;
import com.yifengcom.yfpos.print.PrintPackage;
import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 金卡通打印协议
 */
public class PrintActivity extends BaseActivity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print);
		setTitleName("打印数据");

		this.findViewById(R.id.btnPrint).setOnClickListener(this);
		this.findViewById(R.id.btnCancel).setOnClickListener(this);

		// 初始化刷卡器类

	}

	CallBackListener mCallBack = new CallBackListener() {
		@Override
		public void onError(final int errorCode, final String errorMessage)
				throws RemoteException {
			closeDialog();

			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(PrintActivity.this)
							.setTitle("提示")
							.setMessage(
									"操作失败，返回码：" + errorCode + " 信息:"
											+ errorMessage)
							.setPositiveButton("确定", null).show();
				}
			});

		}

		@Override
		public void onTimeout() throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(PrintActivity.this).setTitle("错误")
							.setMessage("打印超时").setPositiveButton("确定", null)
							.show();
				}
			});
		}

		@Override
		public void onResultSuccess(final int ntype) throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					showToast(PrintType.convert(ntype).getMesssage());
				}
			});
		}

		@Override
		public void onTradeCancel() throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(PrintActivity.this).setTitle("错误")
							.setMessage("取消打印").setPositiveButton("确定", null)
							.show();
				}
			});
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 取消
		case R.id.btnCancel:
			this.finish();
			break;

		// 打印
		case R.id.btnPrint:
			pd = new ProgressDialog(this);
			pd.setMessage("正在打印...");
			pd.setCancelable(true);
			pd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					try {
						YFApp.getApp().iService.cancel();
					} catch (RemoteException e) {
					}
				}
			});
			pd.show();

			try {
				if (!YFApp.getApp().isBind) {
					showToast("请先绑定");
					return;
				}
				pd.show();
				YFApp.getApp().iService.onPrint(getPrintBody(), mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				showToast("接口访问错误");
				closeDialog();
			}
			break;
		}
	}

	public byte[] getPrintBody() {
		byte[] sendbuf = new byte[1024];
		try {
			Print printinfor = new Print();
			printinfor.PRINT_clear();
			// --------增加图片序号1--------
			printinfor.PRINT_Add_picture((byte) 100, (byte) 1);
			// ---------增加走纸步进--------
			printinfor.PRINT_Add_setp((short) (100));
			// ----------增加字符信息0-------
			String print0 = "商户名称：";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print0.getBytes("gb2312"),
					(short) (print0.getBytes("gb2312").length));
			// ----------增加字符信息1-------
			String print1 = "江苏怡丰测试专用商户:";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_48X24,
					print1.getBytes("gb2312"),
					(short) (print1.getBytes("gb2312").length));

			// ----------增加字符信息2-------
			String print2 = "商户编号:801389670704798";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print2.getBytes("gb2312"),
					(short) (print2.getBytes("gb2312").length));

			// ----------增加字符信息3-------
			String print3 = "终端编号:20140923";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print3.getBytes("gb2312"),
					(short) (print3.getBytes("gb2312").length));

			// ----------增加字符信息4-------
			String print4 = "操作员:01";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print4.getBytes("gb2312"),
					(short) (print4.getBytes("gb2312").length));

			// ----------增加横线5-------
			String print5 = "--------------------------------";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print5.getBytes("gb2312"),
					(short) (print5.getBytes("gb2312").length));

			// ----------增加字符信息6-------
			String print6 = "发 卡 行：深证平安银行 04100000";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print6.getBytes("gb2312"),
					(short) (print6.getBytes("gb2312").length));
			// ----------增加字符信息7-------
			String print7 = "卡    号：";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print7.getBytes("gb2312"),
					(short) (print7.getBytes("gb2312").length));
			// ----------增加字符信息8-------
			String print8 = "6230 58** **** ***9 611 (C)";
			printinfor.PRINT_Add_character((byte) 10, Print.PNT_48X24,
					print8.getBytes("gb2312"),
					(short) (print8.getBytes("gb2312").length));

			// ----------增加字符信息9-------
			String print9 = "交易类型：";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print9.getBytes("gb2312"),
					(short) (print9.getBytes("gb2312").length));
			// ----------增加字符信息10-------
			String print10 = "消 费";
			printinfor.PRINT_Add_character((byte) 10, Print.PNT_48X24,
					print10.getBytes("gb2312"),
					(short) (print10.getBytes("gb2312").length));

			// ----------增加横线11-------
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print5.getBytes("gb2312"),
					(short) (print5.getBytes("gb2312").length));

			// ----------增加字符信息12-------
			String print12 = "批 次 号：000020";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print12.getBytes("gb2312"),
					(short) (print12.getBytes("gb2312").length));
			// ----------增加字符信息13-------
			String print13 = "凭 证 号：100004";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print13.getBytes("gb2312"),
					(short) (print13.getBytes("gb2312").length));
			// ----------增加字符信息14-------
			String print14 = "日期/时间：2016/04/19 11:59:34";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print14.getBytes("gb2312"),
					(short) (print14.getBytes("gb2312").length));
			// ----------增加字符信息15-------
			String print15 = "参 考 号：268381955837";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print15.getBytes("gb2312"),
					(short) (print15.getBytes("gb2312").length));
			// ----------增加字符信息16-------
			String print16 = "金    额：";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print16.getBytes("gb2312"),
					(short) (print16.getBytes("gb2312").length));
			// ----------增加字符信息17-------
			String print17 = "RMB  -0.01";
			printinfor.PRINT_Add_character((byte) 10, Print.PNT_48X24,
					print17.getBytes("gb2312"),
					(short) (print17.getBytes("gb2312").length));

			// ----------增加横线18-------
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
					print5.getBytes("gb2312"),
					(short) (print5.getBytes("gb2312").length));

			// ----------增加字符信息18-------
			String print18 = "备注：";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_16X16,
					print18.getBytes("gb2312"),
					(short) (print18.getBytes("gb2312").length));
			// ----------增加字符信息19-------
			String print19 = "电子现金金额：RMB 0.00";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_16X16,
					print19.getBytes("gb2312"),
					(short) (print19.getBytes("gb2312").length));
			// ----------增加字符信息20-------
			String print20 = "APP:V2.4.1";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_16X16,
					print20.getBytes("gb2312"),
					(short) (print20.getBytes("gb2312").length));
			// ----------增加字符信息21-------
			String print21 = "原凭证号：100002";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_16X16,
					print21.getBytes("gb2312"),
					(short) (print21.getBytes("gb2312").length));

			// ---------增加走纸步进--------
			printinfor.PRINT_Add_setp((short) (50));
			
			//----------增加二维码信息--------
//			String print22 = "本次消费352.00";
//			printinfor.PRINT_Add_2Dpicture((byte)20,print22.getBytes("gb2312"),(byte)print22.getBytes("gb2312").length);

			short sendLen = printinfor.PRINT_packages(sendbuf);
			byte[] sendbuf1 = new byte[sendLen];

			System.arraycopy(sendbuf, 0, sendbuf1, 0, sendLen);

			PrintPackage package1 = new PrintPackage(sendbuf1);
			if (package1 != null) {
				return package1.getPackData();
			}
		} catch (Exception e) {

		}
		return null;
	}

}
