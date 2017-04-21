package com.yifengcom.yfpos.bank;
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.mina.core.buffer.IoBuffer;
import org.json.JSONException;
import net.mfs.util.StringUtil;
import android.util.Log;
import com.yifengcom.yfpos.bank.command.BusiCodeList;
import com.yifengcom.yfpos.bank.command.CmdDisconnect;
import com.yifengcom.yfpos.bank.command.CmdDisplay;
import com.yifengcom.yfpos.bank.command.CmdICResponse;
import com.yifengcom.yfpos.bank.command.CmdPrint;
import com.yifengcom.yfpos.bank.command.CmdReadBusiNo;
import com.yifengcom.yfpos.bank.command.CmdReadPSAMID;
import com.yifengcom.yfpos.bank.command.CmdReadPin;
import com.yifengcom.yfpos.bank.command.CmdReadTERMID;
import com.yifengcom.yfpos.bank.command.CmdReadTrack;
import com.yifengcom.yfpos.bank.command.CmdUpdateAppVer;
import com.yifengcom.yfpos.bank.command.CmdUpdateMenu;
import com.yifengcom.yfpos.bank.command.EposCommand;
import com.yifengcom.yfpos.bank.command.CmdReadBusiCount;  
import com.yifengcom.yfpos.bank.command.CmdReadBankNo;
import com.yifengcom.yfpos.bank.command.CmdReadBankID;
import com.yifengcom.yfpos.bank.command.CmdReadBusiAmount;
import com.yifengcom.yfpos.bank.command.CmdReadDate;
import com.yifengcom.yfpos.bank.command.CmdReadYYYYMM;
import com.yifengcom.yfpos.bank.command.CmdReadCustom;
import com.yifengcom.yfpos.bank.command.CmdCalcMac;
import com.yifengcom.yfpos.bank.command.CmdSignature;
import com.yifengcom.yfpos.bank.command.CmdReadProgram_Ver;
import com.yifengcom.yfpos.bank.command.CmdReadAppVer;
import com.yifengcom.yfpos.bank.command.CmdUpdateTerm;
import com.yifengcom.yfpos.bank.command.CmdUpdate_Caption;
import com.yifengcom.yfpos.bank.command.CmdICControl;
public final class EposClientPacket {      		  	//?
	
	private String TAG = this.getClass().getName();
	private byte[] rand = new byte[4];   	//
	private byte seq;						//
	
	private byte packetType;				//1报文类型 ，接入系统至业务系统
	private byte endFlag;					//2结束标志 
	private String softVersion="3001";				//3程序版本号 
	private String appVersion="30303431";				//4应用版本号 
	private byte callShowFlag= 0x30;			//5来电显示标志
	private String psamID;					//6 密码键盘序列号PSAMID
	@SuppressWarnings("unused")
	private String date;					//7系统日期
	@SuppressWarnings("unused")
	private String time;					//8系统时间
	private int busiNo;						//9交易流水号
	private String busiCode;				//10交易代码
	
	//public BusiFlowCodeList busiFlowCodeList; //11流程代码 
	public BusiCodeList fields;
		
	private ByteBuffer fieldData;
	private String mac;
	
	private Charset charset = Charset.forName("GB2312");
	public EposClientPacket(){
		fieldData = ByteBuffer.allocate(1024);
		fields = new BusiCodeList();
		setMac("0000000000000000");
		resetPacket();
	}
		
	//重置报文
	public void resetPacket(){
		fieldData.clear();
		seq = 0;
	}
			

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
	public String getPsamID() {
		if ((psamID == null) || (psamID.length() !=12))
			return "000000000000";
		else
			return psamID;
	}
	
	public String getASCPsamID() {
		if ((psamID == null) || (psamID.length() !=16))
			return "00000000";
		else
		{
			return StringUtil.HexCharToChar(psamID,"");
		}
	}

	public void setPsamID(String psamid) {
		this.psamID = psamid;
	}

	 
	public String getBusiCode() {
		if ((busiCode == null) || (busiCode.length() != 3)) 
			return "000";
		else
			return busiCode;
	}

	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
	}
	

	public int getBusiNo() {
		return busiNo;
	}

	public void setBusiNo(int busiNo) {
		this.busiNo = busiNo;
	}

	public String getSoftVersion() {
		if ((softVersion == null)||(softVersion.length() != 4))
			return "0000";
		else
			return softVersion;
	}

	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}

	public String getAppVersion() {
		if ((appVersion == null)||(appVersion.length() != 8))
			return "00000000";
		else
			return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
		


	public byte[] getRand() {
		return rand;
	}

	public void setRand(byte[] rand) {
		this.rand = rand;
	}

	public byte getSeq() {
		return seq;
	}

	public void setSeq(byte i) {
		this.seq = i;
	}
		
	public void setTime(String value){
		this.time = value;
	}
		
	public void setDate(String value){
		this.date = value;
	}

	public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		return sdf.format(new Date());
	}
	
	public String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
		
	public boolean decodeFromStream(IoBuffer in) throws CharacterCodingException, JSONException{
		//需要区分是接入平台至业务平台还是业务到成至接入平台
			
		in.mark();
		byte[] tmp = new byte[in.remaining()];
		in.get(tmp);
		String smsg=  StringUtil.bytesToHexString(tmp);
		
		Log.e("data",smsg);
		
		in.reset();  
		
		this.packetType = in.get();									//1
		this.endFlag = in.get();									//2 结束标志
		
		byte[] psamid = new byte[6];
		/*
		if (this.packetType == EposProtocol.C2S) {
			byte[] softVersion = new byte[2];
			in.get(softVersion);
			this.softVersion = StringUtil.bcd2Str(softVersion);		//3程序版本号
			
			byte[] appVersion = new byte[4];
			in.get(appVersion);
			this.appVersion = StringUtil.bcd2Str(appVersion);		//4应用版本号
			
			byte flag = in.get();									//5来电显示标志
			this.callShowFlag = String.format("%s", flag);	
			
			in.get(psamid);											//6POSCATI	
			this.setPsamID(StringUtil.bytesToHexString(psamid));
		}else*/
		{
			in.get(psamid);											//6POSCATI	
			this.setPsamID(StringUtil.bytesToHexString(psamid));
			
			byte[] date = new byte[4];							
			in.get(date);
			this.setDate(StringUtil.bcd2Str(date));					//7交易日期
			
			byte[] time = new byte[3];
			in.get(time);
			this.setTime(StringUtil.bcd2Str(time));					//8交易代码
		}
				
			
		this.busiNo = ((0x0000FF & in.get())<<16) + ((0x0000FF & in.get())<<8) + ((0x0000FF & in.get()));	//9 交易序号
		this.busiCode = in.getString(3, charset.newDecoder());			//10交易代码
		byte codeNum = in.get(); 										//11流程码个数

		this.fields.clear();
		boolean bmac=false;
		EposCommand ec;
		for(int i=0;i<codeNum;i++){
			byte cmd = in.get();
			byte flag = (byte)((cmd & 0xFF) >>> 6);
			cmd = (byte) (cmd & 0x3F);
			@SuppressWarnings("unused")
			byte op2;

			ec = null;

			switch(cmd){					
				case EposProtocol.CMD2_READ_PSAMID://2  
					ec = new CmdReadPSAMID();
					break;	
				case EposProtocol.CMD2_READ_CARDID://3
					ec = new CmdReadBankID();
					break;
				case EposProtocol.CMD2_READ_TRACK://4
					ec = new CmdReadTrack();
					break;
				case EposProtocol.CMD2_READ_PIN://5 
					ec = new CmdReadPin();
				break;
				case EposProtocol.CMD2_READ_CAPACITY://6
					ec = new CmdReadBusiCount();
					break;
				case EposProtocol.CMD2_READ_AMOUNT://7
					ec = new CmdReadBusiAmount();
					break;
				case EposProtocol.CMD2_READ_BANKNO://8
					ec = new CmdReadBankNo();
					break;
				case EposProtocol.CMD2_READ_BUSINO://9
					ec = new CmdReadBusiNo();
					break;
				case EposProtocol.CMD2_READ_YYYYMMDD://10
					ec = new CmdReadDate();
					break;
				case EposProtocol.CMD2_READ_YYYYMM://11 
					ec = new CmdReadYYYYMM();
					break;
				case EposProtocol.CMD2_READ_CUSTOM://12
					ec = new CmdReadCustom();
					break;
				case EposProtocol.CMD2_CALC_MAC://13
					bmac=true;
					ec = new CmdCalcMac();
					break;
				case EposProtocol.CMD2_SIGNATURE://54
					ec = new CmdSignature();
					break;
				case EposProtocol.CMD2_READ_PROGRAM_VER://16
					ec = new CmdReadProgram_Ver();
					break;
				case EposProtocol.CMD2_READ_APP_VER://17
					ec = new CmdReadAppVer();
					break;
				case EposProtocol.CMD2_READ_TERMID://18
					ec = new CmdReadTERMID();
					break;
				case EposProtocol.CMD2_UPDATE_TERM://22
					ec = new CmdUpdateTerm();
					break;
				case EposProtocol.CMD2_UPDATE_PSAM://23
					ec = new CmdUpdateTerm();
					break;
				case EposProtocol.CMD2_UPDATE_CAPTION://27
					ec = new CmdUpdate_Caption();
					break;
				case EposProtocol.CMD2_UPDATE_PRINT://28  
					ec = new CmdUpdateMenu();
					break;
				case EposProtocol.CMD2_UPDATE_MENU://24
					ec = new CmdUpdateMenu();
					break;
				case EposProtocol.CMD2_PRINT: //33
					ec = new CmdPrint();
					break;
				case EposProtocol.CMD2_DISPLAY: //34
					ec = new CmdDisplay();
					break;
				//case EposProtocol.CMD2_SELE_MENU: //34 CMD2_IC_CONTROL
				//	ec = new CmdSeleMenu();
				//	break;	
				case EposProtocol.CMD2_HANGUP:
					ec = new CmdDisconnect();
					break;
				case EposProtocol.CMD2_IC_CONTROL: //44 
					ec = new CmdICControl();
					break; 
				case EposProtocol.CMD2_IC_RESPONSE:
					ec = new CmdICResponse();
					break;
				case EposProtocol.CMD2_UPDATE_APP_VER://62	
					ec = new CmdUpdateAppVer();
					break;
			}
		
			if (ec != null){
				if ( flag == EposProtocol.CMD_TWO_BYTE ){
					ec.setHintIndex(in.get());
				}else if (flag == EposProtocol.CMD_THREE_BYTE){
					ec.setHintIndex(in.get());
					op2 = in.get();
				}
				this.fields.add(ec);
			}
		} 
	
		@SuppressWarnings("unused")
		int dataLen = (int)in.getShort();	//读取长度
		for (int j=0;j<this.fields.size();j++){
			ec = this.fields.get(j);
				ec.decodeFromBuffer(in);				
		} 
		
		if (in.remaining() >=8){
			ec = new CmdCalcMac();
			ec.decodeFromBuffer(in);
			this.fields.add(ec);
		}
		return true;
	}
		
	public IoBuffer encodeToStream() throws IOException{
		IoBuffer buf = IoBuffer.allocate(1024);
		buf.clear();
			
		//流程代码及流程代码数据
		ByteArrayOutputStream codes = new ByteArrayOutputStream();
		ByteArrayOutputStream fieldData = new ByteArrayOutputStream();
				
		//1生成流程代码及流程代码数据
		codes.write((byte) this.fields.size());  
		for (EposCommand cl: this.fields){
			codes.write(cl.getCodeHex());
			byte[] tmp = cl.getFieldData();
			if (tmp != null){
				fieldData.write(tmp);
				Log.d(TAG, String.format("CMD: [%d], DATALEN=%d", cl.getCommand(), tmp.length ));
			}else {
				Log.d(TAG, String.format("CMD: [%d], DATALEN=%d", cl.getCommand(), 0 ));
			}
			
		}
					
		//if (this.stx == EposProtocol.C2S_BUSI){
			buf.put(EposProtocol.C2S); 									//1  报文类型 
			buf.put(this.endFlag);										//2  结束标志
					
			buf.put(StringUtil.hexStringToByte(this.getSoftVersion()));	//3程序版本号 
			buf.put(StringUtil.hexStringToByte(this.getAppVersion()));	//4应用版本号
			buf.put(this.callShowFlag);									//5来电显示标志
			buf.put(StringUtil.hexStringToByte(this.getPsamID()));		//6	PSAM
		/*}else{
			buf.put(EposProtocol.S2C);									//1  报文类型 
			buf.put(this.endFlag);										//2  结束标志
			buf.put(StringUtil.hexStringToByte(this.getPsamID()));		//6	PSAM
			buf.put(StringUtil.hexStringToByte(this.getDate()));		//7交易日期
			buf.put(StringUtil.hexStringToByte(this.getTime()));		//8交易时间
		}*/

		int busino = this.getBusiNo();
//		buf.put((byte)((busino >> 16) % 256));							//9交易流水号	
//		buf.put((byte)((busino >> 8) % 256));
//		buf.put((byte)(busino  % 256));
		buf.put(StringUtil.str2Bcd(String.format("%06d", busiNo)));	
		
		buf.put(this.getBusiCode().getBytes());							//10交易代码
		buf.put(codes.toByteArray());									//11写入流程代码
			
					
		//12流程代码数据长度及数据 
		byte[] a = fieldData.toByteArray();
		int fieldDataLen = a.length;  
		buf.put((byte) (fieldDataLen/ 256));
		buf.put((byte) (fieldDataLen % 256));
		if (a != null)
			buf.put(a);
		
		buf.flip();
		return buf;
	}
		
}

