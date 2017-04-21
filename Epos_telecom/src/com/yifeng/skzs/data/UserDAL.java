package com.yifeng.skzs.data;

import java.util.Map;

import org.json.JSONObject;

import android.content.Context;

import com.yifeng.skzs.entity.User;
import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.util.DataConvert;

public class UserDAL extends BaseDAL
{
	public UserDAL(Context context)
	{
		super(context);
	}

	/**
	 * 获取用户信息
	 * @param param
	 * @return
	 */
	public User loadUser(Map<String, String> param)
	{
		// this.setUrl(this.getIpconfig()+"login/login/doLogin");
		this.setUrl(this.getIpconfig() + "android/recruitmentcloud/login");

		String json = this.doGet(param);
		User user = new User();
		if (json.equals("error"))
		{
			user.setState(ConstantUtil.SERVER_ERROR);
		} else if (json.equals("FAIL"))
		{
			user.setState(ConstantUtil.LOGIN_FAIL);
		} else if (json.equals("FAIL1"))
		{
			user.setState(ConstantUtil.LOGIN_FAIL1);
		} else
		{
			try
			{
				Map<String, String> map = DataConvert.toMap(json);
				user.setState(ConstantUtil.LOGIN_SUCCESS);
				Boolean isSuccess = Boolean.parseBoolean(map.get("success"));
				if (isSuccess == true)
				{
					JSONObject vo = new JSONObject(map.get("user"));
					user.setUserId(vo.getString("acb260")==null?"":vo.getString("acb260"));
					user.setUserPwd(vo.getString("acb265")==null?"":vo.getString("acb265"));
					user.setUserName(vo.getString("acb264")==null?"":vo.getString("acb264"));
					user.setCompanyId(vo.getString("aab001"));
					user.setCompanyName(vo.getString("aab004"));
				}else if (isSuccess == false)
				{
					user.setState(ConstantUtil.DATA_NULL);
				} else
				{
					user.setState(ConstantUtil.INNER_ERROR);
				}

			} catch (Exception e)
			{
				user.setState(ConstantUtil.INNER_ERROR);// 解析异常
			}
		}
		return user;
	}
}
