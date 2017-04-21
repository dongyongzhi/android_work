package com.yf.defender;

import java.io.File;
import com.yf.define.PublicDefine;

import android.content.Context;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

public class InstallAppThread implements Runnable{

	private static final int APP_INSTALL_AUTO = 0;
//	private static final int APP_INSTALL_DEVICE = 1;
//  private static final int APP_INSTALL_SDCARD = 2;
	//private final  Object obj = new Object();
	private String filename,packname;
	private int  index;
	private Context context;
	private Handler handler;
	//private final static String TAG="InstallAppThread";

	@Override
	public void run() {
	     PmInstallApk(filename,packname);
	}
		
	public void SetDirAndPackname(String file, int i,String packageName,Context context,Handler handler) {
       this.filename=file;
       this.index=i;
       this.packname=packageName;
       this.context=context;
       this.handler=handler;
	}
	
	public void PmInstallApk(String fileName, String packageName) {

		final int selectedLocation = Settings.Global.getInt(context.getContentResolver(),
				Settings.Global.DEFAULT_INSTALL_LOCATION, APP_INSTALL_AUTO);

		if (APP_INSTALL_AUTO != selectedLocation) {
			Settings.Global.putInt(context.getContentResolver(), Settings.Global.DEFAULT_INSTALL_LOCATION, APP_INSTALL_AUTO);
		}
		Uri uri = Uri.fromFile(new File(fileName));
		int installFlags = 0;
		PackageManager pm = context.getPackageManager();

		try {
			PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			if (pi != null) {
				installFlags |= PackageManager.INSTALL_REPLACE_EXISTING;
			}
		} catch (NameNotFoundException e) {

		}
		MyPakcageInstallObserver observer = new MyPakcageInstallObserver();
		pm.installPackage(uri, observer, installFlags, packageName);

	}
	public class MyPakcageInstallObserver extends IPackageInstallObserver.Stub {

		@Override
		public void packageInstalled(String packageName, int returnCode) {
	
			if (returnCode == 1) {
				handler.obtainMessage(PublicDefine.SHOWINSTALLINFO,index,returnCode," 安装成功").sendToTarget();
			} else {
				handler.obtainMessage(PublicDefine.SHOWINSTALLINFO,index,returnCode," 安装失败:" + checkInstallResult(returnCode))
						.sendToTarget();
			}	
		}
	}
	
	private String checkInstallResult(int returnCode) {
		String result;

		switch (returnCode) {
		case PackageManager.INSTALL_SUCCEEDED: // 1
			result = "安装成功";
			break;
		case PackageManager.INSTALL_FAILED_ALREADY_EXISTS: // -1
			result = "程序已经存在";
			break;
		case PackageManager.INSTALL_FAILED_INVALID_APK: // -2
			result = "无效的APK";
			break;
		case PackageManager.INSTALL_FAILED_INVALID_URI: // -3
			result = "无效的链接";
			break;
		case PackageManager.INSTALL_FAILED_INSUFFICIENT_STORAGE: // -4
			result = "没有足够的存储空间 ";
			break;
		case PackageManager.INSTALL_FAILED_DUPLICATE_PACKAGE: // -5
			result = "已存在同名程序 ";
			break;
		case PackageManager.INSTALL_FAILED_NO_SHARED_USER: // -6
			result = "共享用户不存在";
			break;
		case PackageManager.INSTALL_FAILED_UPDATE_INCOMPATIBLE: // -7
			result = "更新不兼容 ";
			break;
		case PackageManager.INSTALL_FAILED_SHARED_USER_INCOMPATIBLE: // -8
			result = "共享用户不兼容 ";
			break;
		case PackageManager.INSTALL_FAILED_MISSING_SHARED_LIBRARY: // -9
			result = "共享库已丢失 ";
			break;
		case PackageManager.INSTALL_FAILED_REPLACE_COULDNT_DELETE: // -10
			result = "替换时无法删除 ";
			break;
		case PackageManager.INSTALL_FAILED_DEXOPT: // -11
			result = "空间不足或验证失败  ";
			break;
		case PackageManager.INSTALL_FAILED_OLDER_SDK: // -12
			result = "系统版本过旧 ";
			break;
		case PackageManager.INSTALL_FAILED_CONFLICTING_PROVIDER: // -13
			result = "存在同名的内容提供者 ";
			break;
		case PackageManager.INSTALL_FAILED_NEWER_SDK: // -14
			result = "系统版本过新 ";
			break;
		case PackageManager.INSTALL_FAILED_TEST_ONLY: // -15
			result = "调用者不被允许测试的测试程序 ";
			break;
		case PackageManager.INSTALL_FAILED_CPU_ABI_INCOMPATIBLE: // -16
			result = "包含的本机代码不兼容CPU_ABI";
			break;
		case PackageManager.INSTALL_FAILED_MISSING_FEATURE: // -17
			result = "使用了一个无效的特性 ";
			break;
		case PackageManager.INSTALL_FAILED_CONTAINER_ERROR: // -18
			result = "SD卡访问失败 ";
			break;
		case PackageManager.INSTALL_FAILED_INVALID_INSTALL_LOCATION: // -19
			result = "无效的安装路径";
			break;
		case PackageManager.INSTALL_FAILED_MEDIA_UNAVAILABLE: // -20
			result = "SD卡不可用 ";
			break;
		case PackageManager.INSTALL_FAILED_VERIFICATION_TIMEOUT:// -21
			result = " 验证超时 ";
			break;
		case PackageManager.INSTALL_FAILED_PACKAGE_CHANGED: // -22
			result = " 预期的应用被改变 ";
			break;
		case PackageManager.INSTALL_PARSE_FAILED_NOT_APK: // -100
			result = "解析失败，不是APK";
			break;
		case PackageManager.INSTALL_PARSE_FAILED_BAD_MANIFEST: // -101
			result = "解析失败，无法提取Manifest";
			break;
		case PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION: // -102
			result = " 解析失败，无法预期的异常";
			break;
		case PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES: // -103
			result = "解析失败，找不到证书 ";
			break;
		case PackageManager.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES: // -104
			result = "解析失败，证书不一致 ";
			break;
		case PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING: // -105
			result = "解析失败，证书编码异常 ";
			break;
		case PackageManager.INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME: // -106
			result = " 解析失败，manifest中的包名错误或丢失 ";
			break;
		case PackageManager.INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID: // -107
			result = "解析失败，manifest中的共享用户错误 ";
			break;
		case PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED: // -108
			result = "解析失败，manifest中出现结构性错误 ";
			break;
		case PackageManager.INSTALL_PARSE_FAILED_MANIFEST_EMPTY: // -109
			result = " 解析失败，manifest中没有actionable tags ";
			break;
		case PackageManager.INSTALL_FAILED_INTERNAL_ERROR: // -110
			result = "系统问题导致安装失败 ";
			break;
		default:
			result = "安装失败，无法预知的错误";
			break;
		}
		return result;
	}
	
}
