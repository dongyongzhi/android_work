package com.winksoft.yzsmk.public_define;

import java.util.List;

public interface ReCreateWin {

	public void recreateWin(List<AppInfo> appInfo);
    
	public void UpdateAPP_I();
	
	public void swapAppInfo(AppInfo src,AppInfo des);
}