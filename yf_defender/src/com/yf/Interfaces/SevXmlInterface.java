package com.yf.Interfaces;

import java.util.List;

import com.yf.define.YFShopAppInfo;


public interface SevXmlInterface {

	void downloadsuc(List<YFShopAppInfo> apps,boolean isUpdate);
    void downloadfailed(int err,String errmsg);
    void updateStoreUi();
    void deletepacket(String packname);
}