package com.ctbri.net.yeepay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ctbri.ElecException;
import com.ctbri.biz.ManagerService;
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
import com.ctbri.domain.QueryNoticeResponse.Notice;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.TransResponse;
import com.ctbri.net.POSPSetting;
import com.ctbri.net.yeepay.tms.TMSDownResponse;
import com.ctbri.net.yeepay.tms.TMSResponse;
import com.ctbri.pos.ElecPosService;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.HttpClientUtil;
import com.yifeng.iso8583.StringUtils;

/**
 * POS管理.
 * @author Administrator
 */
public class MPOSPManagerService implements ManagerService {

    /**
	 * posp 平台
	 */
    private final TransAction posp = TransAction.getInstance();

	/** 
	 * 检查新版本. 
	 * @return 返回下载地址
	 */
    @Override
	public CheckDownloadResponse checkDownload(String versiontype,
			String softtype, String poscati, String versionno) {
		// 组织参数
		JSONObject paydata = new JSONObject();
		try {
			paydata.put("poscati", poscati);
			paydata.put("versionno", versionno);
			paydata.put("versiontype", versiontype);
			paydata.put("softtype", softtype);
		} catch (JSONException e) {
			ElecLog.e(getClass(), e.getMessage(), e);
		}

		// 调用 posp
		ElecResponse resp = posp.doAction(POSPSetting.METHOD_CHECKDOWNLOAD,
				paydata, POSPSetting.generateKey());
		if (resp.getErrCode() != 0) {
			return ElecResponse.getErrorResponse(CheckDownloadResponse.class,
					resp);
		}

		// 返回结果
		CheckDownloadResponse result = new CheckDownloadResponse();
		MPOSPResponse mp = (MPOSPResponse) resp;
		try {
			result.setUrl(mp.getResult().getString("url"));
		} catch (JSONException e) {
			ElecLog.e(MPOSPOrderService.class, e.getMessage());
			throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
		}
		return result;
	}

	/** 查询pos信息 */
	@Override
	public POSInfoResponse queryPosInfo(String poscati) {
		// 调用 posp
		ElecResponse resp = posp.doAction(POSPSetting.METHOD_QUERYPOSINFO,
				"{\"Poscati\":\""+poscati+"\"}", POSPSetting.generateKey());
		if (resp.getErrCode() != 0) {
			return ElecResponse.getErrorResponse(POSInfoResponse.class, resp);
		}
		// 取出返回信息
		POSInfoResponse result = new POSInfoResponse();
		MPOSPResponse mp = (MPOSPResponse) resp;
		try {
			result.setCustomerName(mp.getResult().getString("customername"));
			result.setCustomerNumber(mp.getResult().getString("customerNumber"));
			result.setSerialNumber(mp.getResult().getString("serialnumber"));

		} catch (JSONException e) {
			ElecLog.e(MPOSPOrderService.class, e.getMessage());
			throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
		}

		return result;
	}

	/** 登录 */
	@Override
	public LoginResponse login(String operator, String poscati, String imsi,
			String deviceid) {
		// 组织参数
		JSONObject paydata = new JSONObject();
		try {
			paydata.put("operator", operator);
			paydata.put("poscati", poscati);
			paydata.put("imsi", imsi);
			paydata.put("deviceid", deviceid);
		} catch (JSONException e) {
			ElecLog.e(getClass(), e.getMessage(), e);
		}

		// 调用 posp
		ElecResponse resp = posp.doAction(POSPSetting.METHOD_LOGIN, paydata,
				POSPSetting.generateKey());
		if (resp.getErrCode() != 0) {
			return ElecResponse.getErrorResponse(LoginResponse.class, resp);
		}

		// 返回结果
		LoginResponse result = new LoginResponse();
		MPOSPResponse mp = (MPOSPResponse) resp;
		try {
			result.setIslogin(mp.getResult().getInt("islogin")==1 ? true : false);
		} catch (JSONException e) {
			ElecLog.e(MPOSPOrderService.class, e.getMessage());
			throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
		}
		return result;
	}

	/** 上传电子签名 */
	public ElectronicSignResponse electronicSign(String orderNumber, byte[] data) {
		// 参数
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("customerOrderNumber", orderNumber); // 订单号

		// 直接上传下载数据，不加密
		String response = HttpClientUtil.sendPostRequest(
				POSPSetting.METHOD_ELECTRONICSIGN, params,
				"cutomerOrderNumber", "cutomerOrderNumber.jpg", data);
		if (response == null || "".equals(response)) {
			throw new ElecException(POSPSetting.POSP_NET_BAD);// 服务器返回数据异常 为NULL  或者 空字符串
		}
		
		try {
			JSONObject result = new JSONObject(response);

			if (result.getInt(TransAction.KEY_ERRCODE) != 0) { // 平台返回错误信息
				return ElecResponse.getErrorResponse(
						ElectronicSignResponse.class,
						result.getInt(TransAction.KEY_ERRCODE),
						result.getString("errormsg"));
			}
			// 取出数据
			ElectronicSignResponse resp = new ElectronicSignResponse();
			resp.setValidateurl(result.getString("validateurl"));
			resp.setPageUrl(result.getString("pageurl"));
			return resp;

		} catch (JSONException e) {
			throw new ElecException(POSPSetting.POSP_NET_BAD);
		}
	}

	@Override
	public PrintResponse printLastTrans(ElecPosService pos) {
		if (pos == null) {
			return ElecResponse.getErrorResponse(PrintResponse.class, 1,
					"pos终端未找到！");
		}
		return pos.printLastTrans();
	}

	/** 查询公告---获取广告图片地址 */
	public QueryNoticeResponse queryNotice(QueryNoticeRequest request) {
		// 调用 posp
		ElecResponse resp = posp.doAction(POSPSetting.METHOD_QUERYNOTICE,
				request, POSPSetting.generateKey());
		if (resp.getErrCode() != 0) {
			return ElecResponse.getErrorResponse(QueryNoticeResponse.class,
					resp.getErrCode(), resp.getErrMsg());
		}
		// 解析数据
		try {
			QueryNoticeResponse response = new QueryNoticeResponse();

			JSONArray arr = ((MPOSPResponse) resp).getResult().getJSONArray(
					"noticeParamList");
			response.setNoticInfo(arr.toString());

			int len = arr.length();
			if (len > 0) {
				Notice[] notices = new Notice[len];
				JSONObject json = null;
				Notice notice = null;
				for (int i = 0; i < len; i++) {
					json = arr.getJSONObject(i);
					notice = new Notice();
					notice.setNoticeInfo(json.getString("noticeContent"));
					notice.setType(json.getString("noticeType"));
					notices[i] = notice;
				}
				response.setNotices(notices);
			}
			return response;

		} catch (JSONException e) {
			e.printStackTrace();
			return ElecResponse.getErrorResponse(QueryNoticeResponse.class, 1,
					POSPSetting.POSP_NET_BAD);
		}
	}

	@Override
	public ElecResponse updateParameter(POSInfo posInfo, ElecPosService pos) {
		ElecLog.d(getClass(), "===========更新参数==========");

		String key = POSPSetting.generateKey(); // 生成密钥

		// ====每一步请求下载====
		byte[] reqData = MPOSPTMSHelper.requestInitTMS(posInfo);
		if (reqData == null) {
			return ElecResponse.getErrorResponse(ResponseCode.TERM_NOT_FOUND);// 终端未找到
		}

		ElecResponse response = posp.doAction(
				POSPSetting.METHOD_UPDATEPARAMETER,
				StringUtils.byteToHex(reqData), key); // 参数更新
		if (response.getErrCode() != 0) { // 出错直接返回
			return response;
		}

		// 取出数据
		try {
			byte[] data = StringUtils.hexToBytes(((MPOSPResponse) response)
					.getResult().getString(POSPSetting.KYE_DATA));
			// 解析返回数据
			TMSResponse tmsResponse = MPOSPTMSHelper.responseInitTMS(data);
			if (tmsResponse.getErrCode() != 0) { // 出错直接返回
				return ElecResponse.getErrorResponse(ElecResponse.class,
						tmsResponse.getErrCode(), tmsResponse.getErrMsg());
			}

			// 有更新信息
			if (tmsResponse.getParamTaskId() != null
					&& !"".equals(tmsResponse.getParamTaskId())) {
				int offset = 0; // 偏移量
				int totalLen = Integer.parseInt(tmsResponse.getParamLength()); // 数据总大小

				ByteArrayOutputStream buf = new ByteArrayOutputStream();

				while (offset < totalLen) {
					// 请求并开始下载
					data = MPOSPTMSHelper.requestDownTMS(posInfo, tmsResponse,
							MPOSPTMSHelper.UPDATEPARAMETER, offset);
					response = posp.doAction(
							POSPSetting.METHOD_UPDATEPARAMETER,
							StringUtils.byteToHex(data), key);
					if (response.getErrCode() != 0) { // 出错直接返回
						return response;
					}

					// 取出数据
					data = StringUtils.hexToBytes(((MPOSPResponse) response)
							.getResult().getString(POSPSetting.KYE_DATA));
					// 解析数据
					TMSDownResponse downResponse = MPOSPTMSHelper
							.responseDownTMS(data);
					if (downResponse.getErrCode() != 0) {
						return downResponse;
					}
					buf.write(downResponse.getData());

					offset += downResponse.getLength();
				}

				// 下载完成应答
				data = MPOSPTMSHelper.requestCompleteTMS(posInfo, tmsResponse);
				response = posp.doAction(POSPSetting.METHOD_UPDATEPARAMETER,
						StringUtils.byteToHex(data), key);
				if (response.getErrCode() != 0) {
					return response;
				}
				// 应答
				data = StringUtils.hexToBytes(((MPOSPResponse) response)
						.getResult().getString(POSPSetting.KYE_DATA));
				response = MPOSPTMSHelper.responseCompleteTMS(data);

				ResponseCode code = pos.writePOSParams(buf.toByteArray());
				// 发起向pos机更新参数
				// POSInfo newPOSInfo = MPOSPTMSHelper.parse(buf.toByteArray());
				// ResponseCode code = pos.writePOSParams(newPOSInfo);
				if (code != ResponseCode.SUCCESS) {
					return ElecResponse.getErrorResponse(code);
				} else {
					return new ElecResponse();
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
			ElecLog.e(getClass(), e.getMessage(), e);
			return ElecResponse.getErrorResponse(TransResponse.class, 1,
					POSPSetting.POSP_CLIENT_FAIL);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ElecResponse.getErrorResponse(ElecResponse.class, 1, "更新参数失败");
	}

	/**信息发送*/
	@Override
	public ElecResponse messageSend(MessageSendRequest request) {
		ElecLog.d(getClass(), "===========信息发送==========");
		// 调用 posp 接口并返回
		return posp.doAction(POSPSetting.METHOD_MESSAGESEND,request, POSPSetting.generateKey());
	}
}
