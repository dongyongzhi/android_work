package com.yifengcom.yfpos.model.syn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import com.yifengcom.yfpos.model.ReadCardModel;
import com.yifengcom.yfpos.utils.ByteUtils;
import com.yifengcom.yfpos.utils.StringUtils;

public class OpenReadCard extends SynPackage implements Serializable {

	private static final long serialVersionUID = 3317529659930004567L;

	private ReadCardModel readCardModel = ReadCardModel.ALL;

	private byte[] activeCode; // 业务激活码

	private boolean supportInputCardNumber = false; // 手输卡号标识
	private int timeout = 20; // 操作超时时间秒
	private TrxType trxType = TrxType.QUERY; // 交易类型
	private double trxMoney = 0.0d; // 交易金额
	private byte[] customData; // 自定义数据
	private byte trxDisplayModel = 0x00;
	private String customDisplay; // 自定义显示内容

	@Override
	public byte[] encode() {
		ByteArrayOutputStream in = new ByteArrayOutputStream(100);
		try {
			//读卡模式
			in.write(readCardModel.getValue());
			//业务激活码 BCD8
			byte[] code = new byte[8];
			if(this.activeCode!=null) {
				System.arraycopy(activeCode, 0, code, 0, activeCode.length<8 ? activeCode.length : 8);
			}
			in.write(code);
			//手输卡号标识
			in.write(this.supportInputCardNumber ? 0x01 : 0x00);
			//操作超时时间
			in.write(this.timeout);
			//交易类型
			byte[] trxTypeBytes  = this.trxType.getMesssage().getBytes(CHARSET);
			if(trxTypeBytes.length<10) {
				in.write(trxTypeBytes);
				//补空格
				for(int i=0;i<10-(trxTypeBytes.length);i++) {
					in.write(SPACE);
				}
			} else {
				in.write(trxTypeBytes,0,10);
			}
			
			//交易金额 BCD6
			//金额转换成分
			long money = ((Double)(trxMoney * 100)).longValue();
			in.write(ByteUtils.hexToByte(StringUtils.leftAddZero(money,12)));
			
			//自定义数据
			if(this.customData==null) {
				in.write(0);
			} else {
				in.write(this.customData.length);
				if(this.customData.length > 0xFF) {
					in.write(this.customData,0,0xFF);
				}else {
					in.write(this.customData);
				}
			}
			//交易显示模式
			in.write(this.trxDisplayModel);
			//自定义显示内容
			if(StringUtils.isEmpty(this.customDisplay)) {
				in.write(0);
			} else {
				byte[] customDisplayData = this.customDisplay.getBytes(CHARSET);
				if(customDisplayData.length > 0xFF) {
					in.write(customDisplayData,0,0xFF);
				}else {
					in.write(customDisplayData);
				}
			}
			
			///结果提示方式
			in.write(0x01);
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in.toByteArray();
	}

	public ReadCardModel getReadCardModel() {
		return readCardModel;
	}

	public void setReadCardModel(ReadCardModel readCardModel) {
		this.readCardModel = readCardModel;
	}

	public byte[] getActiveCode() {
		return activeCode;
	}

	public void setActiveCode(byte[] activeCode) {
		this.activeCode = activeCode;
	}

	public boolean isSupportInputCardNumber() {
		return supportInputCardNumber;
	}

	public void setSupportInputCardNumber(boolean supportInputCardNumber) {
		this.supportInputCardNumber = supportInputCardNumber;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public TrxType getTrxType() {
		return trxType;
	}

	public void setTrxType(TrxType trxType) {
		this.trxType = trxType;
	}

	public double getTrxMoney() {
		return trxMoney;
	}

	public void setTrxMoney(double trxMoney) {
		this.trxMoney = trxMoney;
	}

	public byte[] getCustomData() {
		return customData;
	}

	public void setCustomData(byte[] customData) {
		this.customData = customData;
	}

	public byte getTrxDisplayModel() {
		return trxDisplayModel;
	}

	public void setTrxDisplayModel(byte trxDisplayModel) {
		this.trxDisplayModel = trxDisplayModel;
	}

	public String getCustomDisplay() {
		return customDisplay;
	}

	public void setCustomDisplay(String customDisplay) {
		this.customDisplay = customDisplay;
	}

}
