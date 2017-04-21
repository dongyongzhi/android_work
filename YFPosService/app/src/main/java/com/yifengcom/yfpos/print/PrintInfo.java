package com.yifengcom.yfpos.print;

import com.yifengcom.yfpos.model.syn.SynPackage;


public class PrintInfo extends SynPackage{

	private static final long serialVersionUID = -4377416559553534113L;
	
	public final static byte COM_PIC_INDEX=0x21;	// 01 PC 			图片序号
	public final static byte COM_ASCC=0x22; 	    // 00 ASCC          ASCC文字信息	   注意ASCC可以不使用协议，通过超时或下一个的命令判断
	public final static byte COM_QR_CODE=0x23;	    // 03  二维码直接打印
	public final static byte COM_STEPS=0x24;	  	// 02 steps	                步进
	
	public byte printMode;			    // 打印格式
	public byte printOffset;			// offset
	public byte CFontsize;				// 打印字体
	public short printLen;				// len
	public short printPtr;				// ptr
	
	public int setPrintPtr;
	public byte getPrintMode() {
		return printMode;
	}
	public void setPrintMode(byte printMode) {
		this.printMode = printMode;
	}
	public byte getPrintOffset() {
		return printOffset;
	}
	public void setPrintOffset(byte printOffset) {
		this.printOffset = printOffset;
	}
	public byte getCFontsize() {
		return CFontsize;
	}
	public void setCFontsize(byte cFontsize) {
		CFontsize = cFontsize;
	}
	public short getPrintLen() {
		return printLen;
	}
	public void setPrintLen(short printLen) {
		this.printLen = printLen;
	}
	public short getPrintPtr() {
		return printPtr;
	}
	public void setPrintPtr(short printPtr) {
		this.printPtr = printPtr;
	}
	
	@Override
	public byte[] encode() {
		return null;
	}
}
