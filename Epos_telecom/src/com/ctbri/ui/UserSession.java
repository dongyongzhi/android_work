package com.ctbri.ui;

import android.content.Context;
import android.content.SharedPreferences;

import com.yifeng.skzs.entity.User;


/**
 * 用户信息
 * @author WuJiaHong
 * 
 */
public class UserSession
{
	private Context context;
	public static final String USERINFO = "USERINFO";

	public UserSession(Context context)
	{
		this.context = context;
	}

	/**
	 * 昨时存用户
	 * @param user
	 */
	public void setUser(User user)
	{
		SharedPreferences settings = this.context.getSharedPreferences(USERINFO, 0);
		settings.edit().putInt("state", user.getState()).
		putString("imsi", user.getImsi()).
		putString("key", user.getKey()).
		putString("mobileNo", user.getMobileNo()).
		putString("publicKey", user.getPublicKey()).
		putString("userId", user.getUserId()).
		putString("userName", user.getUserName()).
		putString("userPwd", user.getUserPwd()).
		putBoolean("rememberPwd", user.isRememberPwd()).
		putString("companyId", user.getCompanyId()).
		putString("companyName", user.getCompanyName()).commit();
	}

	/**
	 * 获取用户信息
	 * @return
	 */
	public User getUser()
	{
		User user = new User();
		SharedPreferences userinfo = context.getSharedPreferences(USERINFO, 0);
		System.out.println("----------->" + userinfo.getString("userName", ""));
		user.setState(userinfo.getInt("state", 0));
		user.setPublicKey(userinfo.getString("imsi", ""));
		user.setKey(userinfo.getString("key", ""));
		user.setMobileNo(userinfo.getString("mobileNo", ""));
		user.setPublicKey(userinfo.getString("publicKey", ""));
		user.setUserId(userinfo.getString("userId", ""));
		user.setUserName(userinfo.getString("userName", ""));
		user.setUserPwd(userinfo.getString("userPwd", ""));
		user.setRememberPwd(userinfo.getBoolean("rememberPwd", false));
		user.setCompanyId(userinfo.getString("companyId", ""));
		user.setCompanyName(userinfo.getString("companyName", ""));
		return user;
	}
}
