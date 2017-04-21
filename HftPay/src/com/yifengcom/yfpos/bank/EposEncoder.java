package com.yifengcom.yfpos.bank;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.mfs.util.CalculateCheckCode;
import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import android.util.Log;

import com.yifengcom.yfpos.bank.mina.EposContext;


public class EposEncoder implements ProtocolEncoder {
	private final String TAG = this.getClass().getName();
	

	private String packetFlag="46534B574C504F53";						//0报文标识，8byte
	private String termFlag="30324230313031343530303130353736";			//终端标识,16byte
	@Override
	public void dispose(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws IOException{
		if (message instanceof EposClientPacket) {
			EposClientPacket msg = (EposClientPacket) message;
			
			IoBuffer data = msg.encodeToStream();
			
			EposContext context = EposContext.getContext(session);
			EposCommContext CommCtx=null;
			if (context != null)
				CommCtx = context.getCommContext();
			
			IoBuffer buffer = IoBuffer.allocate(1024);
			buffer.clear();
			
			int packetLen = data.remaining()+24;
			String sPacketLen = String.format("%04d", packetLen);
			byte tmp[] = sPacketLen.getBytes("GB2312");
			buffer.put(tmp);
			
			tmp = StringUtil.hexStringToByte(this.packetFlag);
			buffer.put(tmp);
			
			tmp = StringUtil.hexStringToByte(this.termFlag);
			buffer.put(tmp);
			
			buffer.put(data);
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String a = formatter.format(new Date());
			buffer.flip();
			
			
			buffer.mark();
			tmp = new byte[buffer.remaining()];
			buffer.get(tmp);
			Log.i(TAG, a+" write:"+ StringUtil.bytesToHexString(tmp));
			buffer.reset();
			
			out.write(buffer);
		} 
	}
		
	
	public String getPacketFlag() {
		return packetFlag;
	}
	
	//packetFlag 为hex字符串
	public void setPacketFlag(String packetFlag) {
		this.packetFlag = packetFlag;
	}

	public String getTermFlag() {
		return termFlag;
	}

	//termFlag 为hex字符串
	public void setTermFlag(String termFlag) {
		this.termFlag = termFlag;
	}
}
