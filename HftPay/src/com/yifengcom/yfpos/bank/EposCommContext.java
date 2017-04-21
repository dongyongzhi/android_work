package com.yifengcom.yfpos.bank;

public class EposCommContext {
	private byte[] rand = new byte[4];
	private byte seq = -1;
	
	public byte[] getRand() {
		return rand;
	}
	
	public void setRand(byte[] rand) {
		this.rand = rand;
	}
	
	public byte getSeq() {
		return seq;
	}
	public void setSeq(int b) {
		 seq=(byte)b;
	}
	//public void incSeq(){
	//	this.seq = (byte) (((this.seq&0xFF) + 1)&0xff);
	//}
}
