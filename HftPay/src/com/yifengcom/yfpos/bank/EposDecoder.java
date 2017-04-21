package com.yifengcom.yfpos.bank;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.mfs.util.CalculateCheckCode;
import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import android.util.Log;

import com.yifengcom.yfpos.bank.mina.EposContext;

public class EposDecoder extends CumulativeProtocolDecoder  {
	private final String TAG = this.getClass().getName();
	private MessageDecoderResult isValidPacketStan(IoSession session, IoBuffer in){
		if (in.remaining()< 4)
			return MessageDecoderResult.NEED_DATA;
		in.mark();
		//取出缓冲区数据
		byte[] tmp = new byte[in.remaining()];
		in.get(tmp);
		Log.i(TAG,"read: " + StringUtil.bytesToHexString(tmp));
		in.reset();  //反转缓冲区
		
		EposContext context = EposContext.getContext(session);
		int packetLen = (tmp[0]&0x0F)*1000 + (tmp[1]&0x0F)*100 + (tmp[2]&0x0F)*10 + (tmp[3]&0x0F);
		if (packetLen > 1024){
			Log.e(TAG,this.getClass().getName()+ "isValidPacket(): 交易报文长度超出处理范围！");
			return MessageDecoderResult.NOT_OK;
		}
		if (packetLen+4<=in.remaining()){
			return MessageDecoderResult.OK; 
		}else{
			Log.i(TAG,this.getClass().getName()+ "isValidPacket(): 需要更多数据！");
			return MessageDecoderResult.NEED_DATA;
		}
			
	}
	
	
	private MessageDecoderResult isValidPacket(IoSession session, IoBuffer in){
		if (in.remaining()< 4)
			return MessageDecoderResult.NEED_DATA;
		
		//取出缓冲区数据
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String a = formatter.format(new Date());
		 
		in.mark();
		byte[] tmp = new byte[in.remaining()];
		in.get(tmp);
		Log.i(TAG, a+" read1:"+ StringUtil.bytesToHexString(tmp));
		in.reset();  //反转缓冲区
		
		EposContext context = EposContext.getContext(session);
		//1读取长度
		int packetLen = (tmp[0]&0x0F)*1000 + (tmp[1]&0x0F)*100 + (tmp[2]&0x0F)*10 + (tmp[3]&0x0F);
		if (tmp.length < packetLen+4)
			return MessageDecoderResult.NEED_DATA;
		else {
			return MessageDecoderResult.OK;
		}
	}
	
	@Override
	/* @title: 交易报文解析
	 * @author: mcb
	 * @Description: ${先检测交易报文是否完整，然后解析接入层报文，再然后解析交易报文}(
	 */
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		
		MessageDecoderResult result = isValidPacket(session, in); 
		if (result == MessageDecoderResult.NOT_OK){
			//非正常包，直接清理数据
			in.clear();
			return true;  
		} else if (result == MessageDecoderResult.NEED_DATA){
			return false;
		}
	//	Log.e(TAG, " read1:"+ StringUtil.bytesToHexString(tmp));
		byte[] tmp = new byte[28];						//4+8+16
		in.get(tmp);
		//Log.e(TAG," read2:"+ StringUtil.bytesToHexString(tmp));
		//int appLen = (tmp[0]&0x0F)*1000 + (tmp[1]&0x0F)*100 + (tmp[2]&0x0F)*10 + (tmp[3]&0x0F);				//2 appLen	
		EposClientPacket msg = new EposClientPacket();
		
		if (msg.decodeFromStream(in)){
			in.clear();
		}
		else {
			in.clear(); //清空数据
			return true;
		}
		
		
		out.write(msg);
		return false;
	}	
}

/*		
in.mark();
byte stx = in.get(); 							//1 tag
int appLen = (int)in.getShort(); 				//2 appLen	
if (in.remaining()<appLen+1){
	in.reset();
	LOGGER.error(this.getClass().getName()+ "doDecode长度错误");
	return false;
}

EposSessionPacket msg = new EposSessionPacket();
msg.stx = stx;									//???????
in.get(msg.rand);								//3		????????????
msg.seq = in.get();								//4		??????????

if ((stx == EposProtocol.C2S_BUSI) || (stx == EposProtocol.S2C_BUSI)){
	int srcLen = (int) in.getShort();			//5 	srcLen
	//Log.i("eeeee", Integer.toString(in.remaining()));
	if (srcLen+1 > in.remaining() || (srcLen>1024) || (srcLen < 24)){ //?ж????????????
		return false;
	}
	in.get();												//6  
	in.get();												//7 
	byte[] psamid = new byte[8];
	in.get(psamid);												//POSCATI	
	msg.setPsamID(StringUtil.bytesToHexString(psamid));			

	byte[] date = new byte[4];
	in.get(date);
	msg.date = StringUtil.bcd2Str(date);							//交易日期

	byte[] time = new byte[3];
	in.get(time);
	msg.time = StringUtil.bcd2Str(time);							//交易代码
	msg.busiNo = ((int)in.get()<<16)+((int)in.get())<<8+in.get();	//交易序号
	msg.busiCode = in.getString(3, charset.newDecoder());			//交易代码
	//msg.busiFlowCodeNum;											//
	byte codeNum = in.get(); 										//流程码个数

	msg.fields.clear();
	for(int i=0;i<codeNum;i++){
		byte cmd = in.get();
		byte flag = (byte)(cmd >>> 6);
		cmd = (byte) (cmd & 0x3F);
		byte op2;
	
		//msg.fields.add()
		//msg.busiFlowCodeList.add(new BusiFlowCode(cmd, hint, flag));
		EposCommand ec = null;
	

		switch(cmd){
			case EposProtocol.CMD2_DISPLAY: 
				ec = new CmdDisplay();
			
				break;
			case EposProtocol.CMD2_PRINT: 
				ec = new CmdPrint();
				break;
		}
	
		if (ec != null){
			if ( flag == EposProtocol.CMD_TWO_BYTE ){
				ec.hintIndex = in.get();
			}else if (flag == EposProtocol.CMD_THREE_BYTE){
				ec.hintIndex = in.get();
				op2 = in.get();
			}
			msg.fields.add(ec);
		}
	} 

	
	int dataLen = (int)in.getShort();	//读取长度
	
	for (int j=0;j<msg.fields.size();j++){
		EposCommand ec = msg.fields.get(j);
			ec.decodeFromBuffer(in);				
	}
	byte[] mac = new byte[8];
	in.get(mac);					//读取MAC?
	in.get();
}
else if(stx == EposProtocol.C2S_LINK){
	in.get();
}

out.write(msg);
int m = in.remaining();
//Log.i("ddd", Integer.toString(m));

if (in.remaining()>0)
	return true;
else
	return false;
	*/