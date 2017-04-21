package com.ctbri.biz;

import com.ctbri.domain.CheckDownloadResponse;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.ElectronicSignResponse;
import com.ctbri.domain.LoginResponse;
import com.ctbri.domain.MessageSendRequest;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.POSInfoResponse;
import com.ctbri.domain.PrintResponse;
import com.ctbri.domain.QueryNoticeRequest;
import com.ctbri.domain.QueryNoticeResponse;
import com.ctbri.pos.ElecPosService;

/**
 * 手机 和 pos 管理类<br/>
 * 2013.02.26 增加信息发送接口
 * 
 * @author qin
 * 
 *         2012-12-8
 */
public interface ManagerService {

	/**
	 * 版本更新检查接口
	 * 
	 * @param versiontype
	 * @param softtype
	 * @param poscati
	 *            POS终端号
	 * @param versionno
	 * @return
	 */
	CheckDownloadResponse checkDownload(String versiontype, String softtype,
			String poscati, String versionno);

	/**
	 * 查询商户号和终端号
	 * 
	 * @param poscati
	 *            终端号
	 * @return
	 */
	POSInfoResponse queryPosInfo(String poscati);

	/**
	 * 操作员登陆
	 * 
	 * @param operator
	 *            操作员
	 * @param poscati
	 *            终端号
	 * @param imsi
	 *            Imsi号
	 * @param deviceid
	 *            Pad序列号
	 * @return
	 */
	LoginResponse login(String operator, String poscati, String imsi,
			String deviceid);

	/**
	 * 上传电子签名
	 * 
	 * <li>上传内容和返回内容未加密</li>
	 * 
	 * @param orderNumber
	 *            订单号
	 * @param data
	 *            签名数据内容(jpg图片格式)
	 * @return
	 */
	ElectronicSignResponse electronicSign(String orderNumber, byte[] data);

	/**
	 * 打印最后一笔交易
	 * 
	 * @param pos
	 *            pos终端
	 * @return
	 */
	PrintResponse printLastTrans(ElecPosService pos);

	/***
	 * 获取公告
	 * 
	 * @param posCati
	 *            终端号
	 * @param customerNumber
	 *            商户号
	 * @param noticeType
	 *            公告类型
	 * @return
	 */
	QueryNoticeResponse queryNotice(QueryNoticeRequest request);

	/**
	 * pos终端 向 posp 更新参数
	 * @param posInfo 终端信息
	 * @return
	 */
	ElecResponse updateParameter(POSInfo posInfo,ElecPosService pos);
	
	/**
	 * 下发信息
	 * @param req  
	 * @return
	 */
	ElecResponse messageSend(MessageSendRequest req);
}
